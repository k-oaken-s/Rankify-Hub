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

  return (
    <div
      ref={setNodeRef}
      style={{
        transform: CSS.Transform.toString(transform),
        transition,
        opacity: isDragging ? 0.4 : 1,
      }}
      className="group relative w-[120px] h-[120px] rounded-lg overflow-hidden cursor-move
                 transition-all duration-300 ease-in-out bg-transparent
                 hover:shadow-lg hover:-translate-y-1"
      {...attributes}
      {...listeners}
    >
      <div className="relative w-full h-full transition-transform duration-300 group-hover:scale-[1.02]">
        <ImageWrapper
          src={getImageUrl(item.image || "")}
          alt={item.name}
          width={120}
          height={120}
          className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
        />

        <div className="absolute bottom-0 left-0 right-0 bg-black/40 backdrop-blur-[2px]">
          <p
            className="text-white text-xs uppercase tracking-wider text-center truncate px-2 py-1.5
                       transition-all duration-300 group-hover:tracking-widest"
          >
            {item.name}
          </p>
        </div>
      </div>
    </div>
  );
};

export default DraggableItem;
