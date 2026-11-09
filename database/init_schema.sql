-- ============================================================
-- 🏗️  ONLINE AUCTION SYSTEM — Production Database Schema
-- Database: PostgreSQL 15+
-- Encoding: UTF-8
-- Chạy: psql -U postgres -f init_schema.sql
-- ============================================================

-- 0. Tạo Database
CREATE DATABASE online_auction_db
    WITH ENCODING 'UTF8';

-- Kết nối vào database vừa tạo
\c online_auction_db;

-- 1. Kích hoạt extension UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================
-- ENUM TYPES
-- ============================================================

CREATE TYPE user_role AS ENUM ('BIDDER', 'SELLER', 'ADMIN');

CREATE TYPE item_category AS ENUM ('ELECTRONICS', 'ART', 'VEHICLE');

CREATE TYPE auction_status AS ENUM ('OPEN', 'RUNNING', 'FINISHED', 'PAID', 'CANCELED');

CREATE TYPE payment_method AS ENUM ('BANK_TRANSFER', 'CREDIT_CARD', 'E_WALLET');

CREATE TYPE payment_status AS ENUM ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED');

CREATE TYPE notification_type AS ENUM ('BID_UPDATE', 'AUCTION_WON', 'AUCTION_LOST', 'PAYMENT', 'SYSTEM');

-- ============================================================
-- TABLE: users
-- Mapping: Entity → User → Admin / Bidder (Single Table Inheritance)
-- ============================================================

