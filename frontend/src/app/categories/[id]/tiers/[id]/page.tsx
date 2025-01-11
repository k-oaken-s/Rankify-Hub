'use client';

import { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { getApiBaseUrl } from '@/utils/getApiBaseUrl';

// 既存画面と同じ構成を再利用したい場合、同じコンポーネントをそのまま使ってもOK。
// 今回は TierCreationScreen を流用する例を示します。
// 「閲覧専用にしたい」などUI変更があるなら、専用コンポーネントを作るのも良いでしょう。
import TierCreationScreen from '@/app/categories/[id]/TierCreationScreen/TierCreationScreen';

import { Category } from '@/types/Category';
import { Item } from '@/types/Item';

// ユーザーが共有したTierを取得する際のレスポンスイメージ
type UserSharedTier = {
    id: string; // ユーザーのTierID
    items: Item[];
    // 必要に応じてユーザー名など他の情報があれば追加
};

// カテゴリ情報 + ユーザーTier情報をまとめて返すAPI設計の場合のサンプル
type SharedTierResponse = {
    category: Category;
    userTier: UserSharedTier;
};

const SharedTierPage = () => {
    const params = useParams();

    /**
     * ここで Next.js App Router は
     * /categories/[categoryId]/tiers/[userTierId] の場合でも
     * "params.id" という同じキーに2つの値が配列で入ってくることがあります。
     *
     * /categories/123/tiers/456 → params.id = ['123', '456']
     */
    const [categoryId, userTierId] = Array.isArray(params?.id) ? params.id : [null, null];

    const [items, setItems] = useState<Item[]>([]);
    const [categoryName, setCategoryName] = useState('');
    const [categoryImageUrl, setCategoryImageUrl] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!categoryId || !userTierId) return;

        const fetchSharedTier = async () => {
            try {
                // 例: GET /categories/:categoryId/tiers/:userTierId のエンドポイントがある想定
                const response = await fetch(
                    `${getApiBaseUrl()}/categories/${categoryId}/tiers/${userTierId}`
                );
                if (!response.ok) {
                    throw new Error('共有Tierの取得に失敗しました');
                }

                // API のレスポンス形式に合わせてパースする
                const data: SharedTierResponse = await response.json();

                // カテゴリ情報
                const { category, userTier } = data;
                setItems(userTier.items);
                setCategoryName(category.name);
                setCategoryImageUrl(category.image);
            } catch (error) {
                console.error('エラー:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchSharedTier();
    }, [categoryId, userTierId]);

    return (
        <div>
            {loading ? (
                <p>データを読み込み中...</p>
            ) : (
                // 同じTierCreationScreenを再利用する場合は、そのままitemsやcategory情報を渡す
                <TierCreationScreen
                    items={items}
                    categoryId={String(categoryId)}
                    categoryName={categoryName}
                    categoryImageUrl={categoryImageUrl}
                />
            )}
        </div>
    );
};

export default SharedTierPage;
