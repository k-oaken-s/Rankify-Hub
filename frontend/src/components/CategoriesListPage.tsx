import { SearchOutlined } from "@ant-design/icons";
import { Input, Select, Spin } from "antd";

import React from "react";

import UserCategoryTile from "@/components/UserCategoryTile";

import { Category } from "@/types/Category";
import { SortOrder } from "@/types/SortOrder";

const { Option } = Select;

interface CategoriesListPageProps {
  categories: Category[];
  isLoading?: boolean;
  searchQuery: string;
  onSearchChange: (query: string) => void;
  sortOrder: SortOrder;
  onSortChange: (order: SortOrder) => void;
}

export default function CategoriesListPage({
  categories,
  isLoading = false,
  searchQuery,
  onSearchChange,
  sortOrder,
  onSortChange,
}: CategoriesListPageProps) {
  return (
    <div className="min-h-screen py-8">
      <div className="container mx-auto px-4">
        <div className="flex flex-col md:flex-row gap-4 mb-8">
          <div className="flex-1">
            <Input
              placeholder="カテゴリー名で検索..."
              prefix={<SearchOutlined className="text-gray-400" />}
              value={searchQuery}
              onChange={(e) => onSearchChange(e.target.value)}
              className="w-full"
            />
          </div>
          <div className="w-full md:w-48">
            <Select value={sortOrder} onChange={onSortChange} className="w-full">
              <Option value={SortOrder.DESC}>新しい順</Option>
              <Option value={SortOrder.ASC}>古い順</Option>
            </Select>
          </div>
        </div>

        {isLoading ? (
          <div className="flex justify-center items-center h-64">
            <Spin size="large" />
          </div>
        ) : (
          <>
            {categories.length == 0 ? (
              <div className="text-center text-gray-400 py-12">
                カテゴリーが見つかりませんでした
              </div>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                {categories.map((category) => (
                  <UserCategoryTile key={category.id} category={category} />
                ))}
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}
