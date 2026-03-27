document.addEventListener('DOMContentLoaded', function() {
    const searchBtn = document.getElementById('searchBtn');
    const searchInput = document.getElementById('searchInput');
    const filterInput = document.getElementById('filterInput');
    const loadingDiv = document.getElementById('loading');
    const resultsContainer = document.getElementById('resultsContainer');
    const resultsBody = document.getElementById('resultsBody');
    const errorDiv = document.getElementById('error');
    const countBar = document.getElementById('countBar');

    // State: raw results from backend, current filter/sort
    let allResults = [];
    let currentSort = 'cheapest';
    let currentFilter = '';

    // ── Sort buttons ─────────────────────────────────────────────────────────
    document.querySelectorAll('.sort-btn').forEach(function(btn) {
        btn.addEventListener('click', function() {
            // Update active state
            document.querySelectorAll('.sort-btn').forEach(function(b) { b.classList.remove('active'); });
            btn.classList.add('active');
            currentSort = btn.dataset.sort;
            renderSortedFiltered();
        });
    });

    // ── Real-time filter (live as you type) ─────────────────────────────────
    filterInput.addEventListener('input', function() {
        currentFilter = filterInput.value.trim().toLowerCase();
        renderSortedFiltered();
    });

    // ── Trigger search on Enter key ─────────────────────────────────────────
    searchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchBtn.click();
        }
    });

    // ── Main search ──────────────────────────────────────────────────────────
    searchBtn.addEventListener('click', function() {
        const query = searchInput.value.trim();
        if (!query) return;

        // Reset UI state
        errorDiv.classList.add('hidden');
        resultsContainer.classList.add('hidden');
        loadingDiv.classList.remove('hidden');
        resultsBody.innerHTML = '';
        searchBtn.disabled = true;
        filterInput.value = '';
        currentFilter = '';

        // Reset sort to cheapest (default)
        document.querySelectorAll('.sort-btn').forEach(function(b) { b.classList.remove('active'); });
        document.getElementById('sortCheapest').classList.add('active');
        currentSort = 'cheapest';

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
                allResults = response.results;
                renderSortedFiltered();
                resultsContainer.classList.remove('hidden');
            } else {
                errorDiv.textContent = "No valid deals found for this product.";
                errorDiv.classList.remove('hidden');
            }
        });
    });

    // ── Render with current sort + filter ──────────────────────────────────
    function renderSortedFiltered() {
        resultsBody.innerHTML = '';

        // 1. Filter
        let visible = allResults;
        if (currentFilter) {
            visible = visible.filter(function(product) {
                return product.name.toLowerCase().includes(currentFilter) ||
                    product.platform.toLowerCase().includes(currentFilter) ||
                    product.price.toString().includes(currentFilter);
            });
        }

        // 2. Sort
        if (currentSort === 'cheapest') {
            visible = visible.slice().sort(function(a, b) { return a.price - b.price; });
        } else if (currentSort === 'recent') {
            // Sort by most recent (assuming results are returned newest first; keep as-is or sort by URL as proxy)
            // If product has a 'date' field use it; otherwise assume original order is fine.
            visible = visible.slice().reverse();
        } else if (currentSort === 'platform') {
            visible = visible.slice().sort(function(a, b) {
                if (a.platform === b.platform) return a.price - b.price;
                return a.platform.localeCompare(b.platform);
            });
        }

        // 3. Find best deal (cheapest among filtered results)
        const bestPrice = visible.length > 0 ? Math.min.apply(null, visible.map(function(p) { return p.price; })) : null;

        // 4. Update count bar
        if (currentFilter) {
            countBar.textContent = 'Showing ' + visible.length + ' of ' + allResults.length + ' results';
        } else {
            countBar.textContent = visible.length + ' results';
        }

        // 5. Render rows
        visible.forEach(function(product) {
            const tr = document.createElement('tr');
            if (bestPrice !== null && product.price === bestPrice) {
                tr.classList.add('best-deal-row');
            }

            const platformClass = product.platform.toLowerCase() === 'amazon' ? 'platform-amazon' : 'platform-flipkart';

            // Format price with Indian Rupee formatting
            const formattedPrice = '\u20B9' + product.price.toLocaleString('en-IN');

            // Truncate long titles
            const displayTitle = product.name.length > 30 ? product.name.substring(0, 30) + '...' : product.name;

            // Best deal badge (only shown on cheapest item)
            const bestDealBadge = (bestPrice !== null && product.price === bestPrice) ?
                ' <span class="best-deal-badge">Best Deal</span>' : '';

            tr.innerHTML = `
                <td><span class="platform-badge ${platformClass}">${product.platform}</span></td>
                <td title="${product.name}">${displayTitle}${bestDealBadge}</td>
                <td class="price-text">${formattedPrice}</td>
                <td><a href="${product.url}" target="_blank" class="buy-btn">View</a></td>
            `;
            resultsBody.appendChild(tr);
        });

        if (visible.length === 0 && allResults.length > 0) {
            const tr = document.createElement('tr');
            tr.innerHTML = '<td colspan="4" style="text-align:center;color:#64748b;padding:20px;">No results match your filter. Try a different search term.</td>';
            resultsBody.appendChild(tr);
        }
    }
});
