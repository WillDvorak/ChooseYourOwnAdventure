import React from 'react';
import { Modal, Image } from 'react-bootstrap';

/**
 * 
 * @param {string} item - item object containing details to display
 * @param {callBackFunction} onClose - function to close the modal  
 * @returns a pop up modal displaying item details on click of inventory item
 */
export default function InventoryModal(props) {
    if (!item) return null;

    return (
        <Modal show={!!item} onHide={onClose} centered>
            <Modal.Header closeButton>
                <Modal.Title>{item.name}</Modal.Title>
            </Modal.Header>
            <Modal.Body className="text-center">
                <Image
                    src={item.largeImage || item.image}
                    alt={item.name}
                    fluid
                    style={{ maxHeight: '300px', marginBottom: '20px' }}
                />
                <p>{item.longDescription || item.description}</p>
            </Modal.Body>
            <Modal.Footer>
                <button className="btn btn-secondary" onClick={onClose}>
                    Close
                </button>
            </Modal.Footer>
        </Modal>
    );
}