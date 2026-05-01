$(document).ready(function() {
    const token = localStorage.getItem('token');
    const headers = { 'Authorization': 'Bearer ' + token };

    // Load product list for dropdown
    function loadProductsForDropdown() {
        $.ajax({
            url: '/api/admin/products',
            headers: headers,
            success: function(products) {
                let options = '<option value="">Global (all products)</option>';
                products.forEach(p => {
                    options += `<option value="${p.productId}">${p.name}</option>`;
                });
                $('#productIdSelect').html(options);
            }
        });
    }

    // Load sale events table
    function loadSales() {
        $.ajax({
            url: '/api/admin/sales',
            headers: headers,
            success: function(events) {
                let rows = '';
                events.forEach(e => {
                    rows += `<tr>
                        <td>${e.saleEventId}</td>
                        <td>${e.productId ? 'Product #' + e.productId : 'Global'}</td>
                        <td>${e.discountPercent}%</td>
                        <td>${e.startDate ? new Date(e.startDate).toLocaleString() : ''}</td>
                        <td>${e.endDate ? new Date(e.endDate).toLocaleString() : ''}</td>
                        <td>${e.active ? '<span class="badge bg-success">Yes</span>' : '<span class="badge bg-secondary">No</span>'}</td>
                        <td>
                            <button class="btn btn-sm btn-warning editSaleBtn" data-id="${e.saleEventId}">Edit</button>
                            <button class="btn btn-sm btn-danger deleteSaleBtn" data-id="${e.saleEventId}">Delete</button>
                        </td>
                    </tr>`;
                });
                $('#saleTable tbody').html(rows);
            }
        });
    }

    loadProductsForDropdown();
    loadSales();

    window.clearSaleForm = function() {
        $('#saleEventId').val('');
        $('#productIdSelect').val('');
        $('#discountPercent').val('');
        $('#startDate').val('');
        $('#endDate').val('');
        $('#activeCheck').prop('checked', true);
        $('#saleValidationError').hide().text('');
        $('.modal-title').text('Add Sale Event');
    };

    // Submit sale form with end date validation
    $('#saleForm').submit(function(e) {
        e.preventDefault();
        const startVal = $('#startDate').val();
        const endVal = $('#endDate').val();

        if (endVal && startVal && endVal <= startVal) {
            $('#saleValidationError').show().text('End Date & Time must be after Start Date & Time.');
            return;
        }
        $('#saleValidationError').hide();

        const id = $('#saleEventId').val();
        const data = {
            productId: $('#productIdSelect').val() || null,
            discountPercent: parseInt($('#discountPercent').val()),
            startDate: startVal + ':00',
            endDate: endVal + ':00',
            active: $('#activeCheck').is(':checked')
        };
        if (data.productId === '') data.productId = null;
        else if (data.productId) data.productId = parseInt(data.productId);

        let url = '/api/admin/sales';
        let method = 'POST';
        if (id) {
            url += '/' + id;
            method = 'PUT';
        }

        $.ajax({
            url: url,
            method: method,
            headers: headers,
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function() {
                $('#saleModal').modal('hide');
                loadSales();
            },
            error: function(xhr) {
                const errMsg = xhr.responseJSON?.error || 'Operation failed';
                alert('Error: ' + errMsg);
            }
        });
    });

    // Edit sale
    $(document).on('click', '.editSaleBtn', function() {
        const id = $(this).data('id');
        $.ajax({
            url: '/api/admin/sales',
            headers: headers,
            success: function(events) {
                const event = events.find(e => e.saleEventId === id);
                if (event) {
                    $('#saleEventId').val(event.saleEventId);
                    $('#productIdSelect').val(event.productId || '');
                    $('#discountPercent').val(event.discountPercent);
                    const start = new Date(event.startDate);
                    const end = new Date(event.endDate);
                    $('#startDate').val(start.toISOString().slice(0,16));
                    $('#endDate').val(end.toISOString().slice(0,16));
                    $('#activeCheck').prop('checked', event.active);
                    $('#saleValidationError').hide().text('');
                    $('.modal-title').text('Edit Sale Event');
                    $('#saleModal').modal('show');
                }
            }
        });
    });

    // Delete sale
    $(document).on('click', '.deleteSaleBtn', function() {
        if (!confirm('Delete this sale event?')) return;
        const id = $(this).data('id');
        $.ajax({
            url: '/api/admin/sales/' + id,
            method: 'DELETE',
            headers: headers,
            success: function() {
                loadSales();
            },
            error: function() {
                alert('Delete failed');
            }
        });
    });

    // Logout and auth check
    const tokenCheck = localStorage.getItem('token');
    if (!tokenCheck) window.location.href = 'login.html';
    $('#logoutBtn').click(function() {
        localStorage.clear();
        window.location.href = '../index.html';
    });
});