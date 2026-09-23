from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.auth import router as auth_router
from app.database import Base, engine
from app.device_routes import router as device_router

Base.metadata.create_all(bind=engine)

app = FastAPI(title="Device Recovery Starter API", version="0.1.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth_router)
app.include_router(device_router)


@app.get("/")
def root():
    return {"message": "Device Recovery Starter API is running"}
