"use client";

import {
  DndContext,
  DragEndEvent,
  DragOverEvent,
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
import UnassignedArea from "./components/UnassignedArea";

export interface TierEditorProps {
  initialTierName: string;
  initialTiers: Record<string, { name: string; items: Item[] }>;
  availableItems: Item[];
  categoryId: string;
  categoryName: string;
  categoryImageUrl: string;
}

export interface Level {
  name: string;
  orderIndex: number;
  items: {
    itemId: string;
    orderIndex: number;
  }[];
}

const TierEditor: React.FC<TierEditorProps> = ({
  initialTierName,
  initialTiers,
  availableItems,
  categoryId,
  categoryName,
  categoryImageUrl,
}) => {
  const [tierName, setTierName] = useState<string>(initialTierName);
  const [tiers, setTiers] = useState(initialTiers);
  const [tierOrder, setTierOrder] = useState<string[]>(Object.keys(initialTiers));
  const [unassignedItems, setUnassignedItems] = useState<Item[]>(availableItems);
  const [activeTierId, setActiveTierId] = useState<string | null>(null);
  const [activeItemId, setActiveItemId] = useState<string | null>(null);
  const [generatedUrl, setGeneratedUrl] = useState<string | null>(null);
  const [isGenerating, setIsGenerating] = useState(false);
  const anonymousId = getAnonymousId();
  const [dropPreview, setDropPreview] = useState<{
    tierId: string;
    index: number;
  } | null>(null);

  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: { distance: 5 },
    }),
  );

  const tierColors: Record<string, string> = {
    Tier1: "#2A1536",
    Tier2: "#3A1A1A",
    Tier3: "#3A2A15",
    Tier4: "#152641",
    Tier5: "#153A29",
    default: "#2D2D2D",
    unassigned: "#8A8A8A",
  };

  const handleDragOver = (event: DragOverEvent) => {
    const { active, over } = event;
    if (!active || !over) return;

    const activeId = active.id as string;
    const overId = over.id as string;

    // Tierのドラッグ中は何もしない
    if (tierOrder.includes(activeId)) {
      setDropPreview(null);
      return;
    }

    // ドロップ先の決定
    let targetTierId: string;
    let targetIndex: number;

    if (overId === "unassigned-area") {
      // 未割り当てエリアへのドロップ
      targetTierId = "unassigned";
      targetIndex = unassignedItems.length;
    } else {
      // アイテムまたはTierへのドロップ
      const overTier = findTierByItem(overId);
      if (overTier) {
        // アイテムへのドロップ
        targetTierId = overTier;
        const items = tiers[overTier].items;
        targetIndex = items.findIndex((item) => item.id === overId);
      } else if (tiers[overId]) {
        // Tierへのドロップ
        targetTierId = overId;
        targetIndex = tiers[overId].items.length;
      } else {
        // 未割り当てエリア内のアイテムへのドロップ
        targetTierId = "unassigned";
        targetIndex = unassignedItems.findIndex((item) => item.id === overId);
        if (targetIndex === -1) {
          targetIndex = unassignedItems.length;
        }
      }
    }

    setDropPreview({ tierId: targetTierId, index: targetIndex });
  };

  const handleDragStart = (event: DragStartEvent) => {
    const { active } = event;
    const activeId = active.id as string;

    if (tierOrder.includes(activeId)) {
      setActiveTierId(activeId);
      setActiveItemId(null);
    } else {
      setActiveItemId(activeId);
      setActiveTierId(null);
    }
  };

  const handleDragEnd = (event: DragEndEvent) => {
    setDropPreview(null);
    const { active, over } = event;
    if (!active || !over) {
      resetActiveState();
      return;
    }

    const activeId = active.id as string;
    const overId = over.id as string;

    // Tier間の移動
    if (activeTierId && tierOrder.includes(activeTierId)) {
      if (activeId !== overId) {
        const oldIndex = tierOrder.indexOf(activeId);
        const newIndex = tierOrder.indexOf(overId);
        setTierOrder((prev) => arrayMove(prev, oldIndex, newIndex));
      }
      resetActiveState();
      return;
    }

    // アイテムの移動
    if (activeItemId) {
      const sourceTierKey = findTierByItem(activeItemId) ?? "unassigned";
      let destinationTierKey = findTierByItem(overId);

      // 移動先の決定
      if (overId === "unassigned-area") {
        destinationTierKey = "unassigned";
      } else if (!destinationTierKey && !tiers[overId]) {
        // 未割り当てエリア内のアイテムへの移動
        destinationTierKey = "unassigned";
      } else if (!destinationTierKey) {
        destinationTierKey = overId;
      }

      // 同一エリア内の移動
      if (sourceTierKey === destinationTierKey) {
        if (sourceTierKey === "unassigned") {
          const items = unassignedItems;
          const oldIndex = items.findIndex((item) => item.id === activeItemId);
          const newIndex =
            overId === "unassigned-area"
              ? items.length
              : items.findIndex((item) => item.id === overId);

          if (oldIndex >= 0 && (newIndex >= 0 || overId === "unassigned-area")) {
            setUnassignedItems((prev) =>
              arrayMove(prev, oldIndex, newIndex >= 0 ? newIndex : prev.length - 1),
            );
          }
        } else {
          const items = tiers[sourceTierKey].items;
          const oldIndex = items.findIndex((item) => item.id === activeItemId);
          const newIndex = items.findIndex((item) => item.id === overId);
          if (oldIndex >= 0 && newIndex >= 0) {
            setTiers((prev) => ({
              ...prev,
              [sourceTierKey]: {
                ...prev[sourceTierKey],
                items: arrayMove(items, oldIndex, newIndex),
              },
            }));
          }
        }
        resetActiveState();
        return;
      }

      const movingItem = getItemById(activeItemId);

      if (!movingItem) {
        resetActiveState();
        return;
      }

      // 移動元からアイテムを削除
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

      // 移動先にアイテムを追加
      if (destinationTierKey === "unassigned") {
        setUnassignedItems((prev) => {
          if (overId === "unassigned-area") {
            return [...prev, movingItem];
          }
          const insertIndex = prev.findIndex((it) => it.id === overId);
          if (insertIndex >= 0) {
            const newItems = [...prev];
            newItems.splice(insertIndex, 0, movingItem);
            return newItems;
          }
          return [...prev, movingItem];
        });
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

    resetActiveState();
  };
  const resetActiveState = () => {
    setActiveTierId(null);
    setActiveItemId(null);
  };

  const findTierByItem = (itemId: string): string | undefined => {
    const keys = Object.keys(tiers);
    for (const key of keys) {
      if (tiers[key].items.some((it) => it.id === itemId)) {
        return key;
      }
    }
    return undefined;
  };

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

      const response = await axios.post(`${getApiBaseUrl()}/tiers`, {
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
      onDragOver={handleDragOver}
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
      </div>

      <SortableContext items={tierOrder} strategy={rectSortingStrategy}>
        {tierOrder.map((tierKey) => (
          <SortableTier
            key={tierKey}
            id={tierKey}
            tierKey={tierKey}
            name={tiers[tierKey].name}
            items={tiers[tierKey].items}
            dropPreview={dropPreview?.tierId === tierKey ? { index: dropPreview.index } : null}
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

      <UnassignedArea
        items={unassignedItems}
        backgroundColor={tierColors.unassigned}
        dropPreview={dropPreview?.tierId === "unassigned" ? { index: dropPreview.index } : null}
      />

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

      <DragOverlay
        dropAnimation={{
          duration: 200,
          easing: "cubic-bezier(0.18, 0.67, 0.6, 1.22)",
        }}
      >
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
            const item = getItemById(activeItemId);
            return item ? <DraggableItem item={item} isOverlay /> : null;
          })()
        ) : null}
      </DragOverlay>
    </DndContext>
  );
};

export default TierEditor;
