"use client";

import { message } from "antd";
import axios from "axios";

import React, { useEffect, useState } from "react";

import TierEditor from "@/components/TierEditor";

import { Item } from "@/types/Item";

import { getApiBaseUrl } from "@/utils/getApiBaseUrl";

interface UserTierItemResponse {
  itemId: string;
  order: number;
  name: string;
  imageUrl: string | null;
  description: string | null;
}

interface UserTierLevelResponse {
  id: string;
  name: string;
  order: number;
  items: UserTierItemResponse[];
}

interface UserTierDetailResponse {
  id: string;
  anonymousId: string;
  categoryId: string;
  categoryName: string;
  categoryImageUrl: string | null;
  name: string;
  isPublic: boolean;
  accessUrl: string;
  levels: UserTierLevelResponse[];
}

type UserTierViewScreenProps = {
  categoryId: string;
  userTierId: string;
};

const UserTierViewScreen: React.FC<UserTierViewScreenProps> = ({ categoryId, userTierId }) => {
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
        const response = await axios.get<UserTierDetailResponse>(
          `${getApiBaseUrl()}/tiers/${userTierId}`,
        );
        const data = response.data;

        // レスポンスデータの変換
        const loadedTiers: Record<string, { name: string; items: Item[] }> = {};
        data.levels.forEach((level) => {
          loadedTiers[`Tier${level.order + 1}`] = {
            name: level.name,
            items: level.items.map((item) => ({
              id: item.itemId,
              name: item.name,
              image: item.imageUrl || undefined,
              description: item.description || undefined,
              order: item.order,
            })),
          };
        });

        setInitialTiers(loadedTiers);
        setTierName(data.name);
        setCategoryName(data.categoryName);
        setCategoryImageUrl(data.categoryImageUrl == null ? "" : data.categoryImageUrl);
        setAvailableItems([]);
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
      } catch (error) {
        message.error("既存Tierの取得に失敗しました");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [userTierId]);

  if (loading) return <div>読み込み中...</div>;

  return (
    <TierEditor
      initialTiers={initialTiers}
      availableItems={availableItems}
      categoryId={categoryId}
      categoryName={categoryName}
      categoryImageUrl={categoryImageUrl}
      initialTierName={tierName}
    />
  );
};

export default UserTierViewScreen;
