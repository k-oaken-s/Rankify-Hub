"use client";

import {Item} from "@/types/Item";
import {getAnonymousId} from "@/utils/getAnonymousId";
import {
    DndContext,
    DragEndEvent,
    DragOverlay,
    DragStartEvent,
    PointerSensor,
    rectIntersection,
    useSensor,
    useSensors,
} from "@dnd-kit/core";
import {arrayMove, rectSortingStrategy, SortableContext} from "@dnd-kit/sortable";
import {useState} from "react";
import {Input, message, Typography} from "antd";
import SortableTier from "./SortableTier";
import DraggableItem from "./DraggableItem";
import {getImageUrl} from "@/utils/getImageUrl";
import Tier from "@/app/categories/[id]/TierCreationScreen/Tier";
import ImageWrapper from "@/components/ImageWrapper";
import {getApiBaseUrl} from "@/utils/getApiBaseUrl";
import axios from "axios";

const {Text, Title} = Typography;

// Tier の構造を型定義
type Tier = {
    name: string;
    items: Item[];
};

type Tiers = Record<string, Tier>;

type TierCreationScreenProps = {
    items: Item[];
    categoryId: string;
    categoryName: string;
    categoryImageUrl: string;
};

const TierCreationScreen: React.FC<TierCreationScreenProps> = ({
                                                                   items,
                                                                   categoryId,
                                                                   categoryName,
                                                                   categoryImageUrl,
                                                               }) => {
    const initialTiers: Tiers = {
        Tier1: {name: "Tier 1", items: []},
        Tier2: {name: "Tier 2", items: []},
        Tier3: {name: "Tier 3", items: []},
        Tier4: {name: "Tier 4", items: []},
    };

    const [tiers, setTiers] = useState<Tiers>(initialTiers);
    const [tierOrder, setTierOrder] = useState<string[]>(Object.keys(initialTiers));

    const [tierName, setTierName] = useState<string>(""); // Tier全体の名前
    const [availableItems, setAvailableItems] = useState<Item[]>(items);
    const [activeId, setActiveId] = useState<string | null>(null);
    const [activeTierId, setActiveTierId] = useState<string | null>(null);
    const [generatedUrl, setGeneratedUrl] = useState<string | null>(null);
    const [isGenerating, setIsGenerating] = useState(false);

    const sensors = useSensors(
        useSensor(PointerSensor, {activationConstraint: {distance: 5}})
    );

    const anonymousId = getAnonymousId();

    const tierColors: Record<string, string> = {
        Tier1: "#2A1536",
        Tier2: "#3A1A1A",
        Tier3: "#3A2A15",
        Tier4: "#152641",
        Tier5: "#153A29",
        default: "#2D2D2D", // グレー背景 (Tier6以降)
        unassigned: "#8A8A8A",
    };


    const findItemById = (id: string): Item | null => {
        const allItems = [...availableItems, ...Object.values(tiers).flatMap((tier) => tier.items)];
        return allItems.find((item) => item.id === id) || null;
    };

    const handleDragStart = (event: DragStartEvent) => {
        const {active} = event;
        if (tierOrder.includes(active.id as string)) {
            // Tierをドラッグ開始
            setActiveTierId(active.id as string);
        } else {
            // アイテムをドラッグ開始
            setActiveId(active.id as string);
        }
    };

    const handleDragEnd = (event: DragEndEvent) => {
        const {active, over} = event;

        if (!over || !active) {
            setActiveId(null);
            setActiveTierId(null);
            return;
        }

        if (activeTierId && tierOrder.includes(active.id as string)) {
            // Tier全体の移動処理
            if (active.id !== over.id) {
                const oldIndex = tierOrder.indexOf(active.id as string);
                const newIndex = tierOrder.indexOf(over.id as string);
                setTierOrder((items) => arrayMove(items, oldIndex, newIndex));
            }
            setActiveTierId(null);
            return;
        }

        if (activeId) {
            // アイテムの移動処理
            const activeItem = findItemById(active.id as string);
            if (!activeItem) {
                setActiveId(null);
                return;
            }

            // 移動元と移動先のTierを特定
            const sourceTier = Object.keys(tiers).find((key) =>
                tiers[key].items.some((item) => item.id === active.id)
            ) || "unassigned";

            const destinationTier = Object.keys(tiers).find((key) =>
                tiers[key].items.some((item) => item.id === over.id)
            ) || over.id;

            console.log("Items in sourceTier:", sourceTier);
            console.log("active.id:", active.id);
            console.log("over.id:", over.id);


            if (sourceTier === destinationTier) {
                // 同じTier内での並び替え処理
                const oldIndex = tiers[sourceTier].items.findIndex((item) => item.id === active.id);
                const newIndex = tiers[sourceTier].items.findIndex((item) => item.id === over.id);

                if (oldIndex !== -1 && newIndex !== -1 && oldIndex !== newIndex) {
                    setTiers((prev) => ({
                        ...prev,
                        [sourceTier]: {
                            ...prev[sourceTier],
                            items: arrayMove(prev[sourceTier].items, oldIndex, newIndex),
                        },
                    }));
                }
            } else {
                // 異なるTier間の移動処理
                if (sourceTier === "unassigned") {
                    setAvailableItems((prev) => prev.filter((item) => item.id !== active.id));
                } else {
                    setTiers((prev) => ({
                        ...prev,
                        [sourceTier]: {
                            ...prev[sourceTier],
                            items: prev[sourceTier].items.filter((item) => item.id !== active.id),
                        },
                    }));
                }

                if (destinationTier === "unassigned") {
                    setAvailableItems((prev) => [...prev, activeItem]);
                } else if (tiers[destinationTier]) {
                    const newIndex = tiers[destinationTier as string].items.findIndex((item) => item.id === (over?.id as string));
                    // 異なるTier間の移動処理
                    const updatedDestinationItems = [...tiers[destinationTier as string].items];


                    if (newIndex !== -1) {
                        updatedDestinationItems.splice(newIndex, 0, activeItem);
                    } else {
                        updatedDestinationItems.push(activeItem);
                    }


                    setTiers((prev) => ({
                        ...prev,
                        [destinationTier as string]: {
                            ...prev[destinationTier as string],
                            items: updatedDestinationItems,
                        },
                    }));
                }
            }

            setActiveId(null);
        }
    };


    const handleTierNameChange = (tierKey: string, newName: string) => {
        setTiers((prev) => ({
            ...prev,
            [tierKey]: {
                ...prev[tierKey],
                name: newName,
            },
        }));
    };

    const generateTierUrl = async (isPublic: boolean) => {
        if (!tierName.trim()) {
            message.error("Tier全体の名前を入力してください");
            return;
        }

        setIsGenerating(true);
        try {
            const levels = tierOrder.map((tierKey, index) => ({
                name: tiers[tierKey].name,
                orderIndex: index + 1,
                items: tiers[tierKey].items.map((item, itemIndex) => ({
                    itemId: item.id,
                    orderIndex: itemIndex + 1,
                })),
            }));

            const response = await axios.post(`${getApiBaseUrl()}/user-tiers`, {
                anonymousId,
                categoryId,
                name: tierName,
                isPublic,
                levels,
            });

            const data = response.data;
            setGeneratedUrl(data.accessUrl);

            navigator.clipboard.writeText(data.accessUrl);
            message.success("URLが生成されクリップボードにコピーされました");
        } catch (error: unknown) {
            if (axios.isAxiosError(error)) {
                if (error.response) {
                    message.error(`URLの生成に失敗しました (ステータス: ${error.response.status})`);
                } else {
                    message.error("サーバーからのレスポンスがありません");
                }
            } else if (error instanceof Error) {
                message.error(`エラー: ${error.message}`);
            } else {
                message.error("不明なエラーが発生しました");
            }
        } finally {
            setIsGenerating(false);
        }
    };

    return (
        <DndContext
            sensors={sensors}
            collisionDetection={rectIntersection}
            onDragStart={handleDragStart}
            onDragEnd={handleDragEnd}
        >
            <div className="text-center mb-8">
                <div
                    className="flex justify-center items-center p-6 mb-6">
                    <div
                        className="flex items-center justify-center max-w-full max-h-full rounded-lg shadow-md overflow-hidden bg-transparent">
                        <ImageWrapper
                            src={getImageUrl(categoryImageUrl)}
                            alt={categoryName}
                            className="object-cover w-full h-full"
                        />
                    </div>


                    <h2 className="text-gray-300 text-3xl">
                        {categoryName}
                    </h2>
                </div>
                <label htmlFor="tierNameInput" className="block text-lg font-medium text-gray-300 mb-2">
                    Your Tier Name
                </label>
                <Input
                    id="tierNameInput"
                    value={tierName}
                    onChange={(e) => setTierName(e.target.value)}
                    placeholder="例: 最強キャラランキング"
                    style={{
                        width: "100%",
                        height: "45px",
                        fontSize: "16px",
                        backgroundColor: "#444",
                        color: "#fff",
                        borderRadius: "6px",
                        border: "1px solid #666",
                        padding: "0 10px",
                    }}
                    className="focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 transition"
                />
                {/*</div>*/}
            </div>

            <SortableContext items={tierOrder} strategy={rectSortingStrategy}>
                <div
                    style={{
                        display: "flex",
                        flexDirection: "column",
                        gap: "8px", // 見た目を改善するために調整
                    }}
                >
                    {tierOrder.map((tierKey) => (
                        <SortableTier
                            key={tierKey}
                            id={tierKey} // ドラッグ可能にするために id を渡す
                            tierKey={tierKey} // 必要に応じて追加情報を渡す
                            name={tiers[tierKey].name}
                            items={tiers[tierKey].items}
                            onNameChange={(newName) => handleTierNameChange(tierKey, newName)}
                            backgroundColor={tierColors[tierKey] || tierColors.default}
                        />
                    ))}
                </div>
            </SortableContext>


            <div
                className="mt-8 p-4 rounded-md shadow-md"
                style={{
                    backgroundColor: tierColors.unassigned,
                    minHeight: "150px",
                }}
            >
                <h3 className="text-lg font-semibold mb-4" style={{color: "#333"}}>
                    未割り当てアイテム
                </h3>
                <div className="flex gap-4 flex-wrap">
                    {availableItems.map((item) => (
                        <DraggableItem key={item.id} item={item}/>
                    ))}
                </div>
            </div>

            <div className="mt-8 text-center">
                <button
                    onClick={() => generateTierUrl(true)}
                    disabled={isGenerating}
                    className={`mb-4 mr-4 px-6 py-3 rounded-lg font-semibold text-base transition duration-200 ${
                        isGenerating
                            ? 'bg-gray-700 text-gray-500 cursor-not-allowed'
                            : 'bg-gray-800 text-white hover:bg-gray-700'
                    }`}
                >
                    公開してURL生成
                </button>
                <button
                    onClick={() => generateTierUrl(false)}
                    disabled={isGenerating}
                    className={`px-6 py-3 rounded-lg font-semibold text-base transition duration-200 ${
                        isGenerating
                            ? 'bg-gray-700 text-gray-500 cursor-not-allowed'
                            : 'bg-gray-800 text-white hover:bg-gray-700'
                    }`}
                >
                    非公開でURL生成
                </button>
                {generatedUrl && (
                    <div className="mt-6">
                        <p className="text-gray-400">
                            生成されたURL:{" "}
                            <a
                                href={generatedUrl}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="text-blue-400 underline hover:text-blue-300"
                            >
                                {generatedUrl}
                            </a>
                        </p>
                    </div>
                )}
            </div>


            <DragOverlay>
                {activeTierId ? (
                    <Tier
                        id={activeTierId}
                        name={tiers[activeTierId].name}
                        items={tiers[activeTierId].items}
                        onNameChange={(newName) => handleTierNameChange(activeTierId, newName)}
                        backgroundColor={tierColors[activeTierId]}
                    />
                ) : activeId ? (
                    (() => {
                        const item = findItemById(activeId);
                        return item ? <DraggableItem item={item} isOverlay/> : null;
                    })()
                ) : null}
            </DragOverlay>
        </DndContext>
    );
};

export default TierCreationScreen;
