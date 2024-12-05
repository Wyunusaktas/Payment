
CREATE TABLE Payment_Methods (
    method_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,  
    type VARCHAR(50) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    account_number VARCHAR(100) NOT NULL,
    expiry_date DATE NOT NULL,
    is_default BOOLEAN DEFAULT FALSE
);


CREATE TABLE Payments (
    payment_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,  
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    payment_method UUID REFERENCES Payment_Methods(method_id),
    transaction_date TIMESTAMP DEFAULT NOW(),
    description VARCHAR(255)
);


CREATE TABLE Transactions (
    transaction_id UUID PRIMARY KEY,
    payment_id UUID REFERENCES Payments(payment_id),
    status VARCHAR(50) NOT NULL,
    transaction_date TIMESTAMP DEFAULT NOW(),
    amount DECIMAL(10, 2)
);


CREATE TABLE Refunds (
    refund_id UUID PRIMARY KEY,
    payment_id UUID REFERENCES Payments(payment_id),
    refund_amount DECIMAL(10, 2) NOT NULL,
    refund_reason VARCHAR(255),
    status VARCHAR(50),
    refund_date TIMESTAMP DEFAULT NOW()
);

CREATE TABLE Transaction_History (
    history_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,  
    payment_id UUID REFERENCES Payments(payment_id),
    transaction_type VARCHAR(50) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT NOW(),
    status VARCHAR(50) NOT NULL
);


