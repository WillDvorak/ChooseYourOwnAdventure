import { Card } from "react-bootstrap"
import HealthBar from "./HealthBar"

export default function DisplayBox(props) {


    return <Card
        style={{
            border: props.theme.containerBorder,
            borderRadius: props.theme.borderRadius,
            background: props.theme.cardBg,
            color: props.theme.messageText,
            textAlign: "center",
            fontFamily: props.theme.fontFamily,
            overflow: "hidden",
            boxShadow: "0 8px 24px rgba(0, 0, 0, 0.8)"
        }}>
        {/* Golden Header Band */}
        <div style={{
            background: "linear-gradient(90deg, #d4af37, #f6e27a, #d4af37)",
            padding: "0.8rem 1rem",
            textTransform: "uppercase",
            letterSpacing: "0.12em",
            fontWeight: "bold",
            fontSize: "0.85rem",
            color: "#1a0933",
            boxShadow: "0 2px 8px rgba(0, 0, 0, 0.3)"
        }}>
            ⚔️ Character Info ⚔️
        </div>
        
        {/* Content Area */}
        <div style={{ padding: "1rem" }}>
            {props.sceneInfo ? <>
                <h2 style={{ marginTop: "0.5rem", marginBottom: "0.3rem", fontSize: "1.5rem" }}>
                    {props.sceneInfo.title}
                </h2>
                <h3 style={{ 
                    fontSize: "1rem", 
                    opacity: 0.7, 
                    marginTop: "0",
                    marginBottom: "1rem",
                    fontStyle: "italic"
                }}>
                    {props.sceneInfo.code}
                </h3>
                <HealthBar 
                    health={props.sceneInfo.health || 100} 
                    maxHealth={props.sceneInfo.maxHealth || 100} 
                />
                <p>HARDCODE</p>
                <p>HARDCODE</p>
                <p>HARDCODE</p>
            </>
            :
            <>
                <h2 style={{ marginTop: "0.5rem" }}>HARDCODE</h2>
                <h3>HARDCODE</h3>
                <HealthBar health={100} maxHealth={100} />
                <p>HARDCODE</p>
                <p>HARDCODE</p>
                <p>HARDCODE</p>
            </>
            }
        </div>
    </Card>


}