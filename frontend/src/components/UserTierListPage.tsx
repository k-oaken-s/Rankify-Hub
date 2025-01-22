// components/UserTierListPage.tsx
import { Spin } from "antd";

import React from "react";

import UserTierList from "@/components/UserTierList";

import { Tier } from "@/types/Tier";

interface UserTierListPageProps {
  tiers: Tier[];
  isLoading?: boolean;
}

export default function UserTierListPage({ tiers, isLoading = false }: UserTierListPageProps) {
  return (
    <div className="min-h-screen py-8">
      <div className="container mx-auto px-4">
        <h1 className="text-3xl font-bold text-white mb-8">新着ユーザーTier一覧</h1>

        {/* 読み込み中の表示 */}
        {isLoading ? (
          <div className="flex justify-center items-center h-64">
            <Spin size="large" />
          </div>
        ) : (
          <>
            {/* Tier一覧 */}
            {tiers.length > 0 ? (
              <UserTierList tiers={tiers} />
            ) : (
              <div className="text-center text-gray-400 py-12">Tierが見つかりませんでした</div>
            )}

            {/* 件数表示 */}
            <div className="text-gray-400 mt-6 text-right">{tiers.length}件を表示</div>
          </>
        )}
      </div>
    </div>
  );
}
