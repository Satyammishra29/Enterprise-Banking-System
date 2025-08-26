-- Enterprise Banking System Database Schema
-- PostgreSQL Database Schema

-- Create Database
CREATE DATABASE enterprise_banking;
\c enterprise_banking;

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create Tables

-- Users and Authentication
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'CASHIER', 'AUDITOR')),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bank Accounts
CREATE TABLE bank_accounts (
    id SERIAL PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    holder_name VARCHAR(100) NOT NULL,
    account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('SAVINGS', 'CURRENT', 'LOAN')),
    balance DECIMAL(15,2) DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'USD',
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'SUSPENDED', 'CLOSED')),
    minimum_balance DECIMAL(15,2) DEFAULT 0.00,
    interest_rate DECIMAL(5,4) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transactions
CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    transaction_id VARCHAR(50) UNIQUE NOT NULL,
    from_account_number VARCHAR(20),
    to_account_number VARCHAR(20),
    amount DECIMAL(15,2) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER')),
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED')),
    description TEXT,
    performed_by INTEGER REFERENCES users(id),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Audit Logs
CREATE TABLE audit_logs (
    id SERIAL PRIMARY KEY,
    action VARCHAR(100) NOT NULL,
    details TEXT,
    username VARCHAR(50) NOT NULL,
    ip_address INET,
    user_agent TEXT,
    success BOOLEAN DEFAULT true,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Customer Information
CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    customer_id VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    date_of_birth DATE,
    kyc_status VARCHAR(20) DEFAULT 'PENDING' CHECK (kyc_status IN ('PENDING', 'VERIFIED', 'REJECTED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Customer Account Mapping
CREATE TABLE customer_accounts (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER REFERENCES customers(id),
    account_id INTEGER REFERENCES bank_accounts(id),
    relationship_type VARCHAR(20) DEFAULT 'PRIMARY' CHECK (relationship_type IN ('PRIMARY', 'JOINT', 'AUTHORIZED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Account Statements
CREATE TABLE account_statements (
    id SERIAL PRIMARY KEY,
    account_id INTEGER REFERENCES bank_accounts(id),
    statement_date DATE NOT NULL,
    opening_balance DECIMAL(15,2) NOT NULL,
    closing_balance DECIMAL(15,2) NOT NULL,
    total_deposits DECIMAL(15,2) DEFAULT 0.00,
    total_withdrawals DECIMAL(15,2) DEFAULT 0.00,
    generated_by INTEGER REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- System Settings
CREATE TABLE system_settings (
    id SERIAL PRIMARY KEY,
    setting_key VARCHAR(100) UNIQUE NOT NULL,
    setting_value TEXT NOT NULL,
    description TEXT,
    updated_by INTEGER REFERENCES users(id),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Indexes for Performance
CREATE INDEX idx_bank_accounts_account_number ON bank_accounts(account_number);
CREATE INDEX idx_bank_accounts_holder_name ON bank_accounts(holder_name);
CREATE INDEX idx_transactions_from_account ON transactions(from_account_number);
CREATE INDEX idx_transactions_to_account ON transactions(to_account_number);
CREATE INDEX idx_transactions_date ON transactions(transaction_date);
CREATE INDEX idx_audit_logs_username ON audit_logs(username);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_users_username ON users(username);

-- Insert Sample Data

-- Sample Users
INSERT INTO users (username, password_hash, email, role, first_name, last_name) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'admin@bank.com', 'ADMIN', 'System', 'Administrator'),
('cashier', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'cashier@bank.com', 'CASHIER', 'John', 'Cashier'),
('auditor', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'auditor@bank.com', 'AUDITOR', 'Jane', 'Auditor');

-- Sample Bank Accounts
INSERT INTO bank_accounts (account_number, holder_name, account_type, balance, minimum_balance, interest_rate) VALUES
('SAV001', 'John Doe', 'SAVINGS', 5000.00, 500.00, 2.50),
('CUR001', 'Jane Smith', 'CURRENT', 2500.00, 0.00, 0.00),
('SAV002', 'Bob Johnson', 'SAVINGS', 3000.00, 500.00, 2.50),
('CUR002', 'Alice Brown', 'CURRENT', 1500.00, 0.00, 0.00);

-- Sample Transactions
INSERT INTO transactions (transaction_id, from_account_number, to_account_number, amount, transaction_type, status, description, performed_by) VALUES
('TXN001', NULL, 'SAV001', 1000.00, 'DEPOSIT', 'COMPLETED', 'Initial deposit', 1),
('TXN002', 'SAV001', NULL, 500.00, 'WITHDRAWAL', 'COMPLETED', 'ATM withdrawal', 1),
('TXN003', 'SAV001', 'CUR001', 750.00, 'TRANSFER', 'COMPLETED', 'Transfer to current account', 1);

-- Sample System Settings
INSERT INTO system_settings (setting_key, setting_value, description) VALUES
('session_timeout_minutes', '30', 'User session timeout in minutes'),
('max_login_attempts', '3', 'Maximum failed login attempts before lockout'),
('maintenance_mode', 'false', 'System maintenance mode'),
('currency_default', 'USD', 'Default currency for the system');

-- Create Views for Common Queries

-- Account Summary View
CREATE VIEW account_summary AS
SELECT 
    ba.account_number,
    ba.holder_name,
    ba.account_type,
    ba.balance,
    ba.status,
    ba.created_at,
    COUNT(t.id) as transaction_count
FROM bank_accounts ba
LEFT JOIN transactions t ON ba.account_number = t.from_account_number OR ba.account_number = t.to_account_number
GROUP BY ba.id, ba.account_number, ba.holder_name, ba.account_type, ba.balance, ba.status, ba.created_at;

-- Transaction Summary View
CREATE VIEW transaction_summary AS
SELECT 
    DATE(transaction_date) as date,
    transaction_type,
    COUNT(*) as count,
    SUM(amount) as total_amount
FROM transactions
WHERE status = 'COMPLETED'
GROUP BY DATE(transaction_date), transaction_type
ORDER BY date DESC;

-- Create Functions

-- Function to update account balance
CREATE OR REPLACE FUNCTION update_account_balance(
    p_account_number VARCHAR(20),
    p_amount DECIMAL(15,2),
    p_transaction_type VARCHAR(20)
) RETURNS BOOLEAN AS $$
BEGIN
    IF p_transaction_type = 'DEPOSIT' THEN
        UPDATE bank_accounts 
        SET balance = balance + p_amount, updated_at = CURRENT_TIMESTAMP
        WHERE account_number = p_account_number;
    ELSIF p_transaction_type = 'WITHDRAWAL' THEN
        UPDATE bank_accounts 
        SET balance = balance - p_amount, updated_at = CURRENT_TIMESTAMP
        WHERE account_number = p_account_number AND balance >= p_amount;
    END IF;
    
    RETURN FOUND;
END;
$$ LANGUAGE plpgsql;

-- Function to log audit events
CREATE OR REPLACE FUNCTION log_audit_event(
    p_action VARCHAR(100),
    p_details TEXT,
    p_username VARCHAR(50),
    p_ip_address INET DEFAULT NULL,
    p_success BOOLEAN DEFAULT true,
    p_error_message TEXT DEFAULT NULL
) RETURNS VOID AS $$
BEGIN
    INSERT INTO audit_logs (action, details, username, ip_address, success, error_message)
    VALUES (p_action, p_details, p_username, p_ip_address, p_success, p_error_message);
END;
$$ LANGUAGE plpgsql;

-- Create Triggers

-- Trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_bank_accounts_updated_at BEFORE UPDATE ON bank_accounts
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_customers_updated_at BEFORE UPDATE ON customers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Grant Permissions
GRANT ALL PRIVILEGES ON DATABASE enterprise_banking TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO postgres;

-- Display table information
\dt+
