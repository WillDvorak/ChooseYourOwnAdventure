import { Card, Col, Row } from "react-bootstrap";

//Create a box that displays the current inventory items
export default function InventoryBox(props) {

    const inventory = props.inventory ?? []
    
    // Map items to their emoji icons
    const getItemIcon = (itemName) => {
        const icons = {
            'torch': '🔦',
            'gold': '💰',
            'key': '🔑',
            'sword': '⚔️',
            'shield': '🛡️',
            'potion': '🧪',
            'map': '🗺️',
            'compass': '🧭',
            'food': '🍖',
            'water': '💧',
            'rope': '🪢',
            'coins': '🪙',
            'gem': '💎',
            'book': '📖',
            'scroll': '📜',
        };
        return icons[itemName.toLowerCase()] || '📦';
    };

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
                {inventory.map((item, i) => (
                    <Col key={i}>
                        <Card className="h-100" style={{
                            background: 'rgba(212, 175, 55, 0.1)',
                            border: '2px solid rgba(212, 175, 55, 0.3)',
                            boxShadow: '0 4px 8px rgba(0, 0, 0, 0.3)'
                        }}>
                            <Card.Body className="d-flex align-items-center gap-3">
                                <div style={{
                                    fontSize: '3rem',
                                    width: 64,
                                    height: 64,
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center',
                                    background: 'rgba(0, 0, 0, 0.3)',
                                    borderRadius: '8px',
                                    boxShadow: 'inset 0 2px 4px rgba(0, 0, 0, 0.5)'
                                }}>
                                    {getItemIcon(item)}
                                </div>
                                <h2 className="h5 m-0" style={{ 
                                    color: '#d4af37',
                                    textTransform: 'capitalize',
                                    fontWeight: 'bold'
                                }}>
                                    {item}
                                </h2>
                            </Card.Body>
                        </Card>
                    </Col>
                ))
                }
            </Row>
            :
            <p>You have nothing in your inventory...</p>}
        </Card>
    );
}