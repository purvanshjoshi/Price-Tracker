// This is a Chrome Service Worker that stays alive in the background

chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
    if (message.type === 'SEARCH' && message.query) {

        // Connecting to your live Hugging Face Space!
        const API_BASE_URL = "https://purvansh01-price-scout.hf.space/api/search?q=";
        const CLOUD_API_URL = API_BASE_URL + encodeURIComponent(message.query);

        fetch(CLOUD_API_URL)
            .then(response => response.json())
            .then(data => {
                console.log("Received Cloud API response:", data);
                // The extension UI expects { results: [...] }
                sendResponse({ results: data });
            })
            .catch(error => {
                console.error("Cloud API Error: ", error);
                sendResponse({ error: "Cannot connect to Price Scout Cloud. Is the server running?" });
            });

        // Return true to indicate we wish to send a response asynchronously 
        // (because Native Messaging takes a second to reply)
        return true;
    }
});
