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
                background: props.theme.cardBg,
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
                                marginBottom: "0.5rem",
                                textAlign: "left",
                                background: props.theme.buttonBg,
                                color: props.theme.buttonText,
                                border: 'none',
                                padding: '1rem',
                                fontWeight: 'bold',
                                fontSize: '1.1rem',
                                borderRadius: '8px',
                                transition: 'all 0.3s ease'
                            }}
                            onMouseEnter={(e) => e.target.style.background = props.theme.buttonHover}
                            onMouseLeave={(e) => e.target.style.background = props.theme.buttonBg}
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