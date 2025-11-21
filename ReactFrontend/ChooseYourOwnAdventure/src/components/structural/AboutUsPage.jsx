import { Container, Card } from "react-bootstrap"

export default function AboutUsPage() {
    return <Container>
        <Card>
            <h1>About Us:</h1>
            <h2>Controlled Chaos</h2>
            <p> 
                This application is a project designed and implemented by a team of students in 
                CS506: Software Engineering at the University of Wisconsin - Madison during the
                fall 2025 semester.
            </p>
            <p>
                This project was assigned to us as semester long project, in order to understand and
                practice various different real world procedures within the realm of software engineering. 
                As a team we practiced various Scrum and Agile principles, version control through GitLab, 
                fullstack integration, containerization, pipelines, and more.
            </p>
        </Card>
    </Container>
}