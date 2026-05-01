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
                            <a href="/api/customer/orders/${order.orderId}/receipt" class="btn btn-sm btn-outline-primary" target="_blank">Receipt PDF</a>
                        </td>
                    </tr>`;
                });
                $('#ordersTable tbody').html(rows);
            }
        });
    }

    loadOrders();
});