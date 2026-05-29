CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE products (
    id UUID PRIMARY KEY,
    category_id UUID NOT NULL REFERENCES categories(id),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price NUMERIC(12, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    stock INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_category ON products(category_id);
