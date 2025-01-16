"use client";

import {
  DndContext,
  DragEndEvent,
  DragOverlay,
  DragStartEvent,
  PointerSensor,
  rectIntersection,
  useSensor,
  useSensors,
} from "@dnd-kit/core";
import { SortableContext, arrayMove, rectSortingStrategy } from "@dnd-kit/sortable";
import { Input, message } from "antd";
import axios from "axios";

import React, { useState } from "react";

import ImageWrapper from "@/components/ImageWrapper";

import { Item } from "@/types/Item";

import { getAnonymousId } from "@/utils/getAnonymousId";
import { getApiBaseUrl } from "@/utils/getApiBaseUrl";
import { getImageUrl } from "@/utils/getImageUrl";

import DraggableItem from "./components/DraggableItem";
import SortableTier from "./components/SortableTier";
import Tier from "./components/Tier";

// "URL生成" 等が必要な場合、外部から呼び出せるように onSave() or onGenerateUrl() をpropsで受け取る
// ここでは「onSave」で公開/非公開を指定できる形にします
export interface TierEditorProps {
  initialTierName: string;
  // 初期の Tier 構造 (閲覧時ならバックエンドから取得済み / 新規なら空 or 既定のTier)
  initialTiers: Record<string, { name: string; items: Item[] }>;
  availableItems: Item[];
  categoryId: string;
  categoryName: string;
  categoryImageUrl: string;
}

// "levels" の型例
export interface Level {
  name: string;
  orderIndex: number;
  items: {
    itemId: string;
    orderIndex: number;
  }[];
}

/**
 * TierEditor コンポーネント
 * - DnDContext & SortableContext を内部に含む共通コンポーネント
 * - 新規/閲覧の違いは上位コンポーネントから渡すpropsで制御
 */
