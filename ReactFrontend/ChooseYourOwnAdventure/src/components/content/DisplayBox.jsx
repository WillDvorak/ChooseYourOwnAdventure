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
        }}>
        {props.sceneInfo ? <>
            <h2>{props.sceneInfo.title}</h2>
            <h3>{props.sceneInfo.code}</h3>
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
            <h2>HARDCODE</h2>
            <h3>HARDCODE</h3>
            <HealthBar health={100} maxHealth={100} />
            <p>HARDCODE</p>
            <p>HARDCODE</p>
            <p>HARDCODE</p>
        </>
        }
        
    </Card>


}