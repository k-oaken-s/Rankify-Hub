import { useSortable } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";

import React from "react";

import { Item } from "@/types/Item";

import DraggableItem from "./DraggableItem";
import DropPreview from "./DropPreview";

interface SortableTierProps {
  id: string;
  tierKey: string;
  name: string;
  items: Item[];
  backgroundColor: string;
  onNameChange: (newName: string) => void;
  dropPreview?: { index: number } | null;
}

const SortableTier: React.FC<SortableTierProps> = ({
  id,
  tierKey,
  name,
  items,
  backgroundColor,
  onNameChange,
  dropPreview,
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
        <div className="w-32 flex items-center justify-center p-4 cursor-move" {...listeners}>
          <input
            type="text"
            value={name}
            onChange={(e) => onNameChange(e.target.value)}
            className="w-full text-center bg-transparent text-white border-none focus:outline-none"
            style={{ cursor: "text" }}
            onClick={(e) => e.stopPropagation()}
          />
        </div>
        <div className="flex-1 bg-gray-800 p-4">
          <div className="flex gap-4 flex-wrap relative min-h-[120px]">
            {items.map((item, index) => (
              <React.Fragment key={item.id}>
                {dropPreview && index === dropPreview.index && <DropPreview />}
                <DraggableItem item={item} />
              </React.Fragment>
            ))}
            {dropPreview && dropPreview.index >= items.length && <DropPreview />}
          </div>
        </div>
      </div>
    </div>
  );
};

export default SortableTier;
