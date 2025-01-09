// components/UserTierList.tsx

import React from 'react';
import {Tier as TierType} from '@/types/Tier';
import Link from 'next/link';
import ImageWrapper from "@/components/ImageWrapper";
import {getImageUrl} from "@/utils/getImageUrl";

interface UserTierListProps {
    tiers: TierType[];
}

const UserTierList: React.FC<UserTierListProps> = ({tiers}) => {
    return (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8 p-6">
            {tiers.map((tier) => (
                <div
                    key={tier.accessUrl}
                    className="bg-white rounded-xl shadow-md p-4 hover:shadow-xl transition-shadow duration-300 transform hover:-translate-y-1 cursor-pointer"
                >
                    <Link href={tier.accessUrl}>
                        <div>
                            <ImageWrapper
                                src={getImageUrl(tier.categoryImageUrl)}
                                alt={`${tier.categoryName}の画像`}
                                className="w-full h-40 object-cover rounded-md mb-4"
                                width={400}
                                height={200}
                            />
                            <h3 className="text-xl font-semibold text-gray-900">{tier.name}</h3>
                            <p className="text-sm text-gray-700 mt-2">{tier.categoryName}</p>
                            <p className="text-sm text-gray-500 mt-2">
                                作成日時: {new Date(tier.createdAt).toLocaleString()}
                            </p>
                        </div>
                    </Link>
                </div>
            ))}
        </div>
    );
};

export default UserTierList;
