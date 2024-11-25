import { Category as CategoryType } from '@/types/Category';
import { getImageUrl } from '@/utils/getImageUrl';
import { Card } from 'antd';
import Link from 'next/link';
import Image from 'next/image';
import ImageWrapper from "@/components/ImageWrapper";

interface CategoryProps {
    category: CategoryType;
}

const Category = ({ category }: CategoryProps) => (
    <Link href={`/admin/categories/${category.id}`} passHref>
        <Card
            hoverable
            style={{
                height: '100%', // カードの高さを100%に設定
                display: 'flex',
                flexDirection: 'column',
                padding: 0, // カード全体のレイアウトを調整
            }}
            cover={
                <div style={{ height: 250, overflow: 'hidden' }}>
                    <ImageWrapper
                        src={getImageUrl(category.image)}
                        alt={`${category.name}の画像`}
                        style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                                          width={400}
                        height={400}
                        onError={(e) => {
                            e.currentTarget.src = '/default-thumbnail.jpg';
                        }}
                    />
                </div>
            }
        >
            <div style={{ padding: '16px', flexGrow: 1, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <span style={{ fontWeight: 'bold', fontSize: '16px', textAlign: 'center' }}>{category.name}</span>
            </div>
        </Card>
    </Link>
);

export default Category;
