import { Card, Col, Row } from "react-bootstrap";
import { useState, useEffect, useMemo } from "react";

import InventoryModal from "./InventoryModal";

import AI_Key from "/figures/AI_Key.png"
import AI_Gold from "/figures/AI_Gold.png"
import AI_Amulet from "/figures/AI_Amulet.png"
import AI_Knowledge from "/figures/AI_Knowledge.png"
import AI_Map from "/figures/AI_Map.png"
import AI_Potion from "/figures/AI_Potion.png"
import AI_Sword from "/figures/AI_Sword.png"
import AI_Torch from "/figures/AI_Torch.png"

/**
 * 
 * @param {jsObject} props.theme -> js object containing theme attributes
 * @param {list<String>} props.inventory -> list of inventory item strings
 * @returns 
 */
export default function InventoryBox(props) {

    const [allInventoryItems, setAllInventoryItems] = useState([]);

    const [selectedItem, setSelectedItem] = useState(null);

    //calls api only on first render
    async function initializeItemList() {
        // TODO: REMOVE LOCALHOST:8080
        const resp = await fetch("http://localhost:8080/api/items/all");
        const data = await resp.json();
        setAllInventoryItems(data);
    }

    // Dictionary of Images
    const itemImageMap = {
        torch: AI_Torch,
        key: AI_Key,
        map: AI_Map,
        amulet: AI_Amulet,
        knowledge: AI_Knowledge,
        potion: AI_Potion,
        sword: AI_Sword,
        gold: AI_Gold,
    };

    const getItemImage = (label) => {
        if (!label) return null;
        const normalized = label.trim().toLowerCase();
        const img = itemImageMap[normalized];
        return img ?? null;
    };

    const itemByLabel = Object.fromEntries(
        allInventoryItems.map(item => [item.label, item])
    );

    useEffect(() => {
        initializeItemList();
    }, [])





    const inventory = props.inventory ?? []
    let placeholderImg = 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ3vrTUU3CKbUDThpm8aZzFXdTmai6PodNfXA&s'

    return (

        <Card
            style={{
                border: props.theme.containerBorder,
                borderRadius: props.theme.borderRadius,
                background: props.theme.cardBg,
                color: props.theme.messageText,
                textAlign: "center",
                fontFamily: props.theme.fontFamily,
                minHeight: 300,
            }}
        >
            <h1>Inventory:</h1>
            {inventory.length > 0 ?
                <Row xs={1} sm={2} className="g-3 p-3">
                    {inventory.map((item, i) => {
                        const imgSrc = getItemImage(item);
                        const fullItem = itemByLabel[item];

                        return <Col key={i}>
                            <Card className="h-100" onClick={() => { setSelectedItem(fullItem) }} style={{
                                border: props.theme.containerBorder,
                                borderRadius: props.theme.borderRadius,
                                background: props.theme.cardBg,
                                color: props.theme.messageText,
                            }}>
                                <Card.Body className="d-flex align-items-center gap-1">
                                    <img
                                        src={imgSrc}
                                        alt={`Image of ${item}`}
                                        className="img-fluid"
                                        style={{ width: 64, height: 64, objectFit: "cover" }}
                                    />
                                    <p className="m-0">{String(item).charAt(0).toUpperCase() + String(item).slice(1)}</p>
                                </Card.Body>
                            </Card>
                        </Col>
                    })
                    }
                </Row>
                :
                <p>You have nothing in your inventory...</p>}
            {selectedItem && (
                <InventoryModal item={selectedItem} onClose={() => setSelectedItem(null)} itemImages={itemImageMap} />
            )}
        </Card>
    );
}