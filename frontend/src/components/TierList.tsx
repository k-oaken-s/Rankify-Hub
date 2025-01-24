import React from "react";

import Link from "next/link";

import ImageWrapper from "@/components/ImageWrapper";

import { Tier as TierType } from "@/types/Tier";

import { getImageUrl } from "@/utils/getImageUrl";

interface TierListProps {
  tiers: TierType[];
}

const TierList: React.FC<TierListProps> = ({ tiers }) => {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8 p-6">
      {tiers.map((tier) => (
        <Link key={tier.accessUrl} href={`/categories/${tier.categoryId}/tiers/${tier.id}`}>
          <div
            className="bg-gray-800 rounded-md shadow-sm p-3 hover:shadow-lg transition-shadow duration-200 transform hover:-translate-y-1 cursor-pointer flex flex-col items-center"
            style={{ width: "300px", height: "250px" }}
          >
            <div className="w-full h-40 bg-gray-700 rounded-md overflow-hidden relative flex items-center justify-center">
              <ImageWrapper
                src={getImageUrl(tier.categoryImageUrl)}
                alt={`${tier.categoryName}の画像`}
                className="object-contain w-full h-full"
                width={300}
                height={160}
                priority={true}
              />
            </div>

            <h3 className="text-base font-medium text-white mt-2">{tier.name}</h3>

            <p className="text-sm text-gray-400 mt-2 text-center">{tier.categoryName}</p>
          </div>
        </Link>
      ))}
    </div>
  );
};

export default TierList;