CREATE TABLE users (
    id              UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    username        VARCHAR(50)     NOT NULL,
    password_hash   VARCHAR(256)    NOT NULL,
    full_name       VARCHAR(100)    NOT NULL,
    email           VARCHAR(150)    NOT NULL,
    role            user_role       NOT NULL DEFAULT 'BIDDER',

    -- Bidder-specific (nullable for non-bidder roles)
    balance         DECIMAL(15, 2)  DEFAULT 0.00,

    -- Admin-specific (nullable for non-admin roles)
    admin_level     SMALLINT        DEFAULT NULL,

    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT uq_users_username    UNIQUE (username),
    CONSTRAINT uq_users_email       UNIQUE (email),
    CONSTRAINT ck_users_balance     CHECK (balance >= 0),
    CONSTRAINT ck_users_admin_level CHECK (
        (role != 'ADMIN' AND admin_level IS NULL)
        OR (role = 'ADMIN' AND admin_level BETWEEN 1 AND 10)
    ),
    CONSTRAINT ck_users_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- ============================================================
-- TABLE: sellers (1-to-1 extension of users where role = 'SELLER')
-- ============================================================

CREATE TABLE sellers (
    id              UUID            PRIMARY KEY,
    store_name      VARCHAR(150)    NOT NULL,
    rating          DECIMAL(2, 1)   NOT NULL DEFAULT 0.0,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_sellers_user
        FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT ck_sellers_rating CHECK (rating >= 0.0 AND rating <= 5.0)
);

-- ============================================================
-- TABLE: items (Single Table Inheritance for Art, Electronics, Vehicle)
-- ============================================================

CREATE TABLE items (
    id              UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR(200)    NOT NULL,
    description     TEXT,
    starting_price  DECIMAL(15, 2)  NOT NULL,
    current_price   DECIMAL(15, 2)  NOT NULL,
    seller_id       UUID            NOT NULL,
    category        item_category   NOT NULL,

    -- Electronics-specific
    brand           VARCHAR(100)    DEFAULT NULL,
    warranty_months SMALLINT        DEFAULT NULL,

    -- Art-specific
    artist          VARCHAR(150)    DEFAULT NULL,
    year_created    SMALLINT        DEFAULT NULL,

    -- Vehicle-specific
    model_year      SMALLINT        DEFAULT NULL,
    mileage         DECIMAL(10, 1)  DEFAULT NULL,

    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_items_seller
        FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT ck_items_starting_price  CHECK (starting_price > 0),
    CONSTRAINT ck_items_current_price   CHECK (current_price >= starting_price),
    CONSTRAINT ck_items_warranty        CHECK (warranty_months IS NULL OR warranty_months >= 0),
    CONSTRAINT ck_items_mileage         CHECK (mileage IS NULL OR mileage >= 0)
);

-- ============================================================
-- TABLE: auction_sessions
-- ============================================================

CREATE TABLE auction_sessions (
    id                  UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_id             UUID            NOT NULL,
    current_price       DECIMAL(15, 2)  NOT NULL,
    highest_bidder_id   UUID            DEFAULT NULL,
    start_time          TIMESTAMP       NOT NULL,
    end_time            TIMESTAMP       NOT NULL,
    status              auction_status  NOT NULL DEFAULT 'OPEN',
    bid_count           INT             NOT NULL DEFAULT 0,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_auction_item
        FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE RESTRICT,
    CONSTRAINT fk_auction_highest_bidder
        FOREIGN KEY (highest_bidder_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT ck_auction_time          CHECK (end_time > start_time),
    CONSTRAINT ck_auction_current_price CHECK (current_price > 0),
    CONSTRAINT ck_auction_bid_count     CHECK (bid_count >= 0)
);

-- ============================================================
-- TABLE: bid_transactions
-- ============================================================

CREATE TABLE bid_transactions (
    id          UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    auction_id  UUID            NOT NULL,
    bidder_id   UUID            NOT NULL,
    amount      DECIMAL(15, 2)  NOT NULL,
    bid_time    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_bid_auction
        FOREIGN KEY (auction_id) REFERENCES auction_sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_bid_bidder
        FOREIGN KEY (bidder_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT ck_bid_amount CHECK (amount > 0)
);

-- ============================================================
-- TABLE: payments
-- ============================================================

CREATE TABLE payments (
    id              UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    auction_id      UUID            NOT NULL,
    payer_id        UUID            NOT NULL,
    seller_id       UUID            NOT NULL,
    amount          DECIMAL(15, 2)  NOT NULL,
    method          payment_method  NOT NULL,
    status          payment_status  NOT NULL DEFAULT 'PENDING',
    transaction_ref VARCHAR(100)    DEFAULT NULL,
    paid_at         TIMESTAMP       DEFAULT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payment_auction
        FOREIGN KEY (auction_id) REFERENCES auction_sessions(id) ON DELETE RESTRICT,
    CONSTRAINT fk_payment_payer
        FOREIGN KEY (payer_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_payment_seller
        FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT ck_payment_amount    CHECK (amount > 0),
    CONSTRAINT uq_payment_auction   UNIQUE (auction_id)
);

-- ============================================================
-- TABLE: notifications
-- ============================================================

CREATE TABLE notifications (
    id          UUID                PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID                NOT NULL,
    auction_id  UUID                DEFAULT NULL,
    type        notification_type   NOT NULL,
    content     TEXT                NOT NULL,
    is_read     BOOLEAN             NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_notification_auction
        FOREIGN KEY (auction_id) REFERENCES auction_sessions(id) ON DELETE SET NULL
);

-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_users_role          ON users(role);
CREATE INDEX idx_users_email         ON users(email);
CREATE INDEX idx_users_is_active     ON users(is_active);

CREATE INDEX idx_items_seller        ON items(seller_id);
CREATE INDEX idx_items_category      ON items(category);
CREATE INDEX idx_items_is_active     ON items(is_active);

CREATE INDEX idx_auction_item        ON auction_sessions(item_id);
CREATE INDEX idx_auction_status      ON auction_sessions(status);
CREATE INDEX idx_auction_end_time    ON auction_sessions(end_time);
CREATE INDEX idx_auction_bidder      ON auction_sessions(highest_bidder_id);
CREATE INDEX idx_auction_active      ON auction_sessions(status, end_time)
    WHERE status IN ('OPEN', 'RUNNING');

CREATE INDEX idx_bid_auction         ON bid_transactions(auction_id);
CREATE INDEX idx_bid_bidder          ON bid_transactions(bidder_id);
CREATE INDEX idx_bid_time            ON bid_transactions(bid_time DESC);
CREATE INDEX idx_bid_auction_time    ON bid_transactions(auction_id, bid_time DESC);

CREATE INDEX idx_payment_payer       ON payments(payer_id);
CREATE INDEX idx_payment_status      ON payments(status);

CREATE INDEX idx_notif_user          ON notifications(user_id);
CREATE INDEX idx_notif_unread        ON notifications(user_id, is_read)
    WHERE is_read = FALSE;

-- ============================================================
-- TRIGGERS
-- ============================================================

CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER trg_items_updated_at
    BEFORE UPDATE ON items
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER trg_auction_updated_at
    BEFORE UPDATE ON auction_sessions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

-- Trigger: auto tăng bid_count và cập nhật giá khi có bid mới
CREATE OR REPLACE FUNCTION increment_bid_count()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE auction_sessions
    SET bid_count = bid_count + 1,
        current_price = NEW.amount,
        highest_bidder_id = NEW.bidder_id
    WHERE id = NEW.auction_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_bid_count
    AFTER INSERT ON bid_transactions
    FOR EACH ROW EXECUTE FUNCTION increment_bid_count();

-- ============================================================
-- SAMPLE DATA (để test)
-- ============================================================

-- Admin account (password: admin123 → SHA-256 hash)
INSERT INTO users (id, username, password_hash, full_name, email, role, admin_level)
VALUES (
    uuid_generate_v4(),
    'admin',
    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
    'System Admin',
    'admin@auction.com',
    'ADMIN',
    10
);

-- Seller account (password: seller123 → SHA-256 hash)
INSERT INTO users (id, username, password_hash, full_name, email, role, balance)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'seller01',
    '5e3015acc0e48a18e4a26a77de5803e7a02b037c7f34966e40a21e2a3539e050',
    'Nguyen Van Seller',
    'seller@auction.com',
    'SELLER',
    0.00
);

INSERT INTO sellers (id, store_name, rating)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'Shop Dien Tu ABC',
    4.5
);

-- Bidder account (password: bidder123 → SHA-256 hash)
INSERT INTO users (id, username, password_hash, full_name, email, role, balance)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    'bidder01',
    'f36df5c82f52c5738917a74d3a98eb642660e9abfa89899bd6318db5b0adb783',
    'Tran Thi Bidder',
    'bidder@auction.com',
    'BIDDER',
    5000.00
);
