import React, {createContext, useContext, useRef} from "react";
import {Item} from "@/types/Item";

type ItemsMapContextType = {
    itemsMapRef: React.MutableRefObject<Map<string, Item>>;
};

const ItemsMapContext = createContext<ItemsMapContextType | null>(null);

export const ItemsMapProvider: React.FC<{ children: React.ReactNode }> = ({children}) => {
    // itemsMapをuseRefで保持し、再レンダリングしない
    const itemsMapRef = useRef(new Map<string, Item>());
    return <ItemsMapContext.Provider value={{itemsMapRef}}>{children}</ItemsMapContext.Provider>;
};

export const useItemsMap = () => {
    const ctx = useContext(ItemsMapContext);
    if (!ctx) throw new Error("useItemsMap must be used inside ItemsMapProvider");
    return ctx.itemsMapRef;
};
