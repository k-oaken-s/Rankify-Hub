import { useDroppable } from "@dnd-kit/core";
import { SortableContext, rectSortingStrategy } from "@dnd-kit/sortable";

import React from "react";

import { Item } from "@/types/Item";

import DraggableItem from "./DraggableItem";
import DropPreview from "./DropPreview";

type UnassignedAreaProps = {
  items: Item[];
  backgroundColor: string;
  dropPreview?: { index: number } | null;
};

const UnassignedArea = ({ items, backgroundColor, dropPreview }: UnassignedAreaProps) => {
  const { setNodeRef } = useDroppable({
    id: "unassigned-area",
  });

  return (
    <div ref={setNodeRef} className="mt-8 p-4 rounded-md shadow-md" style={{ backgroundColor }}>
      <h3 className="text-lg font-semibold mb-4" style={{ color: "#333" }}>
        未割り当てアイテム
      </h3>
      <SortableContext items={items.map((item) => item.id)} strategy={rectSortingStrategy}>
        <div className="flex gap-4 flex-wrap relative min-h-[120px]">
          {items.map((item, index) => (
            <React.Fragment key={item.id}>
              {dropPreview && index === dropPreview.index && <DropPreview />}
              <DraggableItem item={item} />
            </React.Fragment>
          ))}
          {dropPreview && dropPreview.index >= items.length && <DropPreview />}
        </div>
      </SortableContext>
    </div>
  );
};

export default UnassignedArea;
