from fastapi.testclient import TestClient
from src.main import app

client = TestClient(app)

def test_extract_product():
    response = client.post("/v1/extract", json={"url": "https://example.com/product"})
    assert response.status_code == 200
    data = response.json()
    assert "status" in data
    print(f"Test Response: {data}")

if __name__ == "__main__":
    test_extract_product()
    print("Tests passed!")
