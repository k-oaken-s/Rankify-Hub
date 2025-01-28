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
import { Input } from "antd";
import axios from "axios";
import { PlusCircleIcon, XCircleIcon } from "lucide-react";

import React, { useState } from "react";

import ImageWrapper from "@/components/ImageWrapper";
import ShowNotification from "@/components/ShowNotification";

import { Item } from "@/types/Item";

import { getAnonymousId } from "@/utils/getAnonymousId";
import { getApiBaseUrl } from "@/utils/getApiBaseUrl";
import { getImageUrl } from "@/utils/getImageUrl";

import DraggableItem from "./components/DraggableItem";
import SortableTier from "./components/SortableTier";
import Tier from "./components/Tier";
import UnassignedArea from "./components/UnassignedArea";

export const TIER_PRESETS = {
  tier: {
    label: "Tier",
    tiers: {
      Tier1: { name: "Tier 1", items: [] },
      Tier2: { name: "Tier 2", items: [] },
      Tier3: { name: "Tier 3", items: [] },
      Tier4: { name: "Tier 4", items: [] },
      Tier5: { name: "Tier 5", items: [] },
    },
  },
  rank: {
    label: "ランク",
    tiers: {
      Tier1: { name: "S", items: [] },
      Tier2: { name: "A", items: [] },
      Tier3: { name: "B", items: [] },
      Tier4: { name: "C", items: [] },
      Tier5: { name: "D", items: [] },
    },
  },
};

const RANK_NAMES = ["S", "A", "B", "C", "D", "E", "F", "G", "H", "I"];

export interface TierEditorProps {
  initialTierName: string;
  initialTiers?: Record<string, { name: string; items: Item[] }>;
  availableItems: Item[];
  categoryId: string;
  categoryName: string;
  categoryImageUrl: string;
  isViewMode?: boolean;
}

export interface Level {
  name: string;
  orderIndex: number;
  items: {
    itemId: string;
    orderIndex: number;
  }[];
}

interface TierData {
  name: string;
  items: Item[];
}

