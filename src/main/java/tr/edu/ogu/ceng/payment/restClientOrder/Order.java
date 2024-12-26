package tr.edu.ogu.ceng.payment.restClientOrder;

public class Order {
    private String orderId;  // Order ID
    private String userId;   // User ID
    private double totalAmount;  // Total amount for the order
    private Boolean paymentStatus;  // Payment status (true for paid, false for unpaid)

    // Getter and Setter methods for orderId
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    // Getter and Setter methods for userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter and Setter methods for totalAmount
    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    // Getter and Setter methods for paymentStatus
    public Boolean getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
