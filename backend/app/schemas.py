from datetime import datetime
from pydantic import BaseModel, EmailStr, Field


class UserRegister(BaseModel):
    email: EmailStr
    password: str = Field(min_length=8)


class UserLogin(BaseModel):
    email: EmailStr
    password: str


class Token(BaseModel):
    access_token: str
    token_type: str = "bearer"


class DeviceCreate(BaseModel):
    name: str
    platform: str = "android"
    device_token: str


class DeviceOut(BaseModel):
    id: int
    name: str
    platform: str
    device_token: str
    is_active: bool
    is_lost: bool
    created_at: datetime

    class Config:
        from_attributes = True


class LocationEventCreate(BaseModel):
    latitude: float
    longitude: float
    accuracy_meters: float = 0.0
    source: str = "app"


class AuditLogOut(BaseModel):
    id: int
    action: str
    metadata: str | None = None
    created_at: datetime

    class Config:
        from_attributes = True
