$(document).ready(function() {
    const token = localStorage.getItem('token');
    const headers = { 'Authorization': 'Bearer ' + token };

    function loadProducts() {
        const sort = $('#sortSelect').val();
        const onSale = $('#onSaleFilter').is(':checked');

        let url = '/api/customer/products?';
        if (sort) url += 'sort=' + sort + '&';
        url += 'onSale=' + onSale;

        $.ajax({
            url: url,
            headers: headers,
            success: function(products) {
                renderProductCards(products);
            },
            error: function() {
                alert('Failed to load products.');
            }
        });
    }

    function renderProductCards(products) {
        const $grid = $('#productGrid');
        $grid.empty();

        if (products.length === 0) {
            $grid.html('<div class="col-12 text-center mt-5"><h4>No products found.</h4></div>');
            return;
        }

        products.forEach(product => {
            const card = `
                <div class="col-md-4 col-lg-3 mb-4">
                    <div class="card product-card h-100 position-relative">
                        ${product.onSale ? `<span class="badge bg-danger discount-badge">-${product.discountPercent}%</span>` : ''}
                        <img src="${product.imageUrl || 'https://via.placeholder.com/300x200?text=No+Image'}" class="card-img-top" alt="${product.name}" style="height:200px; object-fit:cover;">
                        <div class="card-body d-flex flex-column">
                            <h5 class="card-title">${product.name}</h5>
                            <p class="card-text small text-muted">${product.category}</p>
                            <div class="mt-auto">
                                ${product.onSale ? `
                                    <span class="original-price">Rs. ${product.price.toFixed(2)}</span>
                                    <span class="text-success fw-bold ms-2">Rs. ${product.discountedPrice.toFixed(2)}</span>
                                ` : `
                                    <span class="fw-bold">Rs. ${product.price.toFixed(2)}</span>
                                `}
                                <p class="small mt-1">${product.quantity > 0 ? 'In Stock' : '<span class="text-danger">Out of Stock</span>'}</p>
                                <button class="btn btn-sm btn-outline-primary view-detail-btn" data-id="${product.productId}">View Details</button>
                                <button class="btn btn-sm btn-success add-to-cart-btn" data-id="${product.productId}" ${product.quantity < 1 ? 'disabled' : ''}>Add to Cart</button>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            $grid.append(card);
        });
    }

    // Event listeners
    $('#sortSelect, #onSaleFilter').change(function() {
        loadProducts();
    });

    $(document).on('click', '.view-detail-btn', function() {
        const id = $(this).data('id');
        // For now, just log; we will add modal in Phase 4 or later
        alert('Product details will be expanded in Phase 4.');
    });

    $(document).on('click', '.add-to-cart-btn', function() {
        const id = $(this).data('id');
        alert('Add to cart will be functional in Phase 4.');
    });

    // Initial load
    loadProducts();
});