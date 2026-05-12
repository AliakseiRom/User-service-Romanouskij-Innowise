-- 1. Create application user
CREATE USER user_service_app WITH PASSWORD 'user_service_pass';

-- 2. Allow DB connection
GRANT CONNECT ON DATABASE user_service TO user_service_app;

-- 3. Allow schema usage
GRANT USAGE ON SCHEMA public TO user_service_app;

-- 4. Grant permissions on existing tables (IMPORTANT for Liquibase)
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE users TO user_service_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE payment_cards TO user_service_app;

-- 5. Grant usage on sequences (BIGSERIAL / IDENTITY)
GRANT USAGE, SELECT ON SEQUENCE users_id_seq TO user_service_app;
GRANT USAGE, SELECT ON SEQUENCE payment_cards_id_seq TO user_service_app;