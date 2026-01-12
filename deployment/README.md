# BookPass Deployment Guide

This guide walks you through deploying BookPass to your Hetzner server with automatic CI/CD.

## Prerequisites

- ✅ Hetzner VPS with Docker & Docker Compose installed
- ✅ nginx installed and running (with n8n already configured)
- ✅ Domain pointing to your server (e.g., `api.bookpass.com`)
- ✅ GitHub repository with this code

---

## Step 1: Configure GitHub Secrets

Go to your GitHub repository → **Settings** → **Secrets and variables** → **Actions** → **New repository secret**

Add these secrets:

| Secret Name | Value |
|-------------|-------|
| `DB_PASSWORD` | Your Azure PostgreSQL password (`holbertonTuwaiq@`) |
| `JWT_SECRET` | Your JWT secret key |
| `MOYASAR_SECRET_KEY` | Your Moyasar API key |

> **Note:** `GITHUB_TOKEN` is automatically provided by GitHub Actions for pushing to GHCR.

---

## Step 2: First-Time Server Setup

SSH into your Hetzner server and run:

```bash
# 1. Create deployment directory
mkdir -p /opt/bookpass
cd /opt/bookpass

# 2. Create environment file with your secrets
nano .env
```

**Paste this into the .env file** (update values as needed):
```
GITHUB_USERNAME=AbdulrahmanAlTayar
DB_PASSWORD=holbertonTuwaiq@
JWT_SECRET=HolbertonTuwaiqSecretTokenXDtesttestewaidkmawidajwidadiapsddokawpdkmap
MOYASAR_SECRET_KEY=sk_test_MittjgFyxXYRqsBzH6iFTFWwMX64NWv2tscsWAEm
```

```bash
# 3. Secure the file (only root can read)
chmod 600 .env

# 4. Login to GitHub Container Registry
# First, create a PAT at: https://github.com/settings/tokens
# Select scope: read:packages
echo "YOUR_GITHUB_PAT" | docker login ghcr.io -u YOUR_GITHUB_USERNAME --password-stdin

# 5. Copy docker-compose.prod.yml to server
git clone https://github.com/AbdulrahmanAlTayar/BookPass-spring.git
cp BookPass-spring/docker-compose.prod.yml .

# 6. Start the services
docker compose -f docker-compose.prod.yml up -d

# 7. Check logs
docker logs -f bookpass-backend
```

---

## Step 3: Configure Nginx

```bash
# 1. Copy nginx config
sudo cp deployment/nginx.conf /etc/nginx/sites-available/bookpass-api

# 2. Edit the domain name
sudo nano /etc/nginx/sites-available/bookpass-api
# Change api.bookpass.example.com to your actual domain

# 3. Enable the site
sudo ln -s /etc/nginx/sites-available/bookpass-api /etc/nginx/sites-enabled/

# 4. Get SSL certificate (if not already done)
sudo certbot --nginx -d api.bookpass.example.com

# 5. Test and reload nginx
sudo nginx -t
sudo systemctl reload nginx
```

---

## Step 4: Verify Deployment

```bash
# Check container status
docker ps

# Check application logs
docker logs bookpass-backend

# Test the API
curl https://api.bookpass.example.com/api/health
```

---

## How Auto-Deployment Works

```
Push to main → GitHub Actions → Build Image → Push to GHCR → Watchtower pulls → Container restarts
```

1. **You push code** to the `main` branch
2. **GitHub Actions** builds a Docker image and pushes to `ghcr.io`
3. **Watchtower** (running on your server) checks for new images every 5 minutes
4. **When found**, Watchtower automatically pulls and restarts the container

**No manual intervention required!** 🎉

---

## Troubleshooting

### Container won't start
```bash
docker logs bookpass-backend
```

### Watchtower not updating
```bash
# Check Watchtower logs
docker logs watchtower

# Force update manually
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
```

### GitHub Actions failing
- Check the **Actions** tab in your GitHub repo
- Ensure all secrets are configured
- Verify the Dockerfile builds locally: `docker build -t test .`

### 502 Bad Gateway
```bash
# Check if container is running
docker ps | grep bookpass

# Check if app started successfully
docker logs bookpass-backend | tail -50
```

---

## Useful Commands

```bash
# View running containers
docker ps

# Restart BookPass
docker compose -f docker-compose.prod.yml restart bookpass-backend

# Stop everything
docker compose -f docker-compose.prod.yml down

# View real-time logs
docker logs -f bookpass-backend

# Check disk usage
docker system df
```
