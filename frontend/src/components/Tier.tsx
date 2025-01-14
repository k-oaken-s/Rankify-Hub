import React, {useState} from "react";
import {Item} from "@/types/Item";
import {Input, Typography} from "antd";
import DraggableItem from "./DraggableItem";
import DraggableArea from "./DroppableArea";
import {rectSortingStrategy, SortableContext} from "@dnd-kit/sortable";

const {Title} = Typography;

type TierProps = {
    id: string;
    name: string;
    items: Item[];
    onNameChange: (newName: string) => void;
    backgroundColor: string;
};

const Tier: React.FC<TierProps> = ({
                                       id,
                                       name,
                                       items,
                                       onNameChange,
                                       backgroundColor,
                                   }) => {
    const [isEditing, setIsEditing] = useState(false);
    const [currentName, setCurrentName] = useState(name);

    const handleSaveName = () => {
        if (currentName.trim()) {
            onNameChange(currentName);
        } else {
            setCurrentName(name);
        }
        setIsEditing(false);
    };

    const handleCancelEdit = () => {
        setCurrentName(name);
        setIsEditing(false);
    };

    return (
        <div
            className="flex rounded-md shadow-md"
            style={{
                backgroundColor,
                overflow: "hidden",
            }}
        >
            <div
                className="p-4"
                style={{
                    flex: "0 0 200px",
                    borderRight: "1px solid #444",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    backgroundColor,
                }}
            >
                {isEditing ? (
                    <Input
                        value={currentName}
                        onChange={(e) => setCurrentName(e.target.value)}
                        onBlur={handleSaveName}
                        onPressEnter={handleSaveName}
                        onKeyDown={(e) => {
                            if (e.key === "Escape") handleCancelEdit();
                        }}
                        autoFocus
                        style={{
                            fontSize: "32px",
                            fontWeight: "300",
                            backgroundColor,
                            color: "#E0E0E0",
                            borderRadius: "4px",
                            border: "none",
                            padding: "4px 8px",
                            width: "100%",
                        }}
                    />
                ) : (
                    <Title
                        level={4}
                        className="text-white"
                        onClick={() => setIsEditing(true)}
                        style={{
                            cursor: "pointer",
                            margin: "0",
                            textAlign: "center",
                            width: "100%",
                            color: "#E0E0E0",
                            fontSize: "32px",
                            fontWeight: "300",
                        }}
                    >
                        {name}
                    </Title>
                )}
            </div>

            <div
                className="flex-1 p-4"
                style={{
                    display: "flex",
                    flexDirection: "column",
                    minHeight: "150px",
                }}
            >
                <DraggableArea id={id} items={items}>
                    <SortableContext items={items.map((item) => item.id)} strategy={rectSortingStrategy}>
                        <div className="flex flex-wrap gap-4">
                            {items.map((item) => (
                                <DraggableItem key={item.id} item={item}/>
                            ))}
                        </div>
                    </SortableContext>
                </DraggableArea>
            </div>
        </div>
    );
};

export default Tier;
