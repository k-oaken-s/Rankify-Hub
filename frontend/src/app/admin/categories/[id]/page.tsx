"use client";

import { useAdminAuth } from "@/contexts/AdminAuthContext";
import { ArrowLeftOutlined, UploadOutlined } from "@ant-design/icons";
import { Spin, Typography, Upload, message } from "antd";
import axios from "axios";

import React, { useCallback, useEffect, useMemo, useState } from "react";

import { useParams, useRouter } from "next/navigation";

import ImageWrapper from "@/components/ImageWrapper";

import { useFetchCategoryWithItems } from "@/hooks/useFetchCategoryWithItems";

import { Category } from "@/types/Category";
import { Item } from "@/types/Item";

import api from "@/utils/axios";
import { getApiBaseUrl } from "@/utils/getApiBaseUrl";
import { getImageUrl } from "@/utils/getImageUrl";

const { Title, Text } = Typography;

// カテゴリヘッダー部分をメモ化
const CategoryHeader = React.memo(function CategoryHeader({ category }: { category: Category }) {
  return (
    <div className="mb-8 p-6 bg-gray-800 rounded-lg shadow-lg">
      <h3 className="text-xl font-semibold">{category.name}</h3>
      {category.image && (
        <ImageWrapper
          src={getImageUrl(category.image)}
          alt={`${category.name} image`}
          className="mt-4 rounded-md w-full max-h-72 object-cover"
          width={400}
          height={400}
        />
      )}
      {category.description && <p className="mt-4 text-gray-400">{category.description}</p>}
    </div>
  );
});

// アイテム一覧部分をメモ化
const ItemList = React.memo(function ItemList({
  items,
  onEdit,
}: {
  items: Item[];
  onEdit: (item: Item) => void;
}) {
  return (
    <ul className="space-y-4">
      {items.map((item) => (
        <li
          key={item.id}
          className="flex items-center justify-between p-4 bg-gray-800 rounded-md shadow hover:shadow-md transition"
        >
          <div className="flex items-center space-x-4">
            {item.image && (
              <ImageWrapper
                src={getImageUrl(item.image)}
                alt={`${item.name} image`}
                className="w-12 h-12 rounded-md object-cover"
                width={50}
                height={50}
              />
            )}
            <span className="text-sm font-medium">{item.name}</span>
          </div>
          <button
            className="px-4 py-2 text-sm bg-gray-700 text-gray-300 rounded hover:bg-gray-600"
            onClick={() => onEdit(item)}
          >
            編集
          </button>
        </li>
      ))}
    </ul>
  );
});

