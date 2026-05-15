// Price Scout Content Script - High Reliability Version
console.log("%c Price Scout: Initializing...", "color: #007bff; font-weight: bold;");

function getProductTitle() {
    let title = "";
    if (window.location.hostname.includes("amazon")) {
        // Amazon selectors
        const selectors = ["#productTitle", ".a-size-extra-large", "h1#title"];
        for (let s of selectors) {
            let el = document.querySelector(s);
            if (el && el.innerText.trim()) return el.innerText.trim();
        }
    } else if (window.location.hostname.includes("flipkart")) {
        // Flipkart selectors
        const selectors = [".VU-Z7G", "h1", "span.B_NuE_"];
        for (let s of selectors) {
            let el = document.querySelector(s);
            if (el && el.innerText.trim()) return el.innerText.trim();
        }
    }
    return null;
}

function injectFloatingButton() {
    const title = getProductTitle();
    if (!title) {
        console.log("Price Scout: Product title not found yet...");
        return;
    }

    if (document.getElementById("price-scout-fab")) return;

    console.log("Price Scout: Product detected -> " + title);

    const fab = document.createElement("div");
    fab.id = "price-scout-fab";
    fab.innerHTML = `
        <div class="fab-icon">S</div>
        <div class="fab-content">
            <div class="fab-label">Compare Prices</div>
            <div class="fab-product">${title.substring(0, 30)}...</div>
        </div>
    `;

    document.body.appendChild(fab);

    fab.addEventListener("click", () => {
        showOverlay(title);
    });
}

function showOverlay(query) {
    let overlay = document.getElementById("price-scout-overlay");
    if (!overlay) {
        overlay = document.createElement("div");
        overlay.id = "price-scout-overlay";
        overlay.innerHTML = `
            <div class="scout-side-panel">
                <header>
                    <div class="scout-logo">Price Scout <span>Intel</span></div>
                    <button class="scout-close">&times;</button>
                </header>
                <div id="scout-results-list">
                    <div class="scout-searching">Searching cloud stores...</div>
                </div>
            </div>
        `;
        document.body.appendChild(overlay);
        overlay.querySelector(".scout-close").onclick = () => overlay.classList.remove("open");
    }

    overlay.classList.add("open");
    const list = document.getElementById("scout-results-list");
    list.innerHTML = `<div class="scout-searching">Fetching deals for "${query}"...</div>`;

    chrome.runtime.sendMessage({ type: "SEARCH", query: query }, (response) => {
        if (!response || response.error || !response.results || response.results.length === 0) {
            list.innerHTML = `<div class="scout-not-found">No other deals found. Try a more specific search.</div>`;
            return;
        }

        renderResults(response.results, list);
    });
}

function renderResults(results, container) {
    container.innerHTML = "";
    results.sort((a, b) => a.price - b.price);

    results.forEach(item => {
        const div = document.createElement("div");
        div.className = "scout-item";
        const price = "\u20b9" + item.price.toLocaleString("en-IN");
        div.innerHTML = `
            <div class="item-main">
                <span class="item-store badge-${item.platform.toLowerCase()}">${item.platform}</span>
                <span class="item-price">${price}</span>
            </div>
            <div class="item-name">${item.name}</div>
            <a href="${item.url}" target="_blank" class="item-link">View Deal</a>
        `;
        container.appendChild(div);
    });
}

// Check every 2 seconds to handle dynamic page loads
setInterval(injectFloatingButton, 2000);
