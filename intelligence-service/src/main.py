from fastapi import FastAPI, HTTPException
from src.api.schemas import ExtractionRequest, ExtractionResponse
from src.scraper.playwright_engine import PlaywrightEngine

app = FastAPI(title="Intelligence Service")
engine = PlaywrightEngine()

@app.post("/v1/extract", response_model=ExtractionResponse)
async def extract_product(request: ExtractionRequest):
    try:
        result = await engine.extract_price(request.url)
        return ExtractionResponse(**result)
    except Exception as e:
        return ExtractionResponse(status="parse_error", source_url=request.url)
