import React from 'react';
import { Modal, Image, Row, Col } from 'react-bootstrap';

import "./InventoryModalStyles.css";

/**
 * 
 * @param {string} item - item object containing details to display
 * @param {callBackFunction} onClose - function to close the modal  
 * @param {jsObject} props.theme -> js object containing theme attributes
 * @param {dictionary} itemImages - dictionary of itemImages for each inventory item
 * @returns a pop up modal displaying item details on click of inventory item
 */
export default function InventoryModal({ item, onClose, theme, itemImages }) {
    if (!item) return null;

    const getItemImage = (label) => {
        if (!label) return null;
        const normalized = label.trim().toLowerCase();
        const img = itemImages[normalized];
        return img ?? null;
    };

    const modalTheme = theme;

    return <div>
        <Modal
            show={!!item}
            onHide={onClose}
            size="lg"
            centered                      
            dialogClassName="custom-modal-dialog"
            contentClassName="custom-modal-content"
            backdropClassName="custom-modal-backdrop"
        >
            <Modal.Header closeButton>
                <Modal.Title>{item.title}</Modal.Title>
            </Modal.Header>
            <Modal.Body className="text-center">
                <Row>
                    <Col>
                        <Image
                            src={getItemImage(item.label)}
                            alt={`image of ${item.label}`}
                            fluid
                            style={{ height: '300px', width: "300px", objectFit: "cover", objectPosition: "center", marginBottom: '20px' }}
                        />
                    </Col>
                    <Col>
                        <p>{item.description}</p>
                    </Col>
                </Row>


            </Modal.Body>
            <Modal.Footer>
                <button className="btn btn-secondary" onClick={onClose}>
                    Close
                </button>
            </Modal.Footer>
        </Modal>
    </div>
}