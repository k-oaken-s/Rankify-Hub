"use client";

import React from "react";
import {useParams} from "next/navigation";
import UserTierViewScreen from "./UserTierViewScreen";

type Params = {
    categoryId: string;
    userTierId: string;
}

/**
 * /categories/[categoryId]/tiers/[userTierId]/page.tsx
 *  - UserTierViewScreen にパラメータを渡し、既存Tierを閲覧・再編集できるページ
 */
export default function SharedTierPage() {
    const params = useParams<Params>();

    if (!params.categoryId || !params.userTierId) {
        return <p>URLパラメータが不正です</p>;
    }

    // ここではシンプルに、UserTierViewScreen に直接渡すだけ
    return (
        <UserTierViewScreen
            categoryId={String(params.categoryId)}
            userTierId={String(params.userTierId)}
        />
    );
}
