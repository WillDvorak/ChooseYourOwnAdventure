# TextQuest - Echoes of the Shattered Realms

**Team**: Controlled Chaos

An interactive text-based choose-your-own-adventure game where players navigate through a fractured world of five realms, making choices that affect their journey, health, inventory, and ultimately, the game's ending.

## Quick Links

- **[Full Documentation](DOCUMENTATION.md)** - Complete project documentation
- **[API Reference](API_REFERENCE.md)** - Quick API endpoint reference
- **[Docker Setup Guide](DOCKER.md)** - Docker deployment instructions
- **[Game Documentation](sql/GAME_DOCUMENTATION.md)** - Detailed game design and mechanics
- **[Style Guide](STYLE.md)** - Coding standards and conventions

## Quick Start

### Using Docker (Recommended)

```bash
docker-compose -f docker-compose-full.yml up --build
```

Then access:
- **Frontend**: http://localhost
- **API**: http://localhost:8080
- **API Health Check**: http://localhost:8080/api/game/health

For detailed setup instructions, see [DOCKER.md](DOCKER.md).

### Local Development

**Backend**:
```bash
cd api
./gradlew bootRun
```

**Frontend**:
```bash
cd ReactFrontend/ChooseYourOwnAdventure
npm install
npm run dev
```

See [DOCUMENTATION.md](DOCUMENTATION.md) for complete setup instructions.

## Team

**Controlled Chaos**

### Project Abstract

This software provides an interactive **text-based** choose-your-own-adventure style game. Players make decisions that determine their path through the story, with consequences affecting their health, inventory, and the game's ending. The game features multiple paths, various in-game locations, and many different possible endings.

Players navigate through a branching narrative where their choices matter. The game includes:
- **Health System**: Players have 100 HP that can be lost or restored
- **Inventory System**: Collect items (torch, key, amulet, sword, etc.) that unlock new paths
- **Multiple Endings**: Different endings based on player choices and collected items
- **Story Map**: Visual representation of the game's story structure

For detailed game mechanics, see [sql/GAME_DOCUMENTATION.md](sql/GAME_DOCUMENTATION.md).

<!-- This is an example paragraph written in markdown. You can use *italics*, **bold**, and other formatting options. You can also <u>use inline html</u> to format your text. The example sections included in this document are not necessarily all the sections you will want, and it is possible that you won't use all the one's provided. It is your responsibility to create a document that adequately conveys all the information about your project specifications and requirements. -->

<!-- Please view this file's source to see `comments` with guidance on how you might use the different sections of this document.  -->

### Customer

The target audience for this software includes:
- Fans of text-based games and interactive fiction
- Readers who enjoy making choices that affect the story
- Players who appreciate exploration and multiple playthroughs
- Users looking for a narrative-driven gaming experience

The software provides many paths to take, meaningful decisions, and flexibility in gameplay, allowing each player to create their own unique experience.

<!--A brief description of the customer for this software, both in general (the population who might eventually use such a system) and specifically for this document (the customer(s) who informed this document). Every project will have a customer from the CS506 instructional staff. Requirements should not be derived simply from discussion among team members. Ideally your customer should not only talk to you about requirements but also be excited later in the semester to use the system.-->

### Specification

<!--A detailed specification of the system. UML, or other diagrams, such as finite automata, or other appropriate specification formalisms, are encouraged over natural language.-->

<!--Include sections, for example, illustrating the database architecture (with, for example, an ERD).-->

<!--Included below are some sample diagrams, including some example tech stack diagrams.-->

#### Technology Stack

<!-- Here are some sample technology stacks that you can use for inspiration: -->

<!-- >```mermaid
flowchart RL
subgraph Front End
	A(Javascript: React)
end
	
subgraph Back End
	B(Python: Django with \nDjango Rest Framework)
end
	
subgraph Database
	C[(MySQL)]
end

A <->|"REST API"| B
B <->|Django ORM| C
```
--->

<!-- ```mermaid
flowchart RL
subgraph Front End
	A(Javascript: Vue)
end
	
subgraph Back End
	B(Python: Flask)
end
	
subgraph Database
	C[(MySQL)]
end

A <- ->|"REST API"| B
B <- ->|SQLAlchemy| C
``` -->

<!-- ```mermaid
flowchart RL
subgraph Front End
	A(Javascript: Vue)
end
	
subgraph Back End
	B(Javascript: Express)
end
	
subgraph Database
	C[(MySQL)]
end

A <- ->|"REST API"| B
B <- -> C
``` -->

```mermaid
flowchart RL
subgraph Front End
	A(React, JS, CSS, HTML)
end
	
subgraph Back End
	B(Java: SpringBoot)
end
	
subgraph Database
	C[(MySQL), Docker]
end

A <-->|HTTP| B
B <--> C
```

