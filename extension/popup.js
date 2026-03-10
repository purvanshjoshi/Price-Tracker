document.addEventListener('DOMContentLoaded', function() {
    const searchBtn = document.getElementById('searchBtn');
    const searchInput = document.getElementById('searchInput');
    const loadingDiv = document.getElementById('loading');
    const resultsContainer = document.getElementById('resultsContainer');
    const resultsBody = document.getElementById('resultsBody');
    const errorDiv = document.getElementById('error');

    // Trigger search on Enter key
    searchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchBtn.click();
        }
    });

    searchBtn.addEventListener('click', function() {
        const query = searchInput.value.trim();
        if (!query) return;

        // Reset UI state
        errorDiv.classList.add('hidden');
        resultsContainer.classList.add('hidden');
        loadingDiv.classList.remove('hidden');
        resultsBody.innerHTML = '';
        searchBtn.disabled = true;

        // Send message to background script (which forwards to Java Engine)
        chrome.runtime.sendMessage({ type: 'SEARCH', query: query }, function(response) {
            
            loadingDiv.classList.add('hidden');
            searchBtn.disabled = false;

            if (chrome.runtime.lastError || !response || response.error) {
                errorDiv.textContent = (response && response.error) ? response.error : "Failed to connect to the Price Scout Java Engine.";
                errorDiv.classList.remove('hidden');
                return;
            }

            if (response.results && response.results.length > 0) {
                renderResults(response.results);
                resultsContainer.classList.remove('hidden');
            } else {
                errorDiv.textContent = "No valid deals found for this product.";
                errorDiv.classList.remove('hidden');
            }
        });
    });

    function renderResults(results) {
        results.forEach(product => {
            const tr = document.createElement('tr');
            
            const platformClass = product.platform.toLowerCase() === 'amazon' ? 'platform-amazon' : 'platform-flipkart';
            
            // Format price with Indian Rupee formatting
            const formattedPrice = "₹" + product.price.toLocaleString('en-IN');

            // Truncate long titles
            const displayTitle = product.name.length > 30 ? product.name.substring(0, 30) + '...' : product.name;

            tr.innerHTML = `
                <td><span class="platform-badge ${platformClass}">${product.platform}</span></td>
                <td title="${product.name}">${displayTitle}</td>
                <td class="price-text">${formattedPrice}</td>
                <td><a href="${product.url}" target="_blank" class="buy-btn">View</a></td>
            `;
            resultsBody.appendChild(tr);
        });
    }
});
