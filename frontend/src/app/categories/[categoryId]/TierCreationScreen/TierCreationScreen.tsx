"use client";

import React from "react";

import TierEditor from "@/components/TierEditor";

import { Item } from "@/types/Item";

/** 新規作成用コンポーネント */

type TierCreationScreenProps = {
  items: Item[];
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
  return (
    <TierEditor
      initialTierName={`${categoryName} Tier`}
      availableItems={items}
      categoryId={categoryId}
      categoryName={categoryName}
      categoryImageUrl={categoryImageUrl}
      isViewMode={false}
    />
  );
};
export default TierCreationScreen;
