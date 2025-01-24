import { Card } from "antd";

import Image from "next/image";
import Link from "next/link";

import ImageWrapper from "@/components/ImageWrapper";

import { Category as CategoryType } from "@/types/Category";

import { getImageUrl } from "@/utils/getImageUrl";

interface CategoryProps {
  category: CategoryType;
}

const Category = ({ category }: CategoryProps) => (
  <Link href={`/admin/categories/${category.id}`} passHref>
    <Card
      hoverable
      style={{
        height: "100%",
        display: "flex",
        flexDirection: "column",
        padding: 0,
      }}
      cover={
        <div style={{ height: 250, overflow: "hidden" }}>
          <ImageWrapper
            src={getImageUrl(category.image)}
            alt={`${category.name}の画像`}
            style={{ width: "100%", height: "100%", objectFit: "cover" }}
            width={400}
            height={400}
            onError={(e) => {
              e.currentTarget.src = "/default-thumbnail.jpg";
            }}
          />
        </div>
      }
    >
      <div style={{ padding: "16px" }}>
        <div className="mb-2">
          <span
            style={{ fontWeight: "bold", fontSize: "16px", display: "block", textAlign: "center" }}
          >
            {category.name}
          </span>
        </div>
        <div style={{ textAlign: "center", color: "#666" }}>
          発売日: {new Date(category.releaseDate).toLocaleDateString("ja-JP")}
        </div>
      </div>
    </Card>
  </Link>
);

export default Category;
