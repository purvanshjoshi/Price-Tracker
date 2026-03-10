// This is a Chrome Service Worker that stays alive in the background

chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
    if (message.type === 'SEARCH' && message.query) {

        // This is the magical Native Messaging call that wakes up our Java Engine!
        // We pass the name of the host configuration file we will create in Phase 7.
        chrome.runtime.sendNativeMessage(
            'com.pricetracker.engine',
            { query: message.query },
            function (response) {
                if (chrome.runtime.lastError) {
                    // This happens if the Java Engine isn't running or the registry key is missing
                    console.error("Native Messaging Error: ", chrome.runtime.lastError.message);
                    sendResponse({ error: "Cannot connect to Java Engine. Is the Host registered?" });
                } else {
                    console.log("Received native response:", response);
                    sendResponse(response);
                }
            }
        );

        // Return true to indicate we wish to send a response asynchronously 
        // (because Native Messaging takes a second to reply)
        return true;
    }
});
