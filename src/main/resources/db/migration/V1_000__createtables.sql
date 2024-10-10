-- 1. Currencies Tablosu
CREATE TABLE Currencies (
    currency_code VARCHAR(3) PRIMARY KEY,
    currency_name VARCHAR(50) NOT NULL,
    symbol VARCHAR(5),
    exchange_rate DECIMAL(10, 4),
    last_updated TIMESTAMP DEFAULT NOW()
);

-- 2. Payment_Methods Tablosu
CREATE TABLE Payment_Methods (
    method_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,  -- Kullanıcı ID'si (artık Users tablosuna referans yok)
    type VARCHAR(50) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    account_number VARCHAR(100) NOT NULL,
    expiry_date DATE NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- 3. Payments Tablosu
CREATE TABLE Payments (
    payment_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,  -- Kullanıcı ID'si (artık Users tablosuna referans yok)
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL REFERENCES Currencies(currency_code),
    status VARCHAR(50) NOT NULL,
    payment_method UUID REFERENCES Payment_Methods(method_id),
    transaction_date TIMESTAMP DEFAULT NOW(),
    description VARCHAR(255),
    updated_at TIMESTAMP DEFAULT NOW(),
    external_reference VARCHAR(255),
    discount_applied DECIMAL(10, 2),
    fee_charged DECIMAL(10, 2),
    recurring BOOLEAN DEFAULT FALSE,
    payment_channel VARCHAR(50)
);

-- 4. Transactions Tablosu
CREATE TABLE Transactions (
    transaction_id UUID PRIMARY KEY,
    payment_id UUID REFERENCES Payments(payment_id),
    order_id UUID,
    status VARCHAR(50) NOT NULL,
    transaction_date TIMESTAMP DEFAULT NOW(),
    amount DECIMAL(10, 2),
    currency VARCHAR(3) NOT NULL,
    description VARCHAR(255),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- 5. Refunds Tablosu
CREATE TABLE Refunds (
    refund_id UUID PRIMARY KEY,
    payment_id UUID REFERENCES Payments(payment_id),
    refund_amount DECIMAL(10, 2) NOT NULL,
    refund_reason VARCHAR(255),
    status VARCHAR(50),
    refund_date TIMESTAMP DEFAULT NOW(),
    refund_method VARCHAR(50),
    refund_issued_at TIMESTAMP
);

-- 6. Audit_Logs Tablosu
CREATE TABLE Audit_Logs (
    log_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,  -- Kullanıcı ID'si (artık Users tablosuna referans yok)
    action_type VARCHAR(100) NOT NULL,
    timestamp TIMESTAMP DEFAULT NOW(),
    description VARCHAR(255)
);

-- 7. Discounts Tablosu
CREATE TABLE Discounts (
    discount_id UUID PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    discount_amount DECIMAL(10, 2) NOT NULL,
    discount_type VARCHAR(50) NOT NULL,
    valid_from TIMESTAMP NOT NULL,
    valid_to TIMESTAMP NOT NULL
);

-- 8. Third_Party_Payments Tablosu
CREATE TABLE Third_Party_Payments (
    third_party_payment_id UUID PRIMARY KEY,
    payment_id UUID REFERENCES Payments(payment_id),
    provider VARCHAR(100) NOT NULL,
    transaction_reference VARCHAR(255),
    status VARCHAR(50),
    processed_at TIMESTAMP DEFAULT NOW()
);

-- 9. Payment_Attempts Tablosu
CREATE TABLE Payment_Attempts (
    attempt_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,  -- Kullanıcı ID'si (artık Users tablosuna referans yok)
    payment_method_id UUID REFERENCES Payment_Methods(method_id),
    amount DECIMAL(10, 2) NOT NULL,
    attempt_status VARCHAR(50) NOT NULL,
    attempt_date TIMESTAMP DEFAULT NOW(),
    error_message VARCHAR(255)
);

-- 10. Payment_Fees Tablosu
CREATE TABLE Payment_Fees (
    fee_id UUID PRIMARY KEY,
    payment_id UUID REFERENCES Payments(payment_id),
    fee_type VARCHAR(50) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) REFERENCES Currencies(currency_code),
    created_at TIMESTAMP DEFAULT NOW()
);

-- 11. Failed_Payments Tablosu
CREATE TABLE Failed_Payments (
    failed_payment_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,  -- Kullanıcı ID'si (artık Users tablosuna referans yok)
    payment_method_id UUID REFERENCES Payment_Methods(method_id),
    amount DECIMAL(10, 2) NOT NULL,
    failure_reason VARCHAR(255),
    attempt_date TIMESTAMP DEFAULT NOW()
);

-- 12. Fraud_Detection Tablosu
CREATE TABLE Fraud_Detection (
    fraud_case_id UUID PRIMARY KEY,
    payment_id UUID REFERENCES Payments(payment_id),
    user_id UUID NOT NULL,  -- Kullanıcı ID'si (artık Users tablosuna referans yok)
    suspicious_reason VARCHAR(255),
    fraud_score DECIMAL(5, 2),
    status VARCHAR(50),
    reported_at TIMESTAMP DEFAULT NOW(),
    resolved_at TIMESTAMP
);

-- 13. Error_Logs Tablosu
CREATE TABLE Error_Logs (
    error_id UUID PRIMARY KEY,
    error_message VARCHAR(255) NOT NULL,
    stack_trace TEXT,
    occurred_at TIMESTAMP DEFAULT NOW(),
    resolved BOOLEAN DEFAULT FALSE,
    resolved_at TIMESTAMP
);

-- 14. Transaction_History Tablosu
CREATE TABLE Transaction_History (
    history_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,  -- Kullanıcı ID'si (artık Users tablosuna referans yok)
    payment_id UUID REFERENCES Payments(payment_id),
    transaction_type VARCHAR(50) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT NOW(),
    status VARCHAR(50) NOT NULL
);

-- 15. Chargebacks Tablosu
CREATE TABLE Chargebacks (
    chargeback_id UUID PRIMARY KEY,
    payment_id UUID REFERENCES Payments(payment_id),
    user_id UUID NOT NULL,  -- Kullanıcı ID'si (artık Users tablosuna referans yok)
    chargeback_amount DECIMAL(10, 2) NOT NULL,
    reason VARCHAR(255) NOT NULL,
    filed_at TIMESTAMP DEFAULT NOW(),
    status VARCHAR(50) NOT NULL,
    resolved_at TIMESTAMP
);

-- 16. Payment_Analytics Tablosu
CREATE TABLE Payment_Analytics (
    analytics_id UUID PRIMARY KEY,
    total_payments DECIMAL(10, 2) NOT NULL,
    total_refunds DECIMAL(10, 2),
    average_transaction_value DECIMAL(10, 2),
    payment_channel VARCHAR(50),
    reporting_date TIMESTAMP DEFAULT NOW()
);

-- 17. Sessions Tablosu
CREATE TABLE Sessions (
    session_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,  -- Kullanıcı ID'si (artık Users tablosuna referans yok)
    ip_address VARCHAR(50),
    device VARCHAR(100),
    location VARCHAR(100),
    login_time TIMESTAMP DEFAULT NOW(),
    logout_time TIMESTAMP,
    status VARCHAR(50) NOT NULL
);