const TierEditor: React.FC<TierEditorProps> = ({
  initialTierName,
  initialTiers,
  availableItems,
  categoryId,
  categoryName,
  categoryImageUrl,
  isViewMode = false,
}) => {
  const [tierName, setTierName] = useState<string>(initialTierName);
  const [tierOrder, setTierOrder] = useState<string[]>(
    Object.keys(initialTiers || TIER_PRESETS.tier.tiers),
  );
  const [unassignedItems, setUnassignedItems] = useState<Item[]>(availableItems);
  const [activeTierId, setActiveTierId] = useState<string | null>(null);
  const [activeItemId, setActiveItemId] = useState<string | null>(null);
  const [generatedUrl, setGeneratedUrl] = useState<string | null>(null);
  const [isGenerating, setIsGenerating] = useState(false);
  const [selectedPreset, setSelectedPreset] = useState<keyof typeof TIER_PRESETS>("tier");
  const anonymousId = getAnonymousId();
  const [dropPreview, setDropPreview] = useState<{
    tierId: string;
    index: number;
  } | null>(null);
  const [tiers, setTiers] = useState<Record<string, TierData>>(
    initialTiers || TIER_PRESETS.tier.tiers,
  );

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

  const handlePresetChange = (value: keyof typeof TIER_PRESETS) => {
    if (isViewMode) return;

    setSelectedPreset(value);

    const allItems = [...unassignedItems];
    Object.values(tiers).forEach((tier) => {
      allItems.push(...tier.items);
    });

    const presetTiers = TIER_PRESETS[value].tiers;
    setTiers(presetTiers);
    setTierOrder(Object.keys(presetTiers));
    setUnassignedItems(allItems);
  };

  const generateNewTierName = () => {
    if (isViewMode) {
      const numbers = Object.values(tiers)
        .map((tier) => {
          const match = tier.name.match(/\d+/);
          return match ? parseInt(match[0], 10) : 0;
        })
        .filter((num) => !isNaN(num));

      return `Tier ${numbers.length > 0 ? Math.max(...numbers) + 1 : 1}`;
    }

    const existingNames = Object.values(tiers).map((tier) => tier.name);

    if (selectedPreset === "rank") {
      const unusedRank = RANK_NAMES.find((rank) => !existingNames.includes(rank));
      return unusedRank || RANK_NAMES[RANK_NAMES.length - 1];
    } else {
      const usedNumbers = existingNames
        .map((name) => {
          const match = name.match(/\d+/);
          return match ? parseInt(match[1], 10) : 0;
        })
        .filter((num) => !isNaN(num));

      let newNumber = 1;
      while (usedNumbers.includes(newNumber)) {
        newNumber++;
      }
      return `Tier ${newNumber}`;
    }
  };

  const handleAddTier = () => {
    if (tierOrder.length >= 10) return;

    const maxTierNumber = Math.max(
      ...tierOrder.map((tierId) => {
        const match = tierId.match(/\d+/);
        return match ? parseInt(match[0], 10) : 0;
      }),
    );

    let newTierId = `Tier${maxTierNumber + 1}`;
    const newTierName = generateNewTierName();

    if (tiers[newTierId]) {
      let counter = maxTierNumber + 1;
      while (tiers[`Tier${counter}`]) {
        counter++;
      }
      newTierId = `Tier${counter}`;
    }

    setTiers((prev: Record<string, TierData>) => ({
      ...prev,
      [newTierId]: { name: newTierName, items: [] },
    }));

    setTierOrder((prev) => [...prev, newTierId]);
  };

  const handleRemoveTier = (tierId: string) => {
    if (Object.keys(tiers).length <= 2) return;

    const itemsToMove = tiers[tierId]?.items || [];
    setUnassignedItems((prev) => [...prev, ...itemsToMove]);

    const newTiers = { ...tiers };
    delete newTiers[tierId];
    setTiers(newTiers);

    setTierOrder((prev) => prev.filter((id) => id !== tierId));
  };

  const handleDragOver = (event: DragOverEvent) => {
    const { active, over } = event;
    if (!active || !over) {
      setDropPreview(null);
      return;
    }

    const activeId = active.id as string;
    const overId = over.id as string;

    if (tierOrder.includes(activeId)) {
      setDropPreview(null);
      return;
    }

    let targetTierId: string;
    let targetIndex: number;

    if (overId === "unassigned-area") {
      targetTierId = "unassigned";
      targetIndex = unassignedItems.length;
    } else {
      const overTierId = findTierByItem(overId);
      if (overTierId) {
        targetTierId = overTierId;
        targetIndex = tiers[overTierId].items.findIndex((item) => item.id === overId);
      } else if (tiers[overId]) {
        targetTierId = overId;
        targetIndex = tiers[overId].items.length;
      } else {
        targetTierId = "unassigned";
        targetIndex = unassignedItems.findIndex((item) => item.id === overId);
        if (targetIndex === -1) targetIndex = unassignedItems.length;
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

    if (activeTierId && tierOrder.includes(activeTierId)) {
      if (activeId !== overId) {
        const oldIndex = tierOrder.indexOf(activeId);
        const newIndex = tierOrder.indexOf(overId);
        setTierOrder((prev) => arrayMove(prev, oldIndex, newIndex));
      }
      resetActiveState();
      return;
    }

    if (activeItemId) {
      const sourceTierKey = findTierByItem(activeItemId) ?? "unassigned";
      let destinationTierKey = findTierByItem(overId);

      if (overId === "unassigned-area") {
        destinationTierKey = "unassigned";
      } else if (!destinationTierKey && !tiers[overId]) {
        destinationTierKey = "unassigned";
      } else if (!destinationTierKey) {
        destinationTierKey = overId;
      }

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
      ShowNotification("Tier名を入力してください", "error");
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

      const tierId = response.data;
      const generatedUrl = `${window.location.origin}/categories/${categoryId}/tiers/${tierId}`;

      setGeneratedUrl(generatedUrl);
      await navigator.clipboard.writeText(generatedUrl);
      ShowNotification("URLが生成されクリップボードにコピーされました");
    } catch (error) {
      ShowNotification("URLの生成に失敗しました", "error");
    } finally {
      setIsGenerating(false);
    }
  };

  const TierNameInput = ({
    value,
    onChange,
  }: {
    value: string;
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  }) => (
    <div className="flex items-center gap-4 flex-1 max-w-xl">
      <label htmlFor="tierNameInput" className="text-gray-300 font-medium whitespace-nowrap">
        Tier Name
      </label>
      <Input
        id="tierNameInput"
        value={value}
        onChange={onChange}
        placeholder="Your Tier Name"
        className="w-full h-10 px-3 text-base rounded-lg
                 bg-gray-700 text-white placeholder-gray-400
                 border border-gray-600
                 hover:bg-gray-600 hover:border-indigo-500
                 focus:bg-gray-600 focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500
                 transition-all duration-200 ease-in-out"
      />
    </div>
  );

  return (
    <DndContext
      sensors={sensors}
      collisionDetection={rectIntersection}
      onDragStart={handleDragStart}
      onDragOver={handleDragOver}
      onDragEnd={handleDragEnd}
    >
      <div className="mb-8">
        <div className="flex justify-center p-6 mb-6">
          <div className="relative w-full max-w-lg rounded-lg shadow-md overflow-hidden">
            <ImageWrapper
              src={getImageUrl(categoryImageUrl)}
              alt={categoryName}
              className="w-full h-auto object-contain"
            />
          </div>
        </div>

        {!isViewMode ? (
          <div className="flex items-center justify-between gap-8 mb-8 px-4">
            <div className="flex gap-3">
              {[
                { id: "tier", label: "Tier" },
                { id: "rank", label: "Rank" },
              ].map((preset) => (
                <button
                  key={preset.id}
                  onClick={() => handlePresetChange(preset.id as keyof typeof TIER_PRESETS)}
                  className={`
              px-6 py-2 rounded-lg font-medium text-sm
              transition-all duration-300 ease-out
              border-2 
              ${
                selectedPreset === preset.id
                  ? "border-indigo-500 bg-indigo-500/20 text-indigo-300"
                  : "border-gray-600 bg-gray-800/50 text-gray-400 hover:border-gray-500 hover:text-gray-300"
              }
              hover:shadow-[0_0_15px_rgba(99,102,241,0.3)]
              active:scale-95
            `}
                >
                  {preset.label}
                </button>
              ))}
            </div>
            <TierNameInput value={tierName} onChange={(e) => setTierName(e.target.value)} />
          </div>
        ) : (
          <div className="px-4">
            <TierNameInput value={tierName} onChange={(e) => setTierName(e.target.value)} />
          </div>
        )}
      </div>

      <SortableContext items={tierOrder} strategy={rectSortingStrategy}>
        {tierOrder.map((tierKey) => (
          <div key={tierKey} className="relative">
            <SortableTier
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
            {tierOrder.length > 2 && (
              <button
                onClick={() => handleRemoveTier(tierKey)}
                className="absolute -right-8 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-300 transition-colors"
                title="Tierを削除"
              >
                <XCircleIcon size={20} />
              </button>
            )}
          </div>
        ))}
      </SortableContext>

      {tierOrder.length < 10 && (
        <div className="flex justify-center my-6">
          <button
            onClick={handleAddTier}
            className="group flex items-center gap-2 px-4 py-2 rounded-lg  hover:bg-gray-700
                     text-gray-300 hover:text-white transition-all duration-200"
          >
            <PlusCircleIcon
              size={20}
              className="group-hover:scale-110 transition-transform duration-200"
            />
          </button>
        </div>
      )}

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
