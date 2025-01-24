"use client";

import React from "react";

import { useParams } from "next/navigation";

import TierViewScreen from "./TierViewScreen";

type Params = {
  categoryId: string;
  tierId: string;
};

/**
 * /categories/[categoryId]/tiers/[tierId]/page.tsx
 *  - TierViewScreen にパラメータを渡し、既存Tierを閲覧・再編集できるページ
 */
export default function SharedTierPage() {
  const params = useParams<Params>();

  if (!params.categoryId || !params.tierId) {
    return <p>URLパラメータが不正です</p>;
  }

  return <TierViewScreen categoryId={String(params.categoryId)} tierId={String(params.tierId)} />;
}
