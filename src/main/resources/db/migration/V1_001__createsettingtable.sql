CREATE TABLE settings (
                          id SERIAL PRIMARY KEY,
                          setting_key VARCHAR(255) NOT NULL UNIQUE,
                          setting_value VARCHAR(255) NOT NULL
);