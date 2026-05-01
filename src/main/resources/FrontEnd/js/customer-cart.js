$(document).ready(function() {
    const token = localStorage.getItem('token');
    const headers = { 'Authorization': 'Bearer ' + token };

    function loadCart() {
        $.ajax({
            url: '/api/customer/cart',
            headers: headers,
            success: function(cart) {
                const $tbody = $('#cartTable tbody');
                $tbody.empty();
                if (cart.items.length === 0) {
                    $('#cartTable').hide();
                    $('#emptyCartMessage').show();
                    $('#cartTotal').text('0.00');
                    return;
                }
                $('#cartTable').show();
                $('#emptyCartMessage').hide();
                cart.items.forEach(item => {
                    const price = item.discountedPrice;
                    const subtotal = price * item.quantity;
                    $tbody.append(`
                        <tr>
                            <td>${item.productName}</td>
                            <td>Rs. ${price.toFixed(2)}${item.discountPercent > 0 ? '<br><small class="text-muted"><s>Rs. '+item.price.toFixed(2)+'</s></small>' : ''}</td>
                            <td>
                                <input type="number" class="form-control qty-input" data-id="${item.cartItemId}" value="${item.quantity}" min="1" max="${item.availableStock}" style="width:80px;">
                            </td>
                            <td>Rs. ${subtotal.toFixed(2)}</td>
                            <td><button class="btn btn-sm btn-danger remove-btn" data-id="${item.cartItemId}">Remove</button></td>
                        </tr>
                    `);
                });
                $('#cartTotal').text(cart.total.toFixed(2));
            },
            error: function() {
                alert('Unable to load cart.');
            }
        });
    }

    loadCart();

    $(document).on('change', '.qty-input', function() {
        const cartItemId = $(this).data('id');
        const newQty = parseInt($(this).val());
        if (newQty < 1) return;
        $.ajax({
            url: '/api/customer/cart/item/' + cartItemId + '?quantity=' + newQty,
            method: 'PUT',
            headers: headers,
            success: function(cart) {
                loadCart();
            },
            error: function(xhr) {
                alert(xhr.responseJSON?.error || 'Error updating quantity');
            }
        });
    });

    $(document).on('click', '.remove-btn', function() {
        const cartItemId = $(this).data('id');
        $.ajax({
            url: '/api/customer/cart/item/' + cartItemId,
            method: 'DELETE',
            headers: headers,
            success: function() {
                loadCart();
            },
            error: function() {
                alert('Failed to remove item.');
            }
        });
    });

    $('#checkoutBtn').click(function() {
        if (!confirm('Place order?')) return;
        $.ajax({
            url: '/api/customer/cart/checkout',
            method: 'POST',
            headers: headers,
            success: function() {
                alert('Order placed!');
                window.location.href = 'customer-orders.html';
            },
            error: function(xhr) {
                alert(xhr.responseJSON?.error || 'Checkout failed');
            }
        });
    });
});