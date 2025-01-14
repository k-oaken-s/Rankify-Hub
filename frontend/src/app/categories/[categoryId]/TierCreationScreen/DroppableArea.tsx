import {Item} from "@/types/Item";
import {useDroppable} from "@dnd-kit/core";
import React from "react";

type DroppableAreaProps = {
    id: string;
    items: Item[];
    children: React.ReactNode;
};

const DroppableArea: React.FC<DroppableAreaProps> = ({id, items, children}) => {
    const {setNodeRef} = useDroppable({
        id,
        data: {tierName: id},
    });

    return (
        <div
            ref={setNodeRef} // setNodeRefを適用
            id={id}
            className="relative flex flex-col gap-4 p-4"
            style={{
                backgroundColor: "inherit",
                borderRadius: "8px",
            }}
        >
            {children}
        </div>
    );
};

export default DroppableArea;
