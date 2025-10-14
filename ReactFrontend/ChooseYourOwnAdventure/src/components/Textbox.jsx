import React, { useState } from "react";
import { Container, Card, Form, Button } from "react-bootstrap"
import 'bootstrap/dist/css/bootstrap.min.css';

const Textbox = () => {
    const [messages, setMessages] = useState(["HARDCODE: A group of giant rats approach you, what do you do?"]);
    const [input, setInput] = useState("");
    const [choices, setChoices] = useState([
        { id: 1, text: "Fight the rats", action: "fight" },
        { id: 2, text: "Run away", action: "run away" }
    ]);

    // Dark Fantasy Theme Colors
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
        inputBorder: '#d4af37'
    };

    const handleChoice = (action, choiceText) => {
        setMessages((prev) => [...prev, `> ${choiceText}`]);
        
        if (action === "fight") {
            setMessages((prev) => [...prev, "HARDCODE: you fight the rats and die"]);
            setChoices([]);
        }
        else if (action === "run away") {
            setMessages((prev) => [...prev, "HARDCODE: you escape from the rats"]);
            setChoices([]);
        }
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!input.trim()) return;
        setMessages((prev) => [...prev, input.trim()]);
        if (input.trim() == "fight") {
            setMessages((prev) => [...prev, "HARDCODE: you fight the rats and die"])
        }
        else if (input.trim() == "run away") {
            setMessages((prev) => [...prev, "HARDCODE you escape from the rats"])
        }
        setInput("");
    }

    return (
        <Container 
            fluid 
            className="d-flex flex-column justify-content-end" 
            style={{
                border: theme.containerBorder, 
                height: "100vh", 
                width: "80%", 
                background: theme.background,
                boxShadow: '0 0 50px rgba(212, 175, 55, 0.3)'
            }}
        >
            <Card style={{background: theme.cardBg, border: 'none', borderRadius: '12px', padding: '1.5rem'}}>
                <div style={{maxHeight: '60vh', overflowY: 'auto', marginBottom: '1rem'}}>
                    {messages.map((msg, i) => (
                        <p 
                            style={{
                                padding: "1rem", 
                                border: theme.messageBorder, 
                                background: theme.messageBg,
                                color: theme.messageText,
                                marginBottom: '0.5rem',
                                borderRadius: '8px',
                                fontFamily: 'Georgia, serif',
                                fontSize: '1.1rem',
                                lineHeight: '1.6'
                            }} 
                            key={i}
                        >
                            {msg}
                        </p>
                    ))}
                </div>
                
                {choices.length > 0 && (
                    <div style={{marginBottom: '1rem'}}>
                        {choices.map((choice) => (
                            <Button 
                                key={choice.id}
                                onClick={() => handleChoice(choice.action, choice.text)}
                                style={{
                                    display: "block", 
                                    width: "100%", 
                                    marginBottom: "0.5rem", 
                                    textAlign: "left",
                                    background: theme.buttonBg,
                                    color: theme.buttonText,
                                    border: 'none',
                                    padding: '1rem',
                                    fontWeight: 'bold',
                                    fontSize: '1.1rem',
                                    borderRadius: '8px',
                                    transition: 'all 0.3s ease'
                                }}
                                onMouseEnter={(e) => e.target.style.background = theme.buttonHover}
                                onMouseLeave={(e) => e.target.style.background = theme.buttonBg}
                            >
                                {choice.id}. {choice.text}
                            </Button>
                        ))}
                    </div>
                )}

                <Form onSubmit={handleSubmit}>
                    <Form.Group className="d-flex">
                        <Form.Control
                            type="text"
                            placeholder="Or type command..."
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            style={{
                                background: theme.inputBg,
                                color: theme.inputText,
                                border: `2px solid ${theme.inputBorder}`,
                                borderRadius: '8px 0 0 8px'
                            }}
                        />
                        <Button 
                            type="submit"
                            style={{
                                background: theme.buttonBg,
                                color: theme.buttonText,
                                border: 'none',
                                fontWeight: 'bold',
                                borderRadius: '0 8px 8px 0'
                            }}
                        >
                            Send
                        </Button>
                    </Form.Group>
                </Form>
            </Card>
        </Container>
    );
}

export default Textbox