import { SearchOutlined } from "@ant-design/icons";
import { Input, Select, Spin } from "antd";

import React, { useMemo, useState } from "react";

import UserCategoryTile from "@/components/UserCategoryTile";

import { Category } from "@/types/Category";

const { Option } = Select;
const PAGE_SIZE = 30;

interface CategoriesListPageProps {
  categories: Category[];
  isLoading?: boolean;
}

export default function CategoriesListPage({
  categories,
  isLoading = false,
}: CategoriesListPageProps) {
  const [searchQuery, setSearchQuery] = useState("");
  const [sortBy, setSortBy] = useState<"latest" | "oldest">("latest");
  const [currentPage, setCurrentPage] = useState(1);

  const processedCategories = useMemo(() => {
    let result = [...categories];

    if (searchQuery) {
      result = result.filter((category) =>
        category.name.toLowerCase().includes(searchQuery.toLowerCase()),
      );
    }

    result.sort((a, b) => {
      const dateA = new Date(a.createdAt).getTime();
      const dateB = new Date(b.createdAt).getTime();
      return sortBy === "latest" ? dateB - dateA : dateA - dateB;
    });

    const startIndex = (currentPage - 1) * PAGE_SIZE;
    const paginatedResult = result.slice(startIndex, startIndex + PAGE_SIZE);

    return {
      items: paginatedResult,
      total: result.length,
    };
  }, [categories, searchQuery, sortBy, currentPage]);

  return (
    <div className="min-h-screen py-8">
      <div className="container mx-auto px-4">
        <h1 className="text-3xl font-bold text-white mb-8">カテゴリー一覧</h1>

        <div className="flex flex-col md:flex-row gap-4 mb-8">
          <div className="flex-1">
            <Input
              placeholder="カテゴリー名で検索..."
              prefix={<SearchOutlined className="text-gray-400" />}
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full"
              style={{ backgroundColor: "#374151", borderColor: "#4B5563", color: "white" }}
            />
          </div>
          <div className="w-full md:w-48">
            <Select
              value={sortBy}
              onChange={setSortBy}
              className="w-full"
              style={{ backgroundColor: "#374151" }}
            >
              <Option value="latest">新しい順</Option>
              <Option value="oldest">古い順</Option>
            </Select>
          </div>
        </div>

        {isLoading ? (
          <div className="flex justify-center items-center h-64">
            <Spin size="large" />
          </div>
        ) : (
          <>
            {processedCategories.items.length > 0 ? (
              <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                {processedCategories.items.map((category) => (
                  <UserCategoryTile key={category.id} category={category} />
                ))}
              </div>
            ) : (
              <div className="text-center text-gray-400 py-12">
                カテゴリーが見つかりませんでした
              </div>
            )}

            <div className="text-gray-400 mt-6 text-right">
              全{categories.length}件中 {processedCategories.total}件を表示
            </div>
          </>
        )}
      </div>
    </div>
  );
}
