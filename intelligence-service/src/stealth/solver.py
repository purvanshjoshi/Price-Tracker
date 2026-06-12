from abc import ABC, abstractmethod
import asyncio
import httpx

class CaptchaSolver(ABC):
    @abstractmethod
    async def solve_challenge(self, site_key: str, url: str) -> str:
        """Returns the solved token string."""
        pass

class TwoCaptchaSolver(CaptchaSolver):
    def __init__(self, api_key: str = "DUMMY_KEY_FOR_TESTING"):
        self.api_key = api_key
        self.submit_url = "http://api.captchaai.com/in.php"
        self.retrieve_url = "http://api.captchaai.com/res.php"

    async def solve_challenge(self, site_key: str, url: str) -> str:
        async with httpx.AsyncClient() as client:
            # Submit the challenge
            submit_params = {
                "key": self.api_key,
                "method": "userrecaptcha",
                "googlekey": site_key,
                "pageurl": url,
                "json": 1
            }
            submit_resp = await client.get(self.submit_url, params=submit_params)
            submit_data = submit_resp.json()

            if submit_data.get("status") != 1:
                raise Exception(f"Failed to submit captcha: {submit_data.get('request')}")

            request_id = submit_data["request"]

            # Poll for the solution
            retrieve_params = {
                "key": self.api_key,
                "action": "get",
                "id": request_id,
                "json": 1
            }

            for _ in range(24): # Poll for up to 2 minutes
                await asyncio.sleep(5)
                res = await client.get(self.retrieve_url, params=retrieve_params)
                data = res.json()
                
                if data.get("status") == 1:
                    return data["request"] # The solved token
                
                if data.get("request") != "CAPCHA_NOT_READY":
                    raise Exception(f"Failed to retrieve captcha: {data.get('request')}")
            
            raise Exception("Captcha solving timed out.")
