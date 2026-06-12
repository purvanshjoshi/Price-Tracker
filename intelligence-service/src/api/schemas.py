from typing import Optional, List, Literal
from pydantic import BaseModel

class ExtractionRequest(BaseModel):
    url: str

class ExtractionResponse(BaseModel):
    status: Literal["ok", "blocked", "unavailable", "parse_error", "changed_layout"]
    price: Optional[float] = None
    currency: Optional[str] = None
    evidence: List[str] = []
    source_url: str
