import { Category } from '@/types/Category';
import Link from 'next/link';

interface CategoryListProps {
    categories: Category[];
}

const CategoryList = ({ categories }: CategoryListProps) => (
    <ul className="space-y-4">
        {categories.length > 0 ? (
            categories.map((category) => (
                <li key={category.id} className="border rounded p-4 shadow-md flex items-center">
                    {category.image && (
                        <img
                            src={`data:image/png;base64,${category.image}`}
                            alt={`${category.name}の画像`}
                            className="w-16 h-16 object-cover mr-4"
                            loading="lazy"
                        />
                    )}
                    <div className="flex-1">
                        <Link href={`/admin/categories/${category.id}`} className="font-semibold text-lg text-blue-600 hover:underline">
                            {category.name}
                        </Link>
                    </div>
                </li>
            ))
        ) : (
            <li className="text-gray-500">カテゴリーがありません</li>
        )}
    </ul>
);

export default CategoryList;