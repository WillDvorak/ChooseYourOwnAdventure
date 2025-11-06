import { Card, Col, Row } from "react-bootstrap";

//Create a box that displays the current inventory items
export default function InventoryBox(props) {

    const inventory = props.inventory || [];
    let placeholderImg = 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ3vrTUU3CKbUDThpm8aZzFXdTmai6PodNfXA&s';

    // Icon mapping for different items (can be expanded)
    const itemIcons = {
        'torch': '🔥',
        'gold': '💰',
        'key': '🗝️',
        'map': '🗺️',
        'sword': '⚔️',
        'shield': '🛡️',
        'potion': '🧪'
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
                marginTop: '1rem'
            }}
        >
            <h1 style={{ 
                fontSize: '1.5rem', 
                padding: '1rem',
                borderBottom: `2px solid ${props.theme.containerBorder}`,
                marginBottom: '0'
            }}>
                Inventory
            </h1>
            
            {inventory.length === 0 ? (
                <div style={{ padding: '2rem', fontStyle: 'italic', opacity: 0.7 }}>
                    <p>Empty</p>
                    <p style={{ fontSize: '0.9rem' }}>Collect items as you explore...</p>
                </div>
            ) : (
                <Row xs={1} sm={2} className="g-3 p-3">
                    {inventory.map((item, i) => (
                        <Col key={i}>
                            <Card className="h-100" style={{
                                background: props.theme.messageBg,
                                border: `1px solid ${props.theme.messageBorder}`,
                                transition: 'transform 0.2s'
                            }}>
                                <Card.Body className="d-flex align-items-center gap-3">
                                    <div style={{ 
                                        fontSize: '2rem',
                                        width: 64,
                                        height: 64,
                                        display: 'flex',
                                        alignItems: 'center',
                                        justifyContent: 'center',
                                        background: props.theme.buttonBg,
                                        borderRadius: '8px'
                                    }}>
                                        {itemIcons[item.toLowerCase()] || '📦'}
                                    </div>
                                    <h2 className="h5 m-0" style={{ 
                                        color: props.theme.buttonBg,
                                        textTransform: 'capitalize'
                                    }}>
                                        {item}
                                    </h2>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>
            )}
        </Card>
    );
}