import React from "react";
import {useSortable} from "@dnd-kit/sortable";
import {CSS} from "@dnd-kit/utilities";
import Tier from "./Tier";
import {Item} from "@/types/Item";

type SortableTierProps = {
    id: string;
    name: string;
    items: Item[];
    tierKey: string;
    onNameChange: (newName: string) => void;
    backgroundColor: string;
};

const SortableTier: React.FC<SortableTierProps> = ({id, name, items, onNameChange, backgroundColor}) => {
    const {
        attributes,
        listeners,
        setNodeRef,
        transform,
        transition,
        isDragging,
    } = useSortable({id});

    const style = {
        transform: CSS.Transform.toString(transform),
        transition,
        opacity: isDragging ? 0.8 : 1,
        cursor: "move",
    };

    return (
        <div ref={setNodeRef} style={style} {...attributes} {...listeners}>
            <Tier
                id={id}
                name={name}
                items={items}
                onNameChange={onNameChange}
                backgroundColor={backgroundColor}
            />
        </div>
    );
};

export default React.memo(SortableTier, (prev, next) => {
    if (prev.name !== next.name) return false;
    if (prev.items.length !== next.items.length) return false;
    for (let i = 0; i < prev.items.length; i++) {
        if (prev.items[i].id !== next.items[i].id) return false;
    }
    if (prev.backgroundColor !== next.backgroundColor) return false;
    return true;
});
