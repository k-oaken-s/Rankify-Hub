// app/categories/page.tsx
"use client";

import axios from "axios";

import React, { useEffect, useState } from "react";

import CategoriesListPage from "@/components/CategoriesListPage";

import { Category } from "@/types/Category";

import { getApiBaseUrl } from "@/utils/getApiBaseUrl";

// app/categories/page.tsx

export default function CategoriesPage() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await axios.get<Category[]>(`${getApiBaseUrl()}/categories`);
        setCategories(response.data);
      } catch (error) {
        console.error("Failed to fetch categories:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchCategories();
  }, []);

  return <CategoriesListPage categories={categories} isLoading={isLoading} />;
}
