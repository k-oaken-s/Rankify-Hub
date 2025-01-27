import { useSortable } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";

import React from "react";

import ImageWrapper from "@/components/ImageWrapper";

import { Item } from "@/types/Item";

import { getImageUrl } from "@/utils/getImageUrl";

type DraggableItemProps = {
  item: Item;
  isOverlay?: boolean;
};

const DraggableItem: React.FC<DraggableItemProps> = ({ item, isOverlay = false }) => {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({
    id: item.id,
    data: {
      type: "item",
      item,
    },
  });

  // ベースとなるスタイル
  const baseStyle: React.CSSProperties = {
    opacity: isDragging ? 0.4 : 1,
    transform: transform ? CSS.Transform.toString(transform) : undefined,
    transition: transition ?? "transform 0.2s ease",
    width: "120px",
    height: "120px",
    position: "relative",
    borderRadius: "8px",
    overflow: "hidden",
    cursor: "move",
  };

  // 画像コンテナのスタイル
  const imageContainerStyle: React.CSSProperties = {
    position: "relative",
    width: "100%",
    height: "100%",
  };

  // 画像のスタイル
  const imageStyle: React.CSSProperties = {
    width: "100%",
    height: "100%",
    objectFit: "cover",
  };

  // グラデーションオーバーレイのスタイル
  const overlayStyle: React.CSSProperties = {
    position: "absolute",
    bottom: 0,
    left: 0,
    right: 0,
    height: "50%",
    background: "linear-gradient(to top, rgba(0,0,0,0.7) 0%, rgba(0,0,0,0) 100%)",
  };

  // テキストのスタイル
  const textStyle: React.CSSProperties = {
    position: "absolute",
    bottom: 0,
    left: 0,
    right: 0,
    padding: "8px",
    color: "white",
    fontSize: "14px",
    textAlign: "center",
    fontWeight: "500",
    textOverflow: "ellipsis",
    overflow: "hidden",
    whiteSpace: "nowrap",
  };

  return (
    <div ref={setNodeRef} style={baseStyle} {...listeners} {...attributes}>
      <div style={imageContainerStyle}>
        <ImageWrapper
          src={getImageUrl(item.image || "")}
          alt={item.name}
          width={120}
          height={120}
          style={imageStyle}
        />
        <div style={overlayStyle} />
        <div style={textStyle}>{item.name}</div>
      </div>
    </div>
  );
};

export default DraggableItem;
