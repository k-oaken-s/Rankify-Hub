"use client";

import React from "react";

import TierEditor from "@/components/TierEditor";

import { Item } from "@/types/Item";

/** 新規作成用コンポーネント */

type TierCreationScreenProps = {
  items: Item[]; // すべてのアイテム
  categoryId: string;
  categoryName: string;
  categoryImageUrl: string;
};

const TierCreationScreen: React.FC<TierCreationScreenProps> = ({
  items,
  categoryId,
  categoryName,
  categoryImageUrl,
}) => {
  const initialTiers = {
    Tier1: { name: "Tier 1", items: [] },
    Tier2: { name: "Tier 2", items: [] },
    Tier3: { name: "Tier 3", items: [] },
    Tier4: { name: "Tier 4", items: [] },
  };

  return (
    <TierEditor
      initialTierName={""}
      initialTiers={initialTiers}
      availableItems={items}
      categoryId={categoryId}
      categoryName={categoryName}
      categoryImageUrl={categoryImageUrl}
    />
  );
};

export default TierCreationScreen;
