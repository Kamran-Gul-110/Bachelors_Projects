/* AL-SHIFA BLOOD BANK MANAGEMENT SYSTEM - DATABASE SCRIPT
   Author: Kamran Gul
   
   INSTRUCTIONS:
   Step 1: Run this entire script to create the database and tables.
   Step 2: Ensure this is run BEFORE starting your Java application.
   Step 3: Tables must be created in this specific order due to Foreign Key dependencies.
*/

-- 1. Create and Use Database
CREATE DATABASE IF NOT EXISTS al_shifa_db;
USE al_shifa_db;


-- 2. Create BLOOD_STOCK Table (Lookup Table)
-- Create this first so 'donors' can reference it
CREATE TABLE blood_stock (
    blood_group VARCHAR(5) PRIMARY KEY,
    total_units INT DEFAULT 0
);

-- 3. Create DONORS Table
CREATE TABLE donors (
    donor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    father_name VARCHAR(50),
    blood_group VARCHAR(5),
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    city VARCHAR(50),
    status VARCHAR(15) DEFAULT 'emergency',
    FOREIGN KEY (blood_group) REFERENCES blood_stock(blood_group)
);

-- 4. Create DONATIONS Table (Transactional Log)
CREATE TABLE donations (
    donation_id INT AUTO_INCREMENT PRIMARY KEY,
    donor_id INT NOT NULL,
    units_donated INT NOT NULL,
    donation_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (donor_id) REFERENCES donors(donor_id) ON DELETE CASCADE
);


-- 5. Create ADMIN Table
CREATE TABLE admins (
    admin_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    father_name VARCHAR(50),
    contact_number VARCHAR(20),
    admin_password VARCHAR(50) NOT NULL
);

-- 6. Seed Initial Data

-- Populate the stock table with all blood groups
INSERT INTO blood_stock (blood_group, total_units) VALUES 
('A+', 0), ('A-', 0), 
('B+', 0), ('B-', 0), 
('AB+', 0), ('AB-', 0), 
('O+', 0), ('O-', 0);

-- Create a default admin for your first login
INSERT INTO admins (admin_id, name, father_name, contact_number, admin_password) 
VALUES ('admin@001', 'Kamran Gul', 'Father Name', '03001234567', 'kamran@001');


-- Verification Query
SHOW TABLES;