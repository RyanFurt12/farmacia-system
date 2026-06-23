-- =============================================
-- Seed data — produtos e estoque
-- =============================================

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

INSERT INTO products (name, barcode, price, controlled, stock, supplier) VALUES
('Shampoo Anticaspa 200ml', '7891234560006', 24.90, false, 200, 'FORNECEDOR_A');

INSERT INTO products (name, barcode, price, controlled, stock, supplier) VALUES
('Creme Facial Hidratante 50g', '7891234560007', 39.90, false, 120, 'FORNECEDOR_B');


-- =============================================
-- Purchase intentions seed data
-- =============================================

INSERT INTO purchase_intentions (product_id, quantity, status, created_at) VALUES
(1, 200, 'PENDING', '2024-05-01 10:00:00');

INSERT INTO purchase_intentions (product_id, quantity, status, created_at, reviewed_at) VALUES
(2, 50, 'APPROVED', '2024-05-01 11:00:00', '2024-05-01 14:00:00');

INSERT INTO purchase_intentions (product_id, quantity, status, created_at, reviewed_at) VALUES
(3, 300, 'APPROVED', '2024-05-02 09:00:00', '2024-05-02 10:30:00');

INSERT INTO purchase_intentions (product_id, quantity, status, created_at, reviewed_at) VALUES
(4, 100, 'REJECTED', '2024-05-02 14:00:00', '2024-05-02 16:00:00');
