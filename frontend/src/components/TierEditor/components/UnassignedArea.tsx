import { useDroppable } from "@dnd-kit/core";

import DraggableItem from "@/components/TierEditor/components/DraggableItem";

import { Item } from "@/types/Item";

type UnassignedAreaProps = {
  items: Item[];
  backgroundColor: string;
};
// 未割り当てエリアのコンポーネント
const UnassignedArea = ({ items, backgroundColor }: UnassignedAreaProps) => {
  const { setNodeRef } = useDroppable({
    id: "unassigned-area",
  });

  return (
    <div
      ref={setNodeRef}
      className="mt-8 p-4 rounded-md shadow-md"
      style={{
        backgroundColor,
        minHeight: "150px",
      }}
    >
      <h3 className="text-lg font-semibold mb-4" style={{ color: "#333" }}>
        未割り当てアイテム
      </h3>
      <div className="flex gap-4 flex-wrap">
        {items.map((item) => (
          <DraggableItem key={item.id} item={item} />
        ))}
      </div>
    </div>
  );
};

export default UnassignedArea;
