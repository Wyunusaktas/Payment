    package tr.edu.ogu.ceng.payment.dto;

    import lombok.Data;

    @Data
    public class PaymentAttemptSummaryDTO {
        private Long attemptId;
        private double amount;
        private String attemptStatus;
    }