<!-- ```mermaid
flowchart RL
subgraph Front End
	A(Mobile App)
end
	
subgraph Back End
	B(Python: Django)
end
	
subgraph Database
	C[(MySQL)]
end

A <- ->|REST API| B
B <- ->|Django ORM| C
``` -->



<!-- #### Database

```mermaid
---
title: Sample Database ERD for an Order System
---
erDiagram
    Customer ||--o{ Order : "placed by"
    Order ||--o{ OrderItem : "contains"
    Product ||--o{ OrderItem : "included in"

    Customer {
        int customer_id PK
        string name
        string email
        string phone
    }

    Order {
        int order_id PK
        int customer_id FK
        string order_date
        string status
    }

    Product {
        int product_id PK
        string name
        string description
        decimal price
    }

    OrderItem {
        int order_item_id PK
        int order_id FK
        int product_id FK
        int quantity
    }
``` -->

<!-- #### Class Diagram

```mermaid
---
title: Sample Class Diagram for Animal Program
---
classDiagram
    class Animal {
        - String name
        + Animal(String name)
        + void setName(String name)
        + String getName()
        + void makeSound()
    }
    class Dog {
        + Dog(String name)
        + void makeSound()
    }
    class Cat {
        + Cat(String name)
        + void makeSound()
    }
    class Bird {
        + Bird(String name)
        + void makeSound()
    }
    Animal <|-- Dog
    Animal <|-- Cat
    Animal <|-- Bird
``` -->

#### Flowchart

```mermaid
---
title: Sample Program Flowchart
---
graph TD;
    Start([Start]) --> Input_Data[/Input Data/];
    Input_Data --> Process_Data[Process Data];
    Process_Data --> Validate_Data{Validate Data};
    Validate_Data -->|Valid| Process_Valid_Data[Process Valid Data];
    Validate_Data -->|Invalid| Error_Message[/Error Message/];
    Process_Valid_Data --> Analyze_Data[Analyze Data];
    Analyze_Data --> Generate_Output[Generate Output];
    Generate_Output --> Display_Output[/Display Output/];
    Display_Output --> End([End]);
    Error_Message --> End;
```

<!--  #### Behavior

```mermaid
---
title: Sample State Diagram For Coffee Application
---
stateDiagram
    [*] - -> Ready
    Ready - -> Brewing : Start Brewing
    Brewing - -> Ready : Brew Complete
    Brewing - -> WaterLowError : Water Low
    WaterLowError - -> Ready : Refill Water
    Brewing - -> BeansLowError : Beans Low
    BeansLowError - -> Ready : Refill Beans
``` -->

<!-- #### Sequence Diagram

```mermaid
sequenceDiagram

participant ReactFrontend
participant DjangoBackend
participant MySQLDatabase

ReactFrontend ->> DjangoBackend: HTTP Request (e.g., GET /api/data)
activate DjangoBackend

DjangoBackend ->> MySQLDatabase: Query (e.g., SELECT * FROM data_table)
activate MySQLDatabase

MySQLDatabase - ->> DjangoBackend: Result Set
deactivate MySQLDatabase

DjangoBackend - ->> ReactFrontend: JSON Response
deactivate DjangoBackend
``` -->

### Standards & Conventions

[Style Guide & Conventions](STYLE.md)

## Technology Stack

- **Frontend**: React 19, Vite, Bootstrap
- **Backend**: Java 17, Spring Boot 3.5.6
- **Database**: MySQL 8.0
- **Infrastructure**: Docker, Docker Compose, Nginx

For detailed technology information, see [DOCUMENTATION.md](DOCUMENTATION.md#technology-stack).

## Project Structure

```
Project_10/
├── api/                    # Spring Boot backend
├── ReactFrontend/          # React frontend
├── sql/                    # Database migrations and game data
├── docker-compose-full.yml # Docker Compose configuration
└── Documentation files     # Various documentation files
```

## Documentation

- **[DOCUMENTATION.md](DOCUMENTATION.md)** - Complete project documentation including architecture, setup, API docs, and more
- **[API_REFERENCE.md](API_REFERENCE.md)** - Quick reference for all API endpoints
- **[DOCKER.md](DOCKER.md)** - Docker setup and deployment guide
- **[sql/GAME_DOCUMENTATION.md](sql/GAME_DOCUMENTATION.md)** - Detailed game design, scenes, and mechanics
- **[STYLE.md](STYLE.md)** - Coding standards and conventions

## Docker

This project uses Docker for easy setup and deployment. See [DOCKER.md](DOCKER.md) for detailed instructions on how to run the application using Docker.

## Contributing

This is a CS506 course project. For development guidelines, see [DOCUMENTATION.md](DOCUMENTATION.md#development-guide).
