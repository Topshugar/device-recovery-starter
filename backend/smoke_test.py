import uuid

from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


def run_smoke_test() -> None:
    email = f"user-{uuid.uuid4().hex[:8]}@example.com"
    password = "password123"
    headers = {}

    register_response = client.post(
        "/api/auth/register",
        json={"email": email, "password": password},
    )
    assert register_response.status_code == 200, register_response.text
    token = register_response.json()["access_token"]
    assert token

    login_response = client.post(
        "/api/auth/login",
        json={"email": email, "password": password},
    )
    assert login_response.status_code == 200, login_response.text
    login_token = login_response.json()["access_token"]
    assert login_token
    headers["Authorization"] = f"Bearer {login_token}"

    me_response = client.get("/api/auth/me", headers=headers)
    assert me_response.status_code == 200, me_response.text
    assert me_response.json()["email"] == email

    device_token = f"device-{uuid.uuid4().hex}"
    device_response = client.post(
        "/api/devices",
        headers=headers,
        json={"name": "My Phone", "platform": "android", "device_token": device_token},
    )
    assert device_response.status_code == 200, device_response.text
    device_id = device_response.json()["id"]
    assert device_id > 0

    device_list_response = client.get("/api/devices", headers=headers)
    assert device_list_response.status_code == 200, device_list_response.text
    assert any(item["id"] == device_id for item in device_list_response.json())

    location_response = client.post(
        f"/api/devices/{device_id}/location",
        headers=headers,
        json={"latitude": 40.7128, "longitude": -74.0060, "accuracy_meters": 10.0, "source": "app"},
    )
    assert location_response.status_code == 200, location_response.text

    mark_lost_response = client.post(f"/api/devices/{device_id}/mark-lost", headers=headers)
    assert mark_lost_response.status_code == 200, mark_lost_response.text

    print(f"Smoke test passed for {email}")


if __name__ == "__main__":
    run_smoke_test()
