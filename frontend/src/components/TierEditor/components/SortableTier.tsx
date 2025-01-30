import { useSortable } from "@dnd-kit/sortable";
import { SortableContext, horizontalListSortingStrategy } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";

import React from "react";

import { Item } from "@/types/Item";

import DraggableItem from "./DraggableItem";

interface SortableTierProps {
  id: string;
  tierKey: string;
  name: string;
  items: Item[];
  backgroundColor: string;
  onNameChange: (newName: string) => void;
}

const SortableTier: React.FC<SortableTierProps> = ({
  id,
  tierKey,
  name,
  items,
  backgroundColor,
  onNameChange,
}) => {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({
    id,
  });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  };

  return (
    <div ref={setNodeRef} style={style} {...attributes}>
      <div className="flex mb-4 rounded-md shadow-md overflow-hidden" style={{ backgroundColor }}>
        <div
          className="w-32 flex items-center justify-center p-4 cursor-move border-r border-white/10"
          {...listeners}
          style={{ backgroundColor }}
        >
          <input
            type="text"
            value={name}
            onChange={(e) => onNameChange(e.target.value)}
            className="w-full text-center bg-transparent text-white border-none focus:outline-none"
            style={{ cursor: "text" }}
            onClick={(e) => e.stopPropagation()}
          />
        </div>
        <div className="flex-1 p-4" style={{ backgroundColor }}>
          <SortableContext
            items={items.map((item) => item.id)}
            strategy={horizontalListSortingStrategy}
          >
            <div className="flex flex-wrap gap-4 min-h-[120px]">
              {items.map((item) => (
                <DraggableItem key={item.id} item={item} />
              ))}
            </div>
          </SortableContext>
        </div>
      </div>
    </div>
  );
};

export default SortableTier;
