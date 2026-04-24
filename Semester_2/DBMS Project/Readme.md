# Al-Shifa Blood Bank Management System
**A Relational Database Solution for Secure Medical Inventory**

## 1. Project Description
The Al-Shifa Blood Bank Management System is a centralized relational database application designed to modernize blood donation operations. Developed using Java for the frontend and MySQL for the backend, the system ensures high data integrity and medical safety by automating donor eligibility checks and real-time inventory tracking. By replacing manual ledgers with atomic database transactions, Al-Shifa eliminates common clerical errors and drastically reduces response times during medical emergencies.

## 2. Developer Information
* **Name:** Kamran Gul
* **CMS ID:** 023-25-0161
* **Section:** CS-AI (C)
* **Role:** Lead Developer (Solo Project)

---

## 3. Project Purpose & Problem Statement

### The Problem
Traditional blood bank management relies on fragmented manual records, leading to three critical failures:
1.  **Safety Risks:** No automated way to prevent donors from donating before the mandatory **90-day recovery period**.
2.  **Inventory Lag:** Real-time stock levels are often inaccurate due to delayed manual updates.
3.  **Emergency Delays:** Inefficient searching for specific blood groups in specific cities during crises.

### The Solution
Al-Shifa solves these by acting as a **Single Source of Truth**. It uses database-level constraints and temporal logic to enforce medical safety and provide instant, accurate inventory reporting.

---

## 4. Main Modules & Key Features

### A. Administrative Module
* **Secure Authentication:** Access control via the `admins` table.
* **Donor Onboarding:** Adding new donors with unique constraint validation.
* **Atomic Donation Recording:** A single operation that logs the donation, increments the `blood_stock`, and updates the donor's eligibility status.

### B. User/Donor Module
* **Self-Registration:** Automated ID generation for new community members.
* **Emergency Search:** High-speed queries to find available donors filtered by **blood_group** and **city**.
* **Personal Records:** Access to individual donation history using a unique ID.

### C. Database Logic (The 90-Day Rule)
The system prevents over-donation by executing a `MAX(donation_date)` query. If the time elapsed is less than 90 days, the database prevents the entry, ensuring donor health.

---

## 5. Database Schema
The system is normalized to **3rd Normal Form (3NF)** to prevent data anomalies.

| Table | Purpose | Key Features |
| :--- | :--- | :--- |
| **admins** | System access. | PK on `admin_id` |
| **donors** | Static profiles. | `AUTO_INCREMENT`, `UNIQUE` phone |
| **donations** | Transaction log. | `FK` to donors, `DEFAULT` timestamps |
| **blood_stock** | Inventory levels. | PK on `blood_group` |

---

## 6. How to Run (Setup Instructions)

### Prerequisites
* **IntelliJ IDEA** (Community or Ultimate)
* **JDK 8 or higher**
* **MySQL Server** (8.0+)
* **MySQL Connector/J** (.jar file)

### Step 1: Database Configuration
1.  Open **MySQL Workbench** or Command Line.
2.  Create the database: 
    ```sql
    CREATE DATABASE al_shifa_db;
    ```
3.  Run the provided `.sql` script in this repository to generate the tables and initial stock.

### Step 2: Setting up IntelliJ IDEA
1.  **Open Project:** Launch IntelliJ and select `Open`. Navigate to the project folder.
2.  **Add Database Driver:**
    * Go to `File` > `Project Structure` > `Libraries`.
    * Click the **+** (plus) icon and select `Java`.
    * Navigate to your `mysql-connector-java-x.x.x.jar` file and click **OK**.
    * Apply and close.
3.  **Configure Connection:**
    * Open the file where database credentials are stored (e.g., `DBConnection.java`).
    * Update the `URL`, `USER`, and `PASSWORD` to match your local MySQL settings.

### Step 3: Compile and Run
1.  In the Project tool window, right-click the `Main.java` file (or your starting class).
2.  Select **Run 'Main.main()'**.
3.  The application GUI/Console should launch successfully.

---

## 7. Technical Stack
* **Language:** Java
* **GUI:** Swing
* **Database:** MySQL
* **Connection:** JDBC (using `PreparedStatement` for security)


