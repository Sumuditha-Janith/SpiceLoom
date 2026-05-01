$(document).ready(function() {
    const token = localStorage.getItem('token');
    const headers = { 'Authorization': 'Bearer ' + token };

    function loadOrders() {
        $.ajax({
            url: '/api/customer/orders',
            headers: headers,
            success: function(orders) {
                let rows = '';
                orders.forEach(order => {
                    rows += `<tr>
                        <td>${order.orderId}</td>
                        <td>${new Date(order.orderDate).toLocaleString()}</td>
                        <td>${order.totalAmount.toFixed(2)}</td>
                        <td><span class="badge bg-${order.status === 'DELIVERED' ? 'success' : order.status === 'DISPATCHED' ? 'warning' : 'secondary'}">${order.status}</span></td>
                        <td>
                            <button class="btn btn-sm btn-outline-primary receipt-btn" data-id="${order.orderId}">Receipt PDF</button>
                        </td>
                    </tr>`;
                });
                $('#ordersTable tbody').html(rows);
            },
            error: function() {
                alert('Failed to load orders.');
            }
        });
    }

    loadOrders();

    // Download receipt via AJAX with Authorization header
    $(document).on('click', '.receipt-btn', function() {
        const orderId = $(this).data('id');
        $.ajax({
            url: '/api/customer/orders/' + orderId + '/receipt',
            method: 'GET',
            headers: headers,
            xhrFields: {
                responseType: 'blob'   // important for binary data
            },
            success: function(data, status, xhr) {
                const blob = new Blob([data], { type: 'application/pdf' });
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = 'receipt_' + orderId + '.pdf';
                document.body.appendChild(a);
                a.click();
                a.remove();
                window.URL.revokeObjectURL(url);
            },
            error: function(xhr) {
                alert('Error downloading receipt: ' + (xhr.responseText || 'Access denied'));
            }
        });
    });
});