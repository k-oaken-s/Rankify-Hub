"use client";

import axios from "axios";

import { useEffect, useState } from "react";

import CategoriesListPage from "@/components/CategoriesListPage";

import { Category } from "@/types/Category";
import { SortOrder } from "@/types/SortOrder";

import { getApiBaseUrl } from "@/utils/getApiBaseUrl";

export default function CategoriesPage() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [sortOrder, setSortOrder] = useState<SortOrder>(SortOrder.DESC);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await axios.get<Category[]>(`${getApiBaseUrl()}/categories`, {
          params: {
            name: searchQuery || undefined,
            sortOrder,
          },
        });
        setCategories(response.data);
      } catch (error) {
        console.error("Failed to fetch categories:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchCategories();
  }, [searchQuery, sortOrder]);

  return (
    <CategoriesListPage
      categories={categories}
      isLoading={isLoading}
      searchQuery={searchQuery}
      onSearchChange={setSearchQuery}
      sortOrder={sortOrder}
      onSortChange={setSortOrder}
    />
  );
}
