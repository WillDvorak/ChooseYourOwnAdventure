# Docker Setup Guide

This project uses Docker to run all services together. Follow these steps to get started.

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) installed
- [Docker Compose](https://docs.docker.com/compose/install/) installed

## Quick Start

1. **Start all services:**
   ```bash
   docker-compose -f docker-compose-full.yml up --build
   ```

2. **Access the application:**
   - Frontend: http://localhost
   - API: http://localhost:8080
   - Database: localhost:3306

## Services

- **Frontend**: React app served by Nginx (port 80)
- **Backend**: Spring Boot API (port 8080) 
- **Database**: MySQL 8.0 (port 3306)

## Common Commands

```bash
# Start services in background
docker-compose -f docker-compose-full.yml up -d

# Stop all services
docker-compose -f docker-compose-full.yml down

# View logs
docker-compose -f docker-compose-full.yml logs

# Restart a service
docker-compose -f docker-compose-full.yml restart api
```

## Troubleshooting

### Common Issues

**"Empty table and exit 1" error?** This usually means database schema issues:

1. **Clean start:**
   ```bash
   docker-compose -f docker-compose-full.yml down -v
   docker system prune -f
   docker-compose -f docker-compose-full.yml up --build
   ```

2. **Check database tables:**
   ```bash
   docker exec -it textquest-mysql mysql -u root -prootpass textquest -e "SHOW TABLES;"
   ```

**Port conflicts?** Check what's using the ports:
```bash
lsof -i :8080
lsof -i :3306
lsof -i :80
```

**Database issues?** Restart the database:
```bash
docker-compose -f docker-compose-full.yml restart db
```

**API not starting?** Check logs:
```bash
docker-compose -f docker-compose-full.yml logs api
```

**Services not starting?** Check status:
```bash
docker-compose -f docker-compose-full.yml ps
```

## Development

To make changes:
1. Edit your code
2. Rebuild the affected service: `docker-compose -f docker-compose-full.yml up --build api`
3. Or restart: `docker-compose -f docker-compose-full.yml restart api`

The application should be running at http://localhost
