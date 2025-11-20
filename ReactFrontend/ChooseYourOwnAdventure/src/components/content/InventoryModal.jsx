import React from 'react';
import { Modal, Image } from 'react-bootstrap';

export default function InventoryModal({ item, onClose }) {
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