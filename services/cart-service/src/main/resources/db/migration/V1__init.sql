CREATE TABLE carts (
    id UUID PRIMARY KEY,
    user_id VARCHAR(120) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_carts_active_user ON carts(user_id) WHERE status = 'ACTIVE';

CREATE TABLE cart_items (
    id UUID PRIMARY KEY,
    cart_id UUID NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id UUID NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL,
    quantity INT NOT NULL,
    UNIQUE (cart_id, product_id)
);
