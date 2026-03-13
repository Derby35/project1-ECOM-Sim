# Nile Dot Com - E-Store Simulation

Course: CNT 4714 - Enterprise Computing
Assignment: Project 1
Date: June 2025

## Description

A Java Swing GUI application that simulates an online retail store. The program reads inventory from a CSV file and lets users search for items, build a shopping cart, apply quantity-based discounts, and check out. Each completed order is appended to a transaction log file.

## Features

- Search inventory by item ID
- Add items to cart with real-time stock validation
- Quantity-based discount tiers:
  - 5-9 units: 10% off
  - 10-14 units: 15% off
  - 15+ units: 20% off
- 6% sales tax calculated at checkout
- Purchase invoice displayed in a popup window
- Transaction history written to `output/transactions.csv`
- View past orders through the GUI

## Technologies Used

- Java (Swing / AWT)
- CSV file I/O

## Project Structure

```
nile-dot-com/
├── src/          Java source files
├── data/         inventory.csv (product catalog)
├── output/       transactions.csv (generated on first checkout)
└── README.md
```

## How to Run

Compile and run from the project root directory:

```bash
javac -d bin src/*.java
java -cp bin Main
```

The program must be run from the project root so that the relative paths to `data/` and `output/` resolve correctly.

## Notes

- `output/transactions.csv` is created automatically on first checkout if it does not exist.
- The inventory file (`data/inventory.csv`) lists each product with its ID, description, stock status, quantity, and unit price.
- Items marked as "out of stock" cannot be added to the cart.
