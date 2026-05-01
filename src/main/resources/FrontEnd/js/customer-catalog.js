$(document).ready(function() {
    const token = localStorage.getItem('token');
    const headers = { 'Authorization': 'Bearer ' + token };

    loadProducts();

    // Functions
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
                                <button class="btn btn-sm btn-success add-to-cart-btn" data-id="${product.productId}" data-name="${product.name}" ${product.quantity < 1 ? 'disabled' : ''}>Add to Cart</button>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            $grid.append(card);
        });
    }

    // Filter & Sort events
    $('#sortSelect, #onSaleFilter').change(function() {
        loadProducts();
    });

    // Add to Cart directly from card button
    $(document).on('click', '.add-to-cart-btn', function(e) {
        e.preventDefault();
        const productId = $(this).data('id');
        const productName = $(this).data('name');
        addToCart(productId, 1, productName);
    });

    // View Details modal
    $(document).on('click', '.view-detail-btn', function() {
        const productId = $(this).data('id');
        openProductDetailModal(productId);
    });

    // Modal Add to Cart button
    $('#modalAddToCartBtn').click(function() {
        const productId = $(this).data('id');
        const quantity = parseInt($('#quantityInput').val());
        const productName = $('#modalName').text();
        if (quantity < 1) {
            alert('Quantity must be at least 1');
            return;
        }
        addToCart(productId, quantity, productName);
    });

    // Review form submission
    $('#reviewForm').submit(function(e) {
        e.preventDefault();
        const productId = $('#reviewProductId').val();
        const rating = parseInt($('#reviewRating').val());
        const comment = $('#reviewComment').val();
        if (!rating) {
            $('#reviewMessage').html('<span class="text-danger">Please select a rating.</span>');
            return;
        }
        $.ajax({
            url: '/api/customer/reviews',
            method: 'POST',
            headers: { ...headers, 'Content-Type': 'application/json' },
            data: JSON.stringify({ productId, rating, comment }),
            success: function(res) {
                $('#reviewMessage').html('<span class="text-success">Review submitted!</span>');
                // Reload reviews for the current product
                loadReviews(productId);
                // Clear form
                $('#reviewRating').val('');
                $('#reviewComment').val('');
            },
            error: function(xhr) {
                $('#reviewMessage').html('<span class="text-danger">' + (xhr.responseJSON?.error || 'Error submitting review') + '</span>');
            }
        });
    });

    // Helper: add to cart API call
    function addToCart(productId, quantity, productName) {
        $.ajax({
            url: '/api/customer/cart/add?productId=' + productId + '&quantity=' + quantity,
            method: 'POST',
            headers: headers,
            success: function(cart) {
                alert(productName + ' added to cart!');
                // Optional: update cart badge count later
            },
            error: function(xhr) {
                alert('Error: ' + (xhr.responseJSON?.error || 'Could not add to cart'));
            }
        });
    }

    // Load product details and reviews into modal
    function openProductDetailModal(productId) {
        // Fetch product details
        $.ajax({
            url: '/api/customer/products/' + productId,
            headers: headers,
            success: function(product) {
                $('#modalImage').attr('src', product.imageUrl || 'https://via.placeholder.com/400x300?text=No+Image');
                $('#modalName').text(product.name);
                $('#modalDescription').text(product.description);
                $('#modalCategory').text(product.category);
                $('#modalOriginalPrice').text(product.onSale ? 'Rs. ' + product.price.toFixed(2) : '');
                $('#modalDiscountedPrice').text('Rs. ' + (product.onSale ? product.discountedPrice.toFixed(2) : product.price.toFixed(2)));
                if (product.onSale) {
                    $('#modalDiscountBadge').text('-' + product.discountPercent + '%').show();
                } else {
                    $('#modalDiscountBadge').hide();
                }
                $('#modalStock').text(product.quantity > 0 ? product.quantity : 'Out of Stock');
                $('#quantityInput').attr('max', product.quantity);
                $('#quantityInput').val(1);
                $('#modalAddToCartBtn').data('id', product.productId);
                if (product.quantity < 1) {
                    $('#modalAddToCartBtn').prop('disabled', true);
                } else {
                    $('#modalAddToCartBtn').prop('disabled', false);
                }

                // Setup review form
                $('#reviewProductId').val(product.productId);
                $('#reviewRating').val('');
                $('#reviewComment').val('');
                $('#reviewMessage').text('');

                // Load reviews
                loadReviews(product.productId);

                // Show modal
                $('#productDetailModal').modal('show');
            },
            error: function() {
                alert('Failed to load product details.');
            }
        });
    }

    // Load reviews for a product
    function loadReviews(productId) {
        $.ajax({
            url: '/api/customer/reviews/product/' + productId,
            headers: headers,
            success: function(reviews) {
                const $container = $('#reviewsContainer');
                if (reviews.length === 0) {
                    $container.html('<p class="text-muted">No reviews yet.</p>');
                    return;
                }
                let html = '';
                reviews.forEach(r => {
                    let stars = '';
                    for (let i = 1; i <= 5; i++) {
                        stars += `<i class="bi bi-star${i <= r.rating ? '-fill' : ''} star-rating"></i>`;
                    }
                    html += `
                        <div class="border-bottom pb-2 mb-2">
                            <strong>${r.userName}</strong> <span>${stars}</span>
                            <p class="mb-0">${r.comment || ''}</p>
                        </div>
                    `;
                });
                $container.html(html);
            },
            error: function() {
                $('#reviewsContainer').html('<p class="text-danger">Failed to load reviews.</p>');
            }
        });
    }
});