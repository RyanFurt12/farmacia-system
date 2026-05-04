-- =============================================
-- Seed data for the pharmacy system
-- =============================================

INSERT INTO clients (cpf, name, email, phone, registration_date) VALUES
('52998224725', 'Maria Silva Santos', 'maria.silva@email.com', '11987654321', '2024-01-15');

INSERT INTO clients (cpf, name, email, phone, registration_date) VALUES
('11144477735', 'João Pedro Oliveira', 'joao.pedro@email.com', '21976543210', '2024-02-20');

INSERT INTO clients (cpf, name, email, phone, registration_date) VALUES
('48314394106', 'Ana Carolina Ferreira', 'ana.carolina@email.com', '31965432109', '2024-03-10');


INSERT INTO products (name, barcode, price, controlled, stock, supplier) VALUES
('Paracetamol 750mg', '7891234560001', 12.90, false, 500, 'FORNECEDOR_A');

INSERT INTO products (name, barcode, price, controlled, stock, supplier) VALUES
('Rivotril 2mg', '7891234560002', 45.50, true, 150, 'FORNECEDOR_B');

INSERT INTO products (name, barcode, price, controlled, stock, supplier) VALUES
('Dipirona 500mg', '7891234560003', 8.90, false, 800, 'FORNECEDOR_A');

INSERT INTO products (name, barcode, price, controlled, stock, supplier) VALUES
('Ritalina 10mg', '7891234560004', 72.00, true, 100, 'FORNECEDOR_A');

INSERT INTO products (name, barcode, price, controlled, stock, supplier) VALUES
('Amoxicilina 500mg', '7891234560005', 28.50, false, 300, 'FORNECEDOR_B');
