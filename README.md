# Device Recovery Starter

A legal, consent-based starter project for a lost-device recovery app.

This repository contains:
- A FastAPI backend for registration, auth, device management, and location reporting
- An Android (Kotlin + Jetpack Compose) starter client with location permission flow and device onboarding
- A Docker Compose setup for local development

Important:
- This project is for the device owner to consent to location tracking for their own device.
- It does not provide covert or hidden tracking.
- Do not use it for stalking, unauthorized surveillance, or phishing-related tracking.
- Always follow app store policies, OS permission rules, and local privacy laws.

## Tech stack
- Android: Kotlin, Jetpack Compose
- Backend: FastAPI, SQLAlchemy, PostgreSQL
- Auth: JWT with password hashing
- Container: Docker Compose

## Quick start

### Backend

```bash
cd backend
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

### Docker Compose

```bash
docker compose up --build
```

### Android

Open the `android` directory in Android Studio and sync Gradle.

## Features included
- User registration and login
- JWT-secured API
- Device registration per user
- Consent-based location permission flow
- Last-known location upload endpoint
- Mark device as lost
- Basic audit log
- Docker-ready local environment

## Privacy and safety notes
- Require explicit user consent before enabling location access
- Store only the minimal data required for device recovery
- Give users the ability to disable tracking and delete account data
- Keep location data encrypted in transit and at rest when deployed in production

## Default local backend URL
- http://localhost:8000

## API docs
- Swagger UI: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc
