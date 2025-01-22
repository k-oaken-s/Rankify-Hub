"use client";

import axios from "axios";
import "tailwindcss/tailwind.css";

import { useEffect, useRef, useState } from "react";

import UserCategoryList from "@/components/UserCategoryList";
import UserTierList from "@/components/UserTierList";

import { Category as CategoryType } from "@/types/Category";
import { Tier as TierType } from "@/types/Tier";

import { getApiBaseUrl } from "@/utils/getApiBaseUrl";

const TopPage = () => {
  const [categories, setCategories] = useState<CategoryType[]>([]);
  const [tiers, setTiers] = useState<TierType[]>([]);
  const lastPolledTimeRef = useRef<number>(Date.now());

  useEffect(() => {
    const fetchInitialData = async () => {
      try {
        const [categoriesRes, tiersRes] = await Promise.all([
          axios.get<CategoryType[]>(`${getApiBaseUrl()}/categories?limit=10&sort=createdAt_desc`),
          axios.get<TierType[]>(`${getApiBaseUrl()}/tiers/latest?limit=10`),
        ]);

        setCategories(categoriesRes.data);
        setTiers(tiersRes.data);
        if (tiersRes.data.length > 0) {
          lastPolledTimeRef.current = new Date(tiersRes.data[0].createdAt).getTime();
        }
      } catch (err) {
        console.error("Failed to fetch initial data:", err);
      }
    };

    fetchInitialData();
  }, []);

  useEffect(() => {
    let isMounted = true;
    let timer: NodeJS.Timeout | null = null;

    const longPolling = async () => {
      if (!isMounted) return;

      try {
        const res = await axios.get<TierType[]>(
          `${getApiBaseUrl()}/tiers/since?since=${lastPolledTimeRef.current}`,
          { timeout: 35000 },
        );

        if (!isMounted) return;

        if (res.data && res.data.length > 0) {
          const prevIds = new Set(tiers.map((t) => t.id));
          const newData = res.data.filter((t) => !prevIds.has(t.id));

          if (newData.length > 0) {
            const timestamps = newData.map((tier) => new Date(tier.createdAt).getTime());
            const newestTimestamp = Math.max(...timestamps);
            lastPolledTimeRef.current = newestTimestamp;

            setTiers((prevTiers) => {
              const newList = [...newData, ...prevTiers];
              return newList.slice(0, 10);
            });

            timer = setTimeout(() => {
              if (isMounted) longPolling();
            }, 0);
          } else {
            lastPolledTimeRef.current = Date.now();
            timer = setTimeout(() => {
              if (isMounted) longPolling();
            }, 2000);
          }
        } else {
          lastPolledTimeRef.current = Date.now();
          timer = setTimeout(() => {
            if (isMounted) longPolling();
          }, 2000);
        }
      } catch (err) {
        timer = setTimeout(() => {
          if (isMounted) longPolling();
        }, 5000);
      }
    };

    longPolling();

    return () => {
      isMounted = false;
      if (timer) {
        clearTimeout(timer);
      }
    };
  }, [tiers]);

  return (
    <div className="container mx-auto px-4">
      <h1 className="text-4xl font-bold text-center mt-10 mb-12">新着Tier一覧</h1>
      <UserTierList tiers={tiers} />

      <h1 className="text-4xl font-bold text-center mt-10 mb-12">カテゴリ一覧</h1>
      <UserCategoryList categories={categories} />
    </div>
  );
};

export default TopPage;