const TierEditor: React.FC<TierEditorProps> = ({
  initialTierName,
  initialTiers,
  availableItems,
  categoryId,
  categoryName,
  categoryImageUrl,
}) => {
  const [tierName, setTierName] = useState<string>(initialTierName);

  // ステート: tiers, tierOrder, ドラッグ中アイテムなど
  const [tiers, setTiers] = useState(initialTiers);

  // Tierの順序を保持する配列 (オブジェクトのkeys() だけでは順序の管理が不安定なため)
  const [tierOrder, setTierOrder] = useState<string[]>(Object.keys(initialTiers));

  // 未割り当てアイテム
  const [unassignedItems, setUnassignedItems] = useState<Item[]>(availableItems);

  // ドラッグ中のID (Tier or Item)
  const [activeTierId, setActiveTierId] = useState<string | null>(null);
  const [activeItemId, setActiveItemId] = useState<string | null>(null);

  // DnD Sensors
  const sensors = useSensors(useSensor(PointerSensor, { activationConstraint: { distance: 5 } }));

  const [generatedUrl, setGeneratedUrl] = useState<string | null>(null);
  const [isGenerating, setIsGenerating] = useState(false);

  const anonymousId = getAnonymousId();

  // Tier の色 (例)
  const tierColors: Record<string, string> = {
    Tier1: "#2A1536",
    Tier2: "#3A1A1A",
    Tier3: "#3A2A15",
    Tier4: "#152641",
    Tier5: "#153A29",
    default: "#2D2D2D",
    unassigned: "#8A8A8A",
  };

  /**
   * ドラッグ開始
   */
  const handleDragStart = (event: DragStartEvent) => {
    const { active } = event;
    const activeId = active.id as string;

    if (tierOrder.includes(activeId)) {
      // Tier全体をドラッグ
      setActiveTierId(activeId);
      setActiveItemId(null);
    } else {
      // アイテムをドラッグ
      setActiveItemId(activeId);
      setActiveTierId(null);
    }
  };

  /**
   * ドラッグ終了
   */
  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;
    if (!active || !over) {
      resetActiveState();
      return;
    }

    const activeId = active.id as string;
    const overId = over.id as string;

    // Tier の並び替え
    if (activeTierId && tierOrder.includes(activeTierId)) {
      if (activeId !== overId) {
        const oldIndex = tierOrder.indexOf(activeId);
        const newIndex = tierOrder.indexOf(overId);
        setTierOrder((prev) => arrayMove(prev, oldIndex, newIndex));
      }
      resetActiveState();
      return;
    }

    // アイテムの並び替え
    if (activeItemId) {
      const sourceTierKey = findTierByItem(activeItemId) ?? "unassigned";
      const destinationTierKey = findTierByItem(overId) ?? overId;

      if (sourceTierKey === destinationTierKey) {
        // 同一Tier内の並び替え
        if (sourceTierKey === "unassigned") {
          // 未割り当て同士の並び替えをする場合は arrayMove を実装する
          resetActiveState();
          return;
        } else {
          const oldIndex = tiers[sourceTierKey].items.findIndex((itm) => itm.id === activeItemId);
          const newIndex = tiers[sourceTierKey].items.findIndex((itm) => itm.id === overId);
          if (oldIndex >= 0 && newIndex >= 0 && oldIndex !== newIndex) {
            setTiers((prev) => ({
              ...prev,
              [sourceTierKey]: {
                ...prev[sourceTierKey],
                items: arrayMove(prev[sourceTierKey].items, oldIndex, newIndex),
              },
            }));
          }
        }
      } else {
        // 異なるTierへ移動
        const movingItem = getItemById(activeItemId);
        if (!movingItem) {
          resetActiveState();
          return;
        }

        // 元のTier or 未割り当てから削除
        if (sourceTierKey === "unassigned") {
          setUnassignedItems((prev) => prev.filter((it) => it.id !== activeItemId));
        } else {
          setTiers((prev) => ({
            ...prev,
            [sourceTierKey]: {
              ...prev[sourceTierKey],
              items: prev[sourceTierKey].items.filter((it) => it.id !== activeItemId),
            },
          }));
        }

        // 先のTier or 未割り当てへ追加
        if (destinationTierKey === "unassigned") {
          setUnassignedItems((prev) => [...prev, movingItem]);
        } else if (tiers[destinationTierKey]) {
          const destItems = [...tiers[destinationTierKey].items];
          const overIndex = destItems.findIndex((it) => it.id === overId);
          if (overIndex >= 0) {
            destItems.splice(overIndex, 0, movingItem);
          } else {
            destItems.push(movingItem);
          }

          setTiers((prev) => ({
            ...prev,
            [destinationTierKey]: {
              ...prev[destinationTierKey],
              items: destItems,
            },
          }));
        }
      }
    }

    resetActiveState();
  };

  /** アクティブ状態をリセット */
  const resetActiveState = () => {
    setActiveTierId(null);
    setActiveItemId(null);
  };

  /** Tier 内に itemId があれば TierKey を返す */
  const findTierByItem = (itemId: string): string | undefined => {
    const keys = Object.keys(tiers);
    for (const key of keys) {
      if (tiers[key].items.some((it) => it.id === itemId)) {
        return key;
      }
    }
    return undefined;
  };

  /** 全 Tier + 未割り当て から Item を探す */
  const getItemById = (itemId: string): Item | undefined => {
    const fromUnassigned = unassignedItems.find((it) => it.id === itemId);
    if (fromUnassigned) return fromUnassigned;

    for (const tierKey in tiers) {
      const found = tiers[tierKey].items.find((it) => it.id === itemId);
      if (found) return found;
    }
    return undefined;
  };

  const generateTierUrl = async (isPublic: boolean) => {
    if (!tierName.trim()) {
      message.error("Tier全体の名前を入力してください");
      return;
    }

    setIsGenerating(true);
    try {
      const levels = tierOrder.map((tierKey, index) => ({
        name: tiers[tierKey].name,
        orderIndex: index + 1,
        items: tiers[tierKey].items.map((item, itemIndex) => ({
          itemId: item.id,
          orderIndex: itemIndex + 1,
        })),
      }));

      const response = await axios.post(`${getApiBaseUrl()}/user-tiers`, {
        anonymousId,
        categoryId,
        name: tierName,
        isPublic,
        levels,
      });

      const data = response.data;
      setGeneratedUrl(data);

      navigator.clipboard.writeText(data);
      message.success("URLが生成されクリップボードにコピーされました");
    } catch (error: unknown) {
      if (axios.isAxiosError(error)) {
        if (error.response) {
          message.error(`URLの生成に失敗しました (ステータス: ${error.response.status})`);
        } else {
          message.error("サーバーからのレスポンスがありません");
        }
      } else if (error instanceof Error) {
        message.error(`エラー: ${error.message}`);
      } else {
        message.error("不明なエラーが発生しました");
      }
    } finally {
      setIsGenerating(false);
    }
  };

  return (
    <DndContext
      sensors={sensors}
      collisionDetection={rectIntersection}
      onDragStart={handleDragStart}
      onDragEnd={handleDragEnd}
    >
      <div className="text-center mb-8">
        <div className="flex justify-center items-center p-6 mb-6">
          <div
            className="flex items-center justify-center max-w-full max-h-full rounded-lg shadow-md
                        overflow-hidden bg-transparent"
          >
            <ImageWrapper
              src={getImageUrl(categoryImageUrl)}
              alt={categoryName}
              className="object-cover w-full h-full"
            />
          </div>

          <h2 className="text-gray-300 text-3xl">{categoryName}</h2>
        </div>
        <label htmlFor="tierNameInput" className="block text-lg font-medium text-gray-300 mb-2">
          Your Tier Name
        </label>
        <Input
          id="tierNameInput"
          value={tierName}
          onChange={(e) => setTierName(e.target.value)}
          placeholder="例: 最強キャラランキング"
          style={{
            width: "100%",
            height: "45px",
            fontSize: "16px",
            backgroundColor: "#444",
            color: "#fff",
            borderRadius: "6px",
            border: "1px solid #666",
            padding: "0 10px",
          }}
          className="focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 transition"
        />
        {/*</div>*/}
      </div>

      {/* Tiers の SortableContext */}
      <SortableContext items={tierOrder} strategy={rectSortingStrategy}>
        {tierOrder.map((tierKey) => (
          <SortableTier
            key={tierKey}
            id={tierKey}
            tierKey={tierKey}
            name={tiers[tierKey].name}
            items={tiers[tierKey].items}
            onNameChange={(newName) =>
              setTiers((prev) => ({
                ...prev,
                [tierKey]: {
                  ...prev[tierKey],
                  name: newName,
                },
              }))
            }
            backgroundColor={tierColors[tierKey] || tierColors.default}
          />
        ))}
      </SortableContext>

      <div
        className="mt-8 p-4 rounded-md shadow-md"
        style={{
          backgroundColor: tierColors.unassigned,
          minHeight: "150px",
        }}
      >
        <h3 className="text-lg font-semibold mb-4" style={{ color: "#333" }}>
          未割り当てアイテム
        </h3>
        <div className="flex gap-4 flex-wrap">
          {unassignedItems.map(
            (
              item, // unassignedItems を使用
            ) => (
              <DraggableItem key={item.id} item={item} />
            ),
          )}
        </div>
      </div>

      <div className="mt-8 text-center">
        <button
          onClick={() => generateTierUrl(true)}
          disabled={isGenerating}
          className={`mb-4 mr-4 px-6 py-3 rounded-lg font-semibold text-base transition duration-200 ${
            isGenerating
              ? "bg-gray-700 text-gray-500 cursor-not-allowed"
              : "bg-gray-800 text-white hover:bg-gray-700"
          }`}
        >
          公開してURL生成
        </button>
        <button
          onClick={() => generateTierUrl(false)}
          disabled={isGenerating}
          className={`px-6 py-3 rounded-lg font-semibold text-base transition duration-200 ${
            isGenerating
              ? "bg-gray-700 text-gray-500 cursor-not-allowed"
              : "bg-gray-800 text-white hover:bg-gray-700"
          }`}
        >
          非公開でURL生成
        </button>
        {generatedUrl && (
          <div className="mt-6">
            <p className="text-gray-400">
              生成されたURL:{" "}
              <a
                href={generatedUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="text-blue-400 underline hover:text-blue-300"
              >
                {generatedUrl}
              </a>
            </p>
          </div>
        )}
      </div>

      {/* ドラッグオーバーレイ (Tier or Item) */}
      <DragOverlay>
        {activeTierId ? (
          <Tier
            id={activeTierId}
            name={tiers[activeTierId].name}
            items={tiers[activeTierId].items}
            onNameChange={(newName) =>
              setTiers((prev) => ({
                ...prev,
                [activeTierId]: {
                  ...prev[activeTierId],
                  name: newName,
                },
              }))
            }
            backgroundColor={tierColors[activeTierId] || tierColors.default}
          />
        ) : activeItemId ? (
          (() => {
            // DraggableItem のオーバーレイ表示
            const item = getItemById(activeItemId);
            return item ? <DraggableItem item={item} isOverlay /> : null;
          })()
        ) : null}
      </DragOverlay>
    </DndContext>
  );
};

export default TierEditor;
