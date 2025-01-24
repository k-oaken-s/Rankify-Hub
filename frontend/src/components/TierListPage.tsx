import { Spin } from "antd";

import React from "react";

import TierList from "@/components/TierList";

import { Tier } from "@/types/Tier";

interface TierListPageProps {
  tiers: Tier[];
  isLoading?: boolean;
}

export default function TierListPage({ tiers, isLoading = false }: TierListPageProps) {
  return (
    <div className="min-h-screen py-8">
      <div className="container mx-auto px-4">
        <h1 className="text-3xl font-bold text-white mb-8">新着ユーザーTier一覧</h1>

        {isLoading ? (
          <div className="flex justify-center items-center h-64">
            <Spin size="large" />
          </div>
        ) : (
          <>
            {tiers.length > 0 ? (
              <TierList tiers={tiers} />
            ) : (
              <div className="text-center text-gray-400 py-12">Tierが見つかりませんでした</div>
            )}

            <div className="text-gray-400 mt-6 text-right">{tiers.length}件を表示</div>
          </>
        )}
      </div>
    </div>
  );
}