const CategoryDetailPage = () => {
  const params = useParams();
  const router = useRouter();
  const categoryId = Array.isArray(params?.id) ? params.id[0] : params?.id;
  const { category, isLoading } = useFetchCategoryWithItems(categoryId);
  const [items, setItems] = useState<Item[]>([]);
  const [editingItem, setEditingItem] = useState<Item | null>(null);
  const [itemName, setItemName] = useState("");
  const [itemImage, setItemImage] = useState<File | null | "remove">(null);
  const [imagePreview, setImagePreview] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [uploadKey, setUploadKey] = useState(0); // Uploadの状態リセット用
  const { isAuthenticated, token } = useAdminAuth();

  useEffect(() => {
    if (category) setItems(category.items);
  }, [category]);

  const handleAddOrUpdateItem = useCallback(async () => {
    setIsSubmitting(true);
    const formData = new FormData();
    formData.append(
      "item",
      new Blob([JSON.stringify({ name: itemName })], { type: "application/json" }),
    );

    if (itemImage && itemImage !== "remove") formData.append("file", itemImage);
    if (editingItem && itemImage === "remove") formData.append("removeImage", "true");

    try {
      const url = editingItem
        ? `/categories/${categoryId}/items/${editingItem.id}`
        : `/categories/${categoryId}/items`;

      const method = editingItem ? "put" : "post";
      const res = await api[method]<Item>(url, formData);

      setItems((prevItems) =>
        editingItem
          ? prevItems.map((item) => (item.id === editingItem.id ? res.data : item))
          : [...prevItems, res.data],
      );
      message.success(editingItem ? "アイテムが更新されました" : "アイテムが追加されました");

      // 入力欄と画像選択をリセット
      setEditingItem(null);
      setItemName("");
      setItemImage(null);
      setImagePreview(null);
      setUploadKey((prevKey) => prevKey + 1);
    } catch (error) {
      console.error("Failed to add/update item:", error);
      message.error("アイテムの処理に失敗しました");
    } finally {
      setIsSubmitting(false);
    }
  }, [categoryId, editingItem, itemImage, itemName, isAuthenticated, router]);

  const handleImageUpload = useCallback((file: File) => {
    setItemImage(file);
    setImagePreview(URL.createObjectURL(file));
    return false;
  }, []);

  const handleImageRemove = useCallback(() => {
    setItemImage(null);
    setImagePreview(null);
  }, []);

  const handleEditItem = useCallback((item: Item) => {
    setEditingItem(item);
    setItemName(item.name);
    setItemImage(null);
    setImagePreview(null);
    setUploadKey((prevKey) => prevKey + 1);
  }, []);

  const handleItemNameChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    setItemName(e.target.value);
  }, []);

  const memoizedCategory = useMemo(() => category, [category]);
  const memoizedItems = useMemo(() => items, [items]);

  return (
    <div className="p-8 max-w-4xl mx-auto bg-gray-900 text-gray-300">
      {isLoading ? (
        <div className="text-center my-10">
          <Spin size="large" />
        </div>
      ) : (
        <>
          <button
            className="mb-6 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-500 transition"
            onClick={() => router.push("/admin")}
          >
            <ArrowLeftOutlined className="mr-2" />
            管理者ダッシュボードに戻る
          </button>

          {memoizedCategory && <CategoryHeader category={memoizedCategory} />}

          <h4 className="text-lg font-semibold mb-4">カテゴリーのアイテム一覧</h4>
          <ItemList items={memoizedItems} onEdit={handleEditItem} />

          <div className="mt-8 p-6 bg-gray-800 rounded-lg shadow max-h-[70vh] overflow-auto">
            <h4 className="text-lg font-semibold mb-4">
              {editingItem ? "アイテムを編集" : "新しいアイテムを追加"}
            </h4>
            <input
              type="text"
              placeholder="アイテム名を入力"
              value={itemName}
              onChange={handleItemNameChange}
              className="w-full px-4 py-2 mb-4 bg-gray-700 text-white rounded-md border border-gray-600 focus:outline-none focus:ring focus:ring-blue-500"
            />
            {imagePreview && (
              <div className="mb-4 flex justify-center">
                <img
                  src={imagePreview}
                  alt="選択した画像プレビュー"
                  className="max-w-full max-h-64 object-cover rounded-md"
                />
              </div>
            )}
            <Upload
              key={uploadKey}
              beforeUpload={handleImageUpload}
              onRemove={handleImageRemove}
              className="mb-4"
              disabled={isSubmitting}
            >
              <button
                className={`px-4 py-2 text-sm rounded transition ${
                  isSubmitting
                    ? "bg-gray-500 cursor-not-allowed text-gray-400"
                    : "bg-gray-700 hover:bg-gray-600 text-gray-300"
                }`}
                disabled={isSubmitting}
              >
                <UploadOutlined className="mr-2" />
                画像を選択
              </button>
            </Upload>
            <button
              className={`px-4 py-2 mt-4 rounded-md transition ${
                isSubmitting ? "bg-gray-500 cursor-not-allowed" : "bg-blue-600 hover:bg-blue-500"
              }`}
              onClick={handleAddOrUpdateItem}
              disabled={isSubmitting}
            >
              {editingItem ? "保存" : "追加"}
            </button>
          </div>
        </>
      )}
    </div>
  );
};

export default CategoryDetailPage;
