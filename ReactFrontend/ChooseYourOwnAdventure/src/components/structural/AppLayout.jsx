import React, { useState, useEffect } from "react";
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
    const [sessionId, setSessionId] = useState(null);
    const [inventory, setInventory] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Start a new game session on mount
        const startSession = async () => {
            try {
                const response = await fetch('/api/game/session/start', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ playerName: 'Player' })
                });
                
                const data = await response.json();
                setSessionId(data.sessionId);
                
                // Initialize inventory from flags
                const flagsArray = Object.keys(data.flags || {}).filter(f => data.flags[f]);
                setInventory(flagsArray);
            } catch (error) {
                console.error('Error starting session:', error);
            } finally {
                setLoading(false);
            }
        };
        
        startSession();
    }, []);

    const handleInventoryUpdate = (flags) => {
        // Convert flags object to array of flag names where value is true
        const flagsArray = Object.keys(flags || {}).filter(f => flags[f]);
        setInventory(flagsArray);
    };

    if (loading) {
        return (
            <Container fluid style={{ 
                height: '100vh', 
                display: 'flex', 
                alignItems: 'center', 
                justifyContent: 'center',
                background: theme.background,
                color: theme.messageText 
            }}>
                <div style={{ textAlign: 'center' }}>
                    <div className="spinner-border text-warning" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </div>
                    <p style={{ marginTop: '1rem', fontFamily: theme.fontFamily }}>
                        Initializing your adventure...
                    </p>
                </div>
            </Container>
        );
    }

    return <Container fluid>
        <Row>
            <Col lg={3} style={{padding: '0px'}}>
                <DisplayBox theme={theme}/>
                <InventoryBox theme={theme} inventory={inventory} />
            </Col>
            <Col lg={9} style={{padding: '0px'}}>
                <Textbox 
                    theme={theme} 
                    sessionId={sessionId}
                    onInventoryUpdate={handleInventoryUpdate}
                />
            </Col>
        </Row>
    </Container>
}
