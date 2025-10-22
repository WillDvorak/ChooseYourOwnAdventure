import React from "react";
import { Container, Row, Col, Card } from "react-bootstrap";

import Textbox from "../content/Textbox";
import DisplayBox from "../content/DisplayBox";
import InventoryBox from "../content/InventoryBox";

const theme = {
        background: 'linear-gradient(135deg, #1a0933 0%, #2d1b4e 100%)',
        containerBorder: '3px solid #d4af37',
        cardBg: 'rgba(13, 5, 26, 0.9)',
        messageText: '#f0e6d2',
        messageBorder: '1px solid #4a3775',
        messageBg: 'rgba(74, 55, 117, 0.3)',
        buttonBg: '#d4af37',
        buttonText: '#1a0933',
        buttonHover: '#ffd700',
        inputBg: 'rgba(74, 55, 117, 0.5)',
        inputText: '#f0e6d2',
        inputBorder: '#d4af37',
        fontFamily: 'Georgia, serif',
        fontSize: '1.1rem',
        borderRadius: '12px',
    };


export default function AppLayout() {
    return <Container fluid>
        <Row>
            <Col lg={3} style={{padding: '0px'}}>
                <DisplayBox theme={theme}/>
                <InventoryBox theme={theme} />
            </Col>
            <Col lg={9} style={{padding: '0px'}}>
                <Textbox theme={theme}/>
            </Col>
        </Row>
    </Container>
}
