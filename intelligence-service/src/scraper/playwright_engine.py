from playwright.async_api import async_playwright
from src.stealth.solver import TwoCaptchaSolver

class PlaywrightEngine:
    def __init__(self):
        self.solver = TwoCaptchaSolver()

    async def extract_price(self, url: str) -> dict:
        async with async_playwright() as p:
            browser = await p.chromium.launch(headless=True)
            page = await browser.new_page()
            
            try:
                await page.goto(url)
                
                # Check for CAPTCHA (soft block)
                captcha_element = await page.query_selector('div.g-recaptcha')
                if captcha_element:
                    site_key = await captcha_element.get_attribute("data-sitekey")
                    if site_key:
                        print(f"Detected CAPTCHA with site_key: {site_key}")
                        # Trigger the Challenge Layer
                        token = await self.solver.solve_challenge(site_key, url)
                        
                        # Inject the token
                        await page.evaluate(f"document.getElementById('g-recaptcha-response').innerHTML='{token}';")
                        
                        # Find and click the submit button (heuristic for demo)
                        submit_button = await page.query_selector('button[type="submit"], input[type="submit"]')
                        if submit_button:
                            await submit_button.click()
                            await page.wait_for_navigation(wait_until="domcontentloaded")
                
                # Scrape the price (heuristic dummy logic)
                price_element = await page.query_selector('.price, .a-price-whole')
                if price_element:
                    price_text = await price_element.inner_text()
                    try:
                        price_float = float(price_text.replace(',', '').replace('$', '').replace('₹', ''))
                        return {"status": "ok", "price": price_float, "currency": "INR", "evidence": [price_text], "source_url": url}
                    except ValueError:
                        return {"status": "parse_error", "source_url": url}
                else:
                    # After bypass, if still no price, layout might be changed
                    return {"status": "changed_layout", "source_url": url}
            
            except Exception as e:
                print(f"Error extracting: {e}")
                return {"status": "parse_error", "source_url": url}
            finally:
                await browser.close()
