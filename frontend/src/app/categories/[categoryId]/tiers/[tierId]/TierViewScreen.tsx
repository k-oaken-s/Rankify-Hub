"use client";

import { message } from "antd";
import axios from "axios";

import React, { useEffect, useState } from "react";

import TierEditor from "@/components/TierEditor";

import { Item } from "@/types/Item";

import { getApiBaseUrl } from "@/utils/getApiBaseUrl";

interface TierItemResponse {
  itemId: string;
  order: number;
  name: string;
  imageUrl: string | null;
  description: string | null;
}

interface TierLevelResponse {
  id: string;
  name: string;
  order: number;
  items: TierItemResponse[];
}

interface TierDetailResponse {
  id: string;
  anonymousId: string;
  categoryId: string;
  categoryName: string;
  categoryImageUrl: string | null;
  name: string;
  isPublic: boolean;
  accessUrl: string;
  levels: TierLevelResponse[];
}

type TierViewScreenProps = {
  categoryId: string;
  tierId: string;
};

const TierViewScreen: React.FC<TierViewScreenProps> = ({ categoryId, tierId }) => {
  const [loading, setLoading] = useState(true);
  const [tierName, setTierName] = useState("");
  const [categoryName, setCategoryName] = useState("");
  const [categoryImageUrl, setCategoryImageUrl] = useState<string>("");
  const [initialTiers, setInitialTiers] = useState<Record<string, { name: string; items: Item[] }>>(
    {},
  );
  const [availableItems, setAvailableItems] = useState<Item[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        // カテゴリーのレスポンス型
        interface CategoryResponse {
          id: string;
          name: string;
          image: string;
          items: {
            id: string;
            name: string;
            image: string;
          }[];
        }

        // Tierのレスポンス型
        interface TierDetailResponse {
          id: string;
          name: string;
          categoryName: string;
          categoryImageUrl: string;
          levels: {
            id: string;
            name: string;
            order: number;
            items: {
              id: string;
              itemId: string;
              order: number;
              name: string;
              imageUrl: string | null;
              description: string | null;
            }[];
          }[];
        }

        // 並行してデータを取得
        const [tierResponse, categoryResponse] = await Promise.all([
          axios.get<TierDetailResponse>(`${getApiBaseUrl()}/tiers/${tierId}`),
          axios.get<CategoryResponse>(`${getApiBaseUrl()}/categories/${categoryId}`),
        ]);

        const tierData = tierResponse.data;
        const categoryData = categoryResponse.data;

        // カテゴリーのアイテムを変換
        const categoryItems: Item[] = categoryData.items.map((item) => ({
          id: item.id,
          name: item.name,
          image: item.image,
          order: 0,
        }));

        // Tierデータを変換
        const loadedTiers: Record<string, { name: string; items: Item[] }> = {};
        const usedItemIds = new Set<string>();

        tierData.levels.forEach((level) => {
          loadedTiers[`Tier${level.order}`] = {
            name: level.name,
            items: level.items.map((item) => {
              usedItemIds.add(item.itemId);
              return {
                id: item.itemId,
                name: item.name,
                image: item.imageUrl || undefined,
                order: item.order,
              };
            }),
          };
        });

        setInitialTiers(loadedTiers);
        setTierName(tierData.name);
        setCategoryName(categoryData.name);
        setCategoryImageUrl(categoryData.image);

        // 未割り当てのアイテムを設定
        const unassignedItems = categoryItems.filter((item) => !usedItemIds.has(item.id));
        setAvailableItems(unassignedItems);
      } catch (error) {
        console.error("Error fetching data:", error);
        message.error("既存Tierの取得に失敗しました");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [tierId, categoryId]);

  if (loading) return <div>読み込み中...</div>;

  return (
    <TierEditor
      initialTiers={initialTiers}
      availableItems={availableItems}
      categoryId={categoryId}
      categoryName={categoryName}
      categoryImageUrl={categoryImageUrl}
      initialTierName={tierName}
      isViewMode={true}
    />
  );
};

export default TierViewScreen;
