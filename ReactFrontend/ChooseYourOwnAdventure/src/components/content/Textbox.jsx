import React, { useState, useEffect } from "react";
import { Container, Card, Form, Button } from "react-bootstrap"
import useStorage from "../../hooks/useStorage";
import 'bootstrap/dist/css/bootstrap.min.css';

const Textbox = (props) => {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState("");
    const [choices, setChoices] = useState([]);
    const [loading, setLoading] = useState(true);
    const [sessionId, setSessionId] = useState(null);

    const API_BASE = "/api/game";

    // Load the intro scene on component mount
    useEffect(() => {
        initializeGame();
    }, []);

    const initializeGame = async () => {
        try {
            // Create a new game session
            const response = await fetch(`${API_BASE}/session/create?playerName=Adventurer&startingScene=intro`, {
                method: 'POST'
            });
            
            if (!response.ok) {
                throw new Error('Failed to create session');
            }
            
            const sessionData = await response.json();
            setSessionId(sessionData.sessionId);
            
            // Load the starting scene with the new session
            loadScene("intro", sessionData.sessionId);
        } catch (error) {
            console.error('Error initializing game:', error);
            // Fallback to loading scene without session
            loadScene("intro", null);
        }
    };

    const loadScene = async (sceneCode, sessId = sessionId) => {
        setLoading(true);
        try {
            // Include sessionId in the request to get health data
            const url = sessId 
                ? `${API_BASE}/scene/${sceneCode}?sessionId=${sessId}`
                : `${API_BASE}/scene/${sceneCode}`;
            
            const response = await fetch(url);

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();

            // Set the scene body as the message
            setMessages((prev) => [...prev, data.body]);

            // Set sceneInfo for displaybox (includes health data)
            props.onSceneChange(data)


            // Set choices from the scene
            if (data.choices && data.choices.length > 0) {
                const formattedChoices = data.choices.map((choice) => ({
                    id: choice.id,
                    text: choice.label,
                    targetScene: choice.targetSceneCode,
                    requiresFlag: choice.requiresFlag,
                    setsFlag: choice.setsFlag
                }));
                setChoices(formattedChoices);


            } else {
                // No choices means it's a terminal scene
                setChoices([]);
            }
        } catch (error) {
            console.error('Error loading scene:', error);
            setMessages(["⚠️ Error: Could not connect to the realm. Is the backend running?"]);
        } finally {
            setLoading(false);
        }
    };

    const handleChoice = async (choice) => {
        setMessages((prev) => [...prev, `> ${choice.text}`]);
        setLoading(true);

        try {
            if (sessionId) {
                // Use the backend to process the choice (updates health, flags, etc.)
                const response = await fetch(`${API_BASE}/session/${sessionId}/choice/${choice.id}`, {
                    method: 'POST'
                });
                
                if (!response.ok) {
                    throw new Error('Failed to process choice');
                }
                
                const data = await response.json();
                
                // Update the UI with the new scene data (includes updated health)
                setMessages((prev) => [...prev, data.body]);
                props.onSceneChange(data);
                
                // Update choices
                if (data.choices && data.choices.length > 0) {
                    const formattedChoices = data.choices.map((choice) => ({
                        id: choice.id,
                        text: choice.label,
                        targetScene: choice.targetSceneCode,
                        requiresFlag: choice.requiresFlag,
                        setsFlag: choice.setsFlag
                    }));
                    setChoices(formattedChoices);
                } else {
                    setChoices([]);
                }
                
                // Handle inventory for display purposes
                if (choice.setsFlag && choice.setsFlag !== "" && !choice.setsFlag.startsWith("health:")) {
                    props.handleInventory(choice.setsFlag, true);
                }
            } else {
                // Fallback to old behavior if no session
                if (choice.setsFlag && choice.setsFlag !== "") {
                    props.handleInventory(choice.setsFlag, true);
                }
                await loadScene(choice.targetScene);
            }
        } catch (error) {
            console.error('Error processing choice:', error);
            setMessages((prev) => [...prev, "⚠️ Error processing your choice"]);
        } finally {
            setLoading(false);
        }
    }

    // const handleSubmit = async (e) => {
    //     e.preventDefault();
    //     if (!input.trim()) return;

    //     const userInput = input.trim().toLowerCase();
    //     setMessages((prev) => [...prev, `> ${input.trim()}`]);
    //     setInput("");

    //     // Check if user wants to load a different scene by code
    //     if (["intro", "forest", "cave", "treasure"].includes(userInput)) {
    //         await loadScene(userInput);
    //     } else {
    //         setMessages((prev) => [...prev, `Unknown command. Try scene names like "intro", "forest", "cave".`]);
    //     }
    // }

    return (
        // <Container 
        //     fluid 
        //     className="d-flex flex-column justify-content-end" 
        //     style={{
        //         border: props.theme.containerBorder, 
        //         height: "100vh",  
        //         background: props.theme.background,
        //         boxShadow: '0 0 50px rgba(212, 175, 55, 0.3)'
        //     }}
        // >
        <Card className="d-flex flex-column justify-content-end"
            style={{
                background: `
                    linear-gradient(135deg, rgba(13, 5, 26, 0.95), rgba(13, 5, 26, 0.85)),
                    repeating-linear-gradient(
                        0deg,
                        transparent,
                        transparent 40px,
                        rgba(212, 175, 55, 0.03) 40px,
                        rgba(212, 175, 55, 0.03) 41px
                    ),
                    repeating-linear-gradient(
                        90deg,
                        transparent,
                        transparent 40px,
                        rgba(212, 175, 55, 0.03) 40px,
                        rgba(212, 175, 55, 0.03) 41px
                    )
                `,
                border: props.theme.containerBorder,
                height: "100vh",
                borderRadius: '12px',
                padding: '1.5rem'
            }}>
            <div style={{ maxHeight: '80vh', overflowY: 'auto', marginBottom: '1rem' }}>
                {messages.map((msg, i) => (
                    <p
                        style={{
                            padding: "1rem",
                            border: props.theme.messageBorder,
                            background: props.theme.messageBg,
                            color: props.theme.messageText,
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

            {loading && (
                <div style={{
                    textAlign: 'center',
                    padding: '1rem',
                    color: props.theme.messageText,
                    fontFamily: 'Georgia, serif'
                }}>
                    <div className="spinner-border text-warning" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </div>
                    <p style={{ marginTop: '0.5rem' }}>Consulting the ancient scrolls...</p>
                </div>
            )}

            {choices.length > 0 && !loading && (
                <div style={{ marginBottom: '1rem' }}>
                    {choices.map((choice, index) => (
                        <Button
                            key={choice.id}
                            onClick={() => handleChoice(choice)}
                            disabled={loading}
                            style={{
                                display: "block",
                                width: "100%",
                                marginBottom: "0.75rem",
                                textAlign: "left",
                                background: "linear-gradient(135deg, #d4af37, #f6e27a)",
                                color: "#1a0933",
                                border: "1px solid #f6e27a",
                                padding: "1rem 1.4rem",
                                fontWeight: "bold",
                                fontSize: "1.05rem",
                                borderRadius: "10px",
                                boxShadow: "0 4px 10px rgba(0, 0, 0, 0.5)",
                                transition: "transform 0.12s ease, box-shadow 0.12s ease, background 0.2s ease",
                                cursor: "pointer"
                            }}
                            onMouseEnter={(e) => {
                                e.target.style.transform = "translateY(-2px)";
                                e.target.style.boxShadow = "0 6px 16px rgba(0,0,0,0.7)";
                            }}
                            onMouseLeave={(e) => {
                                e.target.style.transform = "translateY(0)";
                                e.target.style.boxShadow = "0 4px 10px rgba(0,0,0,0.5)";
                            }}
                            onMouseDown={(e) => {
                                e.target.style.transform = "translateY(1px) scale(0.99)";
                            }}
                            onMouseUp={(e) => {
                                e.target.style.transform = "translateY(-2px)";
                            }}
                        >
                            {index + 1}. {choice.text}
                        </Button>
                    ))}
                </div>
            )}

            {/* <Form onSubmit={handleSubmit}>
                <Form.Group className="d-flex">
                    <Form.Control
                        type="text"
                        placeholder="Type a command or scene name (intro, forest, cave)..."
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        disabled={loading}
                        style={{
                            background: props.theme.inputBg,
                            color: props.theme.inputText,
                            border: `2px solid ${props.theme.inputBorder}`,
                            borderRadius: '8px 0 0 8px'
                        }}
                    />
                    <Button
                        type="submit"
                        disabled={loading}
                        style={{
                            background: props.theme.buttonBg,
                            color: props.theme.buttonText,
                            border: 'none',
                            fontWeight: 'bold',
                            borderRadius: '0 8px 8px 0'
                        }}
                    >
                        Send
                    </Button>
                </Form.Group>
            </Form> */}
        </Card>
    );
}

export default Textbox