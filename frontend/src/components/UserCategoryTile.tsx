import Image from "next/image";
import Link from "next/link";

import ImageWrapper from "@/components/ImageWrapper";

import { Category as CategoryType } from "@/types/Category";

import { getImageUrl } from "@/utils/getImageUrl";

interface UserCategoryTileProps {
  category: CategoryType;
}

const UserCategoryTile = ({ category }: UserCategoryTileProps) => {
  return (
    <Link href={`/categories/${category.id}`}>
      <div className="bg-gray-800 rounded-md shadow-sm p-3 hover:shadow-lg transition-shadow duration-200 transform hover:-translate-y-1 cursor-pointer flex flex-col items-center w-full h-[250px]">
        <div className="w-full h-40 bg-gray-700 rounded-md overflow-hidden relative flex items-center justify-center">
          <ImageWrapper
            src={getImageUrl(category.image)}
            alt={`${category.name}の画像`}
            className="object-contain w-full h-full"
            width={300}
            height={160}
            priority={true}
          />
        </div>

        <h3 className="text-base font-medium text-white mt-2">{category.name}</h3>

        {category.description && (
          <p className="text-sm text-gray-400 mt-2 text-center">{category.description}</p>
        )}
      </div>
    </Link>
  );
};

export default UserCategoryTile;
