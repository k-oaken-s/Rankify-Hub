import { useSortable } from "@dnd-kit/sortable";
import { SortableContext, horizontalListSortingStrategy } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import { XCircleIcon } from "lucide-react";

import React, { useState } from "react";

import { Item } from "@/types/Item";

import DraggableItem from "./DraggableItem";

interface SortableTierProps {
  id: string;
  tierKey: string;
  name: string;
  items: Item[];
  backgroundColor: string;
  onNameChange: (newName: string) => void;
  canRemove: boolean; // 追加
  onRemove?: () => void; // 追加
}

const SortableTier: React.FC<SortableTierProps> = ({
  id,
  tierKey,
  name,
  items,
  backgroundColor,
  onNameChange,
  canRemove = false, // デフォルト値
  onRemove,
}) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editedName, setEditedName] = useState(name);

  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({
    id,
  });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  };

  const handleNameSave = () => {
    onNameChange(editedName.trim() || name);
    setIsEditing(false);
  };

  return (
    <div ref={setNodeRef} style={style} {...attributes} className="relative">
      <div
        className="flex flex-col sm:flex-row mb-4 rounded-md shadow-md overflow-hidden relative"
        style={{ backgroundColor }}
      >
        {canRemove && (
          <button
            onClick={onRemove}
            className="absolute top-2 right-2 text-gray-400 hover:text-gray-300 transition-colors z-10"
            title="Tierを削除"
          >
            <XCircleIcon size={20} />
          </button>
        )}

        <div
          {...listeners}
          className="w-full sm:w-32 flex items-center justify-center p-4 relative border-b sm:border-b-0 sm:border-r border-white/10 cursor-grab  touch-none"
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
        <div className="flex-1 py-4 px-4" style={{ backgroundColor }}>
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
