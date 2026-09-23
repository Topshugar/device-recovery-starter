from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session

from app.auth import get_current_user
from app.database import get_db
from app.models import AuditLog, Device, LocationEvent
from app.schemas import DeviceCreate, DeviceOut, LocationEventCreate

router = APIRouter(prefix="/api", tags=["devices"])


@router.get("/devices", response_model=list[DeviceOut])
def list_devices(current_user=Depends(get_current_user), db: Session = Depends(get_db)):
    return db.query(Device).filter(Device.user_id == current_user.id).all()


@router.post("/devices", response_model=DeviceOut)
def create_device(payload: DeviceCreate, current_user=Depends(get_current_user), db: Session = Depends(get_db)):
    existing = db.query(Device).filter(Device.device_token == payload.device_token).first()
    if existing:
        raise HTTPException(status_code=400, detail="This device token is already registered")

    device = Device(
        user_id=current_user.id,
        name=payload.name.strip(),
        platform=payload.platform.strip().lower(),
        device_token=payload.device_token.strip(),
    )
    db.add(device)
    try:
        db.flush()
        db.add(AuditLog(
            user_id=current_user.id,
            device_id=device.id,
            action="register_device",
            metadata_text=f"Device {device.name} registered",
        ))
        db.commit()
    except IntegrityError as exc:
        db.rollback()
        raise HTTPException(status_code=400, detail="This device token is already registered") from exc
    db.refresh(device)
    return device


@router.post("/devices/{device_id}/location")
def upload_location(device_id: int, payload: LocationEventCreate, current_user=Depends(get_current_user), db: Session = Depends(get_db)):
    device = db.query(Device).filter(Device.id == device_id, Device.user_id == current_user.id).first()
    if not device:
        raise HTTPException(status_code=404, detail="Device not found")

    db.add(LocationEvent(
        device_id=device.id,
        latitude=payload.latitude,
        longitude=payload.longitude,
        accuracy_meters=payload.accuracy_meters,
        source=payload.source,
    ))
    db.add(AuditLog(
        user_id=current_user.id,
        device_id=device.id,
        action="location_update",
        metadata_text=f"{payload.latitude},{payload.longitude}",
    ))
    db.commit()
    return {"message": "Location recorded"}


@router.post("/devices/{device_id}/mark-lost")
def mark_lost(device_id: int, current_user=Depends(get_current_user), db: Session = Depends(get_db)):
    device = db.query(Device).filter(Device.id == device_id, Device.user_id == current_user.id).first()
    if not device:
        raise HTTPException(status_code=404, detail="Device not found")

    device.is_lost = True
    db.add(AuditLog(
        user_id=current_user.id,
        device_id=device.id,
        action="mark_lost",
        metadata_text="Owner marked device as lost",
    ))
    db.commit()
    return {"message": "Device marked as lost"}


@router.delete("/devices/{device_id}")
def delete_device(device_id: int, current_user=Depends(get_current_user), db: Session = Depends(get_db)):
    device = db.query(Device).filter(Device.id == device_id, Device.user_id == current_user.id).first()
    if not device:
        raise HTTPException(status_code=404, detail="Device not found")

    db.delete(device)
    db.commit()
    return {"message": "Device deleted"}
