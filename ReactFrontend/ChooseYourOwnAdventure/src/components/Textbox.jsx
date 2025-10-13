import React, { useState } from "react";
import { Container, Card, Form, Button } from "react-bootstrap"
import 'bootstrap/dist/css/bootstrap.min.css';


const Textbox = () => {

    const [messages, setMessages] = useState(["HARDCODE: A group of giant rats approach you, do you 'fight' or 'run away'?"]);
    const [input, setInput] = useState("");

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

    return <Container fluid className="d-flex flex-column justify-content-end" style={{border: "solid black 2px", height: "100vh", width: "80%", background: "grey"}}>
        <Card style={{background: "grey"}}>
            {messages.map((msg, i) => (
                <p style={{padding: "0px", border: "solid black 2px", color: "black"}}key={i}>{msg}</p>
              ))}
            <Form onSubmit={handleSubmit}>
            <Form.Group className="d-flex">
              <Form.Control
                type="text"
                placeholder="Enter command..."
                value={input}
                onChange={(e) => setInput(e.target.value)}
              />
              <Button type="submit">Send</Button>
            </Form.Group>
          </Form>
        </Card>
    </Container>
}

export default Textbox