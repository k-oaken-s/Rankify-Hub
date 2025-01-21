import { useAdminAuth } from "@/contexts/AdminAuthContext";
import axios from "axios";

import { useEffect, useState } from "react";

import { useRouter } from "next/navigation";

import { Category } from "@/types/Category";

import { getApiBaseUrl } from "@/utils/getApiBaseUrl";

export const useFetchCategoryWithItems = (categoryId: string | undefined) => {
  const [category, setCategory] = useState<Category | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const router = useRouter();
  const { isAuthenticated } = useAdminAuth();

  useEffect(() => {
    if (!categoryId) return;

    const token = localStorage.getItem("admin_token");
    if (!isAuthenticated) {
      router.push("/admin/login");
      return;
    }

    setIsLoading(true);
    axios
      .get<Category>(`${getApiBaseUrl()}/categories/${categoryId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      })
      .then((res) => {
        setCategory(res.data);
      })
      .catch((err) => {
        console.error(err);
        if (err.response && err.response.status === 401) {
          router.push("/admin/login");
        }
      })
      .finally(() => setIsLoading(false));
  }, [categoryId, router]);

  return { category, isLoading };
};
