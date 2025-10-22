import { Card, Col, Row } from "react-bootstrap";

//Create a box that displays the current inventory items
export default function InventoryBox(props) {



    const hardCodeInventory = ['torch', 'rock', 'stick', 'etc'];
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
            }}
        >
            <h1>Inventory:</h1>
            <Row xs={1} sm={2} className="g-3 p-3">
                {hardCodeInventory.map((item, i) => (
                    <Col key={i}>
                        <Card className="h-100">
                            <Card.Body className="d-flex align-items-center gap-3">
                                <img
                                    src={placeholderImg}
                                    alt={`${item} placeholder`}
                                    className="img-fluid"
                                    style={{ width: 64, height: 64, objectFit: "cover" }}
                                />
                                <h2 className="h5 m-0">{item}</h2>
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
            </Row>
        </Card>
    );
}