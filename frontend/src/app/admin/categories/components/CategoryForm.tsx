import Image from 'next/image';
import { useState } from 'react';
import ImageWrapper from "@/components/ImageWrapper";

interface CategoryFormProps {
    onAddCategory: (name: string, image: File | null) => void;
}

const CategoryForm = ({ onAddCategory }: CategoryFormProps) => {
    const [newCategoryName, setNewCategoryName] = useState('');
    const [categoryImage, setCategoryImage] = useState<File | null>(null);
    const [imagePreview, setImagePreview] = useState<string | null>(null);

    const handleImageUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files[0]) {
            const file = e.target.files[0];
            setCategoryImage(file);
            setImagePreview(URL.createObjectURL(file));
        }
    };

    const handleSubmit = () => {
        if (!newCategoryName) return;
        onAddCategory(newCategoryName, categoryImage);
        setNewCategoryName('');
        setCategoryImage(null);
        setImagePreview(null);
    };

    return (
        <div className="p-8 border rounded-lg shadow-lg bg-gray-50 hover:shadow-xl transition-shadow duration-300 mb-8 max-w-md mx-auto">
            <h2 className="text-2xl font-semibold mb-6 text-center text-gray-800">新しいカテゴリーを追加</h2>

            <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">カテゴリー名</label>
                <input
                    type="text"
                    placeholder="カテゴリー名を入力"
                    value={newCategoryName}
                    onChange={(e) => setNewCategoryName(e.target.value)}
                    className="border border-gray-300 rounded-lg p-3 w-full focus:outline-none focus:ring-2 focus:ring-indigo-500 text-gray-700"
                />
            </div>

            <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">画像をアップロード</label>
                <input
                    type="file"
                    onChange={handleImageUpload}
                    className="w-full text-sm text-gray-600 bg-gray-100 border border-gray-200 rounded-lg p-2 cursor-pointer focus:outline-none hover:bg-gray-200 transition"
                />
            </div>

            {imagePreview && (
                <div className="mb-6">
                    <ImageWrapper src={imagePreview}
                    width={400}
                    height={400}
                    alt="Preview"
                    className="w-full h-48 object-cover rounded-lg shadow-md" />
                </div>
            )}

            <button
                onClick={handleSubmit}
                disabled={!newCategoryName}
                className={`w-full py-3 px-4 rounded-lg text-white font-semibold transition-colors duration-200 ${newCategoryName ? 'bg-indigo-600 hover:bg-indigo-700' : 'bg-gray-300 cursor-not-allowed'}`}
            >
                カテゴリーを追加
            </button>
        </div>
    );
};

export default CategoryForm;
