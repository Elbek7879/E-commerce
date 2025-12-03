CREATE TABLE products (
                          id BIGINT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          price DECIMAL(10,2) NOT NULL,
                          stock INT NOT NULL,
                          category VARCHAR(255),
                          is_active BOOLEAN,
                          created_at TIMESTAMP
);
