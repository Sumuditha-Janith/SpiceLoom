$(document).ready(function() {
    const token = localStorage.getItem('token');
    const headers = { 'Authorization': 'Bearer ' + token };

    // Load all products
    function loadProducts() {
        $.ajax({
            url: '/api/admin/products',
            headers: headers,
            success: function(products) {
                let rows = '';
                products.forEach(p => {
                    rows += `<tr>
                        <td>${p.productId}</td>
                        <td><img src="${p.imageUrl || ''}" alt="img" style="max-height:50px;"></td>
                        <td>${p.name}</td>
                        <td>${p.category}</td>
                        <td>Rs. ${p.price.toFixed(2)}</td>
                        <td>${p.quantity}</td>
                        <td>
                            <button class="btn btn-sm btn-warning editBtn" data-id="${p.productId}">Edit</button>
                            <button class="btn btn-sm btn-danger deleteBtn" data-id="${p.productId}">Delete</button>
                        </td>
                    </tr>`;
                });
                $('#productTable tbody').html(rows);
            },
            error: function() {
                alert('Failed to load products');
            }
        });
    }

    loadProducts();

    // Clear modal form
    window.clearForm = function() {
        $('#productId').val('');
        $('#name').val('');
        $('#description').val('');
        $('#price').val('');
        $('#quantity').val('');
        $('#category').val('');
        $('#imageFile').val('');
        $('#imagePreview').hide();
        $('.modal-title').text('Add Product');
    };

    // Preview image before upload
    $('#imageFile').change(function() {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                $('#imagePreview').attr('src', e.target.result).show();
            };
            reader.readAsDataURL(file);
        } else {
            $('#imagePreview').hide();
        }
    });

    // Form submission (create or update)
    $('#productForm').submit(function(e) {
        e.preventDefault();
        const id = $('#productId').val();
        const formData = new FormData();
        // Build product JSON
        const productData = {
            name: $('#name').val(),
            description: $('#description').val(),
            price: parseFloat($('#price').val()),
            quantity: parseInt($('#quantity').val()),
            category: $('#category').val()
        };
        formData.append('product', new Blob([JSON.stringify(productData)], {type: 'application/json'}));

        const file = $('#imageFile')[0].files[0];
        if (file) {
            formData.append('image', file);
        }

        let url = '/api/admin/products';
        let method = 'POST';
        if (id) {
            url += '/' + id;
            method = 'PUT';
        }

        $.ajax({
            url: url,
            method: method,
            headers: { 'Authorization': 'Bearer ' + token },
            data: formData,
            processData: false,
            contentType: false,
            success: function() {
                $('#productModal').modal('hide');
                loadProducts();
            },
            error: function(xhr) {
                alert('Error: ' + (xhr.responseJSON?.error || 'Operation failed'));
            }
        });
    });

    // Edit button
    $(document).on('click', '.editBtn', function() {
        const id = $(this).data('id');
        $.ajax({
            url: '/api/admin/products/' + id,
            headers: headers,
            success: function(product) {
                $('#productId').val(product.productId);
                $('#name').val(product.name);
                $('#description').val(product.description);
                $('#price').val(product.price);
                $('#quantity').val(product.quantity);
                $('#category').val(product.category);
                if (product.imageUrl) {
                    $('#imagePreview').attr('src', product.imageUrl).show();
                } else {
                    $('#imagePreview').hide();
                }
                $('.modal-title').text('Edit Product');
                $('#productModal').modal('show');
            },
            error: function() {
                alert('Failed to fetch product details');
            }
        });
    });

    // Delete button
    $(document).on('click', '.deleteBtn', function() {
        if (!confirm('Are you sure you want to delete this product?')) return;
        const id = $(this).data('id');
        $.ajax({
            url: '/api/admin/products/' + id,
            method: 'DELETE',
            headers: headers,
            success: function() {
                loadProducts();
            },
            error: function() {
                alert('Delete failed');
            }
        });
    });
});