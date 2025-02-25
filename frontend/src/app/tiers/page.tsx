"use client";

import axios from "axios";

import React, { useEffect, useState } from "react";

import TierListPage from "@/components/TierListPage";

import { Tier } from "@/types/Tier";

import { getApiBaseUrl } from "@/utils/getApiBaseUrl";

export default function TiersPage() {
  const [tiers, setTiers] = useState<Tier[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchTiers = async () => {
      try {
        const response = await axios.get<Tier[]>(`${getApiBaseUrl()}/tiers`);
        const sortedTiers = response.data.sort(
          (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
        );
        setTiers(sortedTiers);
      } catch (error) {
        console.error("Failed to fetch user tiers:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchTiers();
  }, []);

  return <TierListPage tiers={tiers} isLoading={isLoading} />;
}
