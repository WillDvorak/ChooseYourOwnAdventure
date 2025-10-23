import { Card, Container } from "react-bootstrap"

export default function DisplayBox(props) {


    return <Card
        style={{
            border: props.theme.containerBorder,
            borderRadius: props.theme.borderRadius,
            background: props.theme.cardBg,
            color: props.theme.messageText,
            textAlign: "center",
            fontFamily: props.theme.fontFamily,
        }}>
        <h2>HARDCODE: Biome</h2>
        <h3>HARDCODE</h3>
        <p>HARDCODE</p>
        <p>HARDCODE</p>
        <p>HARDCODE</p>
    </Card>


}