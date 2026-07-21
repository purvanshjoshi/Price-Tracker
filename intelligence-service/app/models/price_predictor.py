import math


class PricePredictor:
    def __init__(self):
        self.trained = False

    def predict(self, product_name: str, current_price: float, platform: str) -> dict:
        """
        Predict the price trend for a product.
        Uses a simple heuristic model based on platform-specific depreciation rates.
        """
        platform_rates = {
            "amazon": 0.97,
            "flipkart": 0.96,
            "croma": 0.98,
            "reliance": 0.975,
        }
        rate = platform_rates.get(platform.lower(), 0.97)

        predicted_price = round(current_price * rate, 2)
        confidence = round(0.5 + abs(current_price - predicted_price) / current_price * 0.3, 2)
        confidence = min(max(confidence, 0.1), 0.95)

        diff = predicted_price - current_price
        if diff < -1:
            trend = "down"
        elif diff > 1:
            trend = "up"
        else:
            trend = "stable"

        return {
            "product_name": product_name,
            "current_price": current_price,
            "predicted_price": predicted_price,
            "confidence": confidence,
            "days_ahead": 7,
            "trend": trend,
        }
