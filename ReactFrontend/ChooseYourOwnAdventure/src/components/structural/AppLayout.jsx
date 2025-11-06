import { useState } from "react";
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

    // Passes down scene information to displaybox
    // Passes setSceneMeta to textbox
    const [sceneInfo, setSceneInfo] = useState(null)
    // Passes down inventory information to inventory box
    // Passes setInventory to Textbox 
    const [inventory, setInventory] = useState([])

    /**
     * Handles giving or taking an item from the user
     *
     * @param {string} item - The item to add or take from inventory
     * @param {boolean } isGiving - true if giving item to player, false if taking away
     * @returns {void} - updates inventory state variable
     */
    function handleSetInventory(item, isGiving = true) {
        isGiving ? setInventory([...inventory, item])
        :
        setInventory((prev) => {
            const newInv = prev.filter(invItem => invItem != item)
            setInventory(newInv)
        })

    }


    return <Container fluid>
        <Row>
            <Col lg={3} style={{padding: '0px'}}>
                <DisplayBox theme={theme} sceneInfo={sceneInfo}/>
                <InventoryBox theme={theme} inventory={inventory} />
            </Col>
            <Col lg={9} style={{padding: '0px'}}>
                <Textbox theme={theme} onSceneChange={setSceneInfo} handleInventory={handleSetInventory} />
            </Col>
        </Row>
    </Container>
}
