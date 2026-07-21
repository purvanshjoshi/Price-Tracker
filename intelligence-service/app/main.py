from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from app.models.price_predictor import PricePredictor

app = FastAPI(
    title="Price Scout Intelligence Service",
    description="ML-powered price prediction and analytics for e-commerce products",
    version="0.1.0",
)

predictor = PricePredictor()


class PredictionRequest(BaseModel):
    product_name: str
    current_price: float
    platform: str


class PredictionResponse(BaseModel):
    product_name: str
    current_price: float
    predicted_price: float
    confidence: float
    days_ahead: int
    trend: str


class HealthResponse(BaseModel):
    status: str


@app.get("/health", response_model=HealthResponse)
async def health():
    return HealthResponse(status="ok")


@app.post("/api/predict", response_model=PredictionResponse)
async def predict_price(req: PredictionRequest):
    if req.current_price <= 0:
        raise HTTPException(status_code=400, detail="current_price must be positive")

    result = predictor.predict(
        product_name=req.product_name,
        current_price=req.current_price,
        platform=req.platform,
    )
    return result
