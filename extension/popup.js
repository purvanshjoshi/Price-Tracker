document.addEventListener('DOMContentLoaded', function() {
    const searchBtn = document.getElementById('searchBtn');
    const searchInput = document.getElementById('searchInput');
    const loadingDiv = document.getElementById('loading');
    const resultsContainer = document.getElementById('resultsContainer');
    const resultsBody = document.getElementById('resultsBody');
    const errorDiv = document.getElementById('error');
    const sortSelect = document.getElementById('sortSelect');
    const filterInput = document.getElementById('filterInput');
    const resultCount = document.getElementById('resultCount');

    let cachedResults = []; // Store results for sort/filter without re-fetching

    // ── Sort & Filter Logic ──────────────────────────────────────
    function applySortAndFilter() {
        const sortBy = sortSelect.value;
        const filterText = filterInput.value.toLowerCase().trim();

        // Filter first
        let visible = cachedResults;
        if (filterText) {
            visible = visible.filter(p =>
                p.name.toLowerCase().includes(filterText) ||
                p.platform.toLowerCase().includes(filterText) ||
                String(p.price).includes(filterText)
            );
        }

        // Find cheapest for "Best Deal" badge
        const cheapestPrice = Math.min(...visible.map(p => p.price));

        // Sort
        visible.sort((a, b) => {
            switch (sortBy) {
                case 'cheapest':  return a.price - b.price;
                case 'priciest':  return b.price - a.price;
                case 'platform-az': return a.platform.localeCompare(b.platform);
                case 'platform-za': return b.platform.localeCompare(a.platform);
                default:           return 0;
            }
        });

        renderResults(visible, cheapestPrice);
    }

    // ── Render Results ───────────────────────────────────────────
    function renderResults(results, cheapestPrice) {
        resultsBody.innerHTML = '';

        if (results.length === 0) {
            resultCount.textContent = 'No results match your filter.';
            return;
        }

        resultCount.textContent = `Showing ${results.length} result${results.length !== 1 ? 's' : ''}`;

        results.forEach(product => {
            const tr = document.createElement('tr');
            const isCheapest = product.price === cheapestPrice;

            const platformClass = product.platform.toLowerCase() === 'amazon'
                ? 'platform-amazon'
                : 'platform-flipkart';

            const formattedPrice = '\u20b9' + product.price.toLocaleString('en-IN');
            const displayTitle = product.name.length > 35
                ? product.name.substring(0, 35) + '...'
                : product.name;

            tr.innerHTML = `
                <td>
                    <span class="platform-badge ${platformClass}">${product.platform}</span>
                    ${isCheapest ? '<span class="best-deal-badge" title="Lowest price!">Best Deal</span>' : ''}
                </td>
                <td title="${product.name}">${displayTitle}</td>
                <td class="${isCheapest ? 'price-cheapest' : 'price-text'}">${formattedPrice}</td>
                <td><a href="${product.url}" target="_blank" class="buy-btn">View</a></td>
            `;
            resultsBody.appendChild(tr);
        });
    }

    // ── Event Listeners ──────────────────────────────────────────
    searchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') searchBtn.click();
    });

    // Re-render on sort or filter change (no new search needed)
    sortSelect.addEventListener('change', () => {
        if (cachedResults.length) applySortAndFilter();
    });
    filterInput.addEventListener('input', () => {
        if (cachedResults.length) applySortAndFilter();
    });

    // ── Search ────────────────────────────────────────────────────
    searchBtn.addEventListener('click', function() {
        const query = searchInput.value.trim();
        if (!query) return;

        errorDiv.classList.add('hidden');
        resultsContainer.classList.add('hidden');
        loadingDiv.classList.remove('hidden');
        resultsBody.innerHTML = '';
        filterInput.value = '';
        sortSelect.value = 'cheapest';
        cachedResults = [];
        resultCount.textContent = '';
        searchBtn.disabled = true;

        chrome.runtime.sendMessage({ type: 'SEARCH', query: query }, function(response) {
            loadingDiv.classList.add('hidden');
            searchBtn.disabled = false;

            if (chrome.runtime.lastError || !response || response.error) {
                errorDiv.textContent = (response && response.error)
                    ? response.error
                    : 'Failed to connect to the Price Scout Java Engine.';
                errorDiv.classList.remove('hidden');
                return;
            }

            if (response.results && response.results.length > 0) {
                cachedResults = response.results;
                applySortAndFilter();
                resultsContainer.classList.remove('hidden');
            } else {
                errorDiv.textContent = 'No valid deals found for this product.';
                errorDiv.classList.remove('hidden');
            }
        });
    });
});
