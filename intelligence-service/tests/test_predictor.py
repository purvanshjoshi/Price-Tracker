from app.models.price_predictor import PricePredictor


def test_predict_returns_expected_keys():
    predictor = PricePredictor()
    result = predictor.predict("iPhone 15", 79999.0, "amazon")
    assert "product_name" in result
    assert "current_price" in result
    assert "predicted_price" in result
    assert "confidence" in result
    assert "days_ahead" in result
    assert "trend" in result


def test_predict_lower_price():
    predictor = PricePredictor()
    result = predictor.predict("Test", 100.0, "flipkart")
    assert result["predicted_price"] < result["current_price"]


def test_predict_negative_price():
    predictor = PricePredictor()
    result = predictor.predict("Test", 0.0, "amazon")
    assert result["predicted_price"] >= 0
