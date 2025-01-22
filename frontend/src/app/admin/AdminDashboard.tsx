"use client";

import CategoryForm from "@/app/admin/categories/components/CategoryForm";
import { useAdminAuth } from "@/contexts/AdminAuthContext";
import axios from "axios";
import "tailwindcss/tailwind.css";

import { useEffect, useState } from "react";

import { router } from "next/client";

import CategoryList from "@/components/CategoryList";

import { Category } from "@/types/Category";

import api from "@/utils/axios";
import { getApiBaseUrl } from "@/utils/getApiBaseUrl";

const AdminDashboard = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const { isAuthenticated, isLoading, logout } = useAdminAuth();

  useEffect(() => {
    axios
      .get<Category[]>(`${getApiBaseUrl()}/categories`)
      .then((res) => setCategories(res.data))
      .catch((err) => {
        if (err.response?.status === 401) logout();
      });
  }, [isAuthenticated, isLoading, logout, router]);

  const addCategory = (name: string, image: File | null) => {
    if (!isAuthenticated) {
      return;
    }

    const formData = new FormData();
    formData.append("category", new Blob([JSON.stringify({ name })], { type: "application/json" }));
    if (image) formData.append("file", image);

    api
      .post<Category>(`/categories`, formData)
      .then((res) => {
        setCategories((prevCategories) => [...prevCategories, res.data]);
      })
      .catch((err) => {
        if (err.response?.status === 401) logout();
        else console.error("Failed to add category:", err);
      });
  };

  if (!isAuthenticated) {
    return null;
  }

  return (
    <div className="p-8 max-w-4xl mx-auto bg-gray-100 rounded-lg shadow-lg">
      <h1 className="text-3xl font-bold text-center text-gray-900 mb-6">カテゴリーリスト</h1>
      <div className="mb-8 bg-white px-8 py-6 rounded-lg shadow-md hover:shadow-lg transition-shadow duration-300">
        <CategoryForm onAddCategory={addCategory} />
        <CategoryList categories={categories} />
      </div>
    </div>
  );
};

export default AdminDashboard;
