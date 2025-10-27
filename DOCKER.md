# Docker Setup Guide

This project uses Docker to run all services together. Follow these steps to get started.

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) installed
- [Docker Compose](https://docs.docker.com/compose/install/) installed

## Quick Start

1. **Navigate to the project directory:**
   ```bash
   cd /u/z/h/zhiweis/private/cs506/Project_10
   ```

2. **Start all services:**
   ```bash
   docker-compose -f docker-compose-full.yml up --build
   ```

3. **Access the application:**
   - If running **locally**: 
     - Frontend: http://localhost
     - API: http://localhost:8080
   - If running on a **remote server** (via SSH):
     - You'll need to set up SSH port forwarding (see below)

## Accessing from a Remote Server

If you're accessing the application from a remote server via SSH:

### On your local machine:

1. **Set up SSH port forwarding:**
   ```bash
   ssh -N -L 8080:localhost:80 -L 8081:localhost:8080 CS_USERNAME@cs506x10
   ```

2. **Access the application:**
   - Frontend: http://localhost:3000
   - API: http://localhost:8081

**Note:** Keep the SSH terminal open while using the application. The connection will close if you close that terminal.

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

**"Could not connect to the realm" error?** This means the frontend can't reach the backend:

1. **Check if the API is accessible:**
   ```bash
   curl http://localhost:8080/api/game/scene/intro
   ```

2. **Check nginx configuration:**
   ```bash
   docker exec textquest-frontend cat /etc/nginx/conf.d/default.conf
   ```

3. **Restart services:**
   ```bash
   docker-compose -f docker-compose-full.yml restart
   ```

**SSH tunnel "Connection refused"?** The tunnel may have disconnected:
1. Close the tunnel terminal
2. Restart with: `ssh -N -L 3000:localhost:80 -L 8081:localhost:8080 zhiweis@<server-ip>`
3. Refresh your browser at http://localhost:3000

**Port already in use?** Use different ports:
```bash
ssh -N -L 3001:localhost:80 -L 8082:localhost:8080 zhiweis@<server-ip>
```
Then access at http://localhost:3001

## Development

To make changes:
1. Edit your code
2. Rebuild the affected service:
   ```bash
   # Rebuild frontend
   docker-compose -f docker-compose-full.yml build --no-cache frontend
   docker-compose -f docker-compose-full.yml up -d frontend
   
   # Rebuild backend
   docker-compose -f docker-compose-full.yml build --no-cache api
   docker-compose -f docker-compose-full.yml up -d api
   ```

3. Or restart a service:
   ```bash
   docker-compose -f docker-compose-full.yml restart api
   ```

## Complete Restart (Clean Slate)

If you need to completely rebuild everything:
```bash
docker-compose -f docker-compose-full.yml down
docker-compose -f docker-compose-full.yml build --no-cache
docker-compose -f docker-compose-full.yml up -d
```
