$(document).ready(function() {
    const token = localStorage.getItem('token');
    const headers = { 'Authorization': 'Bearer ' + token };

    function loadOrders() {
        $.ajax({
            url: '/api/admin/orders',
            headers: headers,
            success: function(orders) {
                let rows = '';
                orders.forEach(order => {
                    rows += `<tr>
                        <td>${order.orderId}</td>
                        <td>${order.userName}</td>
                        <td>${new Date(order.orderDate).toLocaleString()}</td>
                        <td>${order.totalAmount.toFixed(2)}</td>
                        <td>${order.status}</td>
                        <td>
                            <select class="form-select status-select" data-id="${order.orderId}">
                                <option value="PENDING" ${order.status === 'PENDING' ? 'selected' : ''}>Pending</option>
                                <option value="DISPATCHED" ${order.status === 'DISPATCHED' ? 'selected' : ''}>Dispatched</option>
                                <option value="DELIVERED" ${order.status === 'DELIVERED' ? 'selected' : ''}>Delivered</option>
                            </select>
                        </td>
                    </tr>`;
                });
                $('#adminOrdersTable tbody').html(rows);
            }
        });
    }

    loadOrders();

    $(document).on('change', '.status-select', function() {
        const orderId = $(this).data('id');
        const newStatus = $(this).val();
        $.ajax({
            url: '/api/admin/orders/' + orderId + '/status',
            method: 'PUT',
            headers: headers,
            contentType: 'application/json',
            data: JSON.stringify({ status: newStatus }),
            success: function() {
                alert('Status updated');
                loadOrders();
            },
            error: function(xhr) {
                alert('Error: ' + (xhr.responseJSON?.error || 'Update failed'));
            }
        });
    });
});