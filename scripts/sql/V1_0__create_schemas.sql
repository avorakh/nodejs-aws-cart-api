DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'cart_status') THEN
        CREATE TYPE cart_status AS ENUM ('OPEN', 'ORDERED');
    END IF;
END $$;


CREATE TABLE IF NOT EXISTS carts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    created_at DATE NOT NULL DEFAULT current_date,
    updated_at DATE NOT NULL DEFAULT current_date,
    status cart_status NOT NULL
);


CREATE TABLE IF NOT EXISTS cart_items (
    cart_id UUID REFERENCES carts(id),
    product_id UUID,
    count INTEGER,
    PRIMARY KEY (cart_id, product_id)
);


CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    user_id UUID,
    cart_id UUID REFERENCES carts(id),
    payment JSON,
    delivery JSON,
    comments TEXT,
    status TEXT,
    total NUMERIC
);

