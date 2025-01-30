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
  });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging && !isOverlay ? 0 : 1,
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      className="w-[120px] h-[120px] rounded-lg overflow-hidden cursor-move"
      {...attributes}
      {...listeners}
    >
      <div className="relative w-full h-full">
        <ImageWrapper
          src={getImageUrl(item.image || "")}
          alt={item.name}
          width={120}
          height={120}
          className="w-full h-full object-cover"
        />
        <div className="absolute bottom-0 left-0 right-0 bg-black/40 backdrop-blur-[2px]">
          <p className="text-white text-xs uppercase tracking-wider text-center truncate px-2 py-1.5">
            {item.name}
          </p>
        </div>
      </div>
    </div>
  );
};

export default DraggableItem;
