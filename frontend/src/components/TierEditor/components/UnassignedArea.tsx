import { useDroppable } from "@dnd-kit/core";
import { SortableContext, horizontalListSortingStrategy } from "@dnd-kit/sortable";

import React from "react";

import { Item } from "@/types/Item";

import DraggableItem from "./DraggableItem";

type UnassignedAreaProps = {
  items: Item[];
  backgroundColor: string;
};

const UnassignedArea = ({ items, backgroundColor }: UnassignedAreaProps) => {
  const { setNodeRef } = useDroppable({
    id: "unassigned-area",
  });

  return (
    <div ref={setNodeRef} className="mt-6 p-3 rounded-md shadow-md" style={{ backgroundColor }}>
      <h3 className="text-lg font-semibold mb-3" style={{ color: "#333" }}>
        未割り当てアイテム
      </h3>
      <SortableContext
        items={items.map((item) => item.id)}
        strategy={horizontalListSortingStrategy}
      >
        <div className="flex flex-wrap gap-3 min-h-[106px]">
          {items.map((item) => (
            <DraggableItem key={item.id} item={item} />
          ))}
        </div>
      </SortableContext>
    </div>
  );
};

export default UnassignedArea;
