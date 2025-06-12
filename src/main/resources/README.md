 # SplitApp - Expense Splitter

A RESTful backend application built with **Spring Boot** and **PostgreSQL** that allows users to split shared expenses among participants, track balances, and calculate settlements.

---

## Features

- Add, update, delete expenses (equal, exact, or percentage split)
- Auto-create people if not already present
- Track balances for all users
- Simplify settlements (who owes whom and how much)
- REST API tested via Postman
- Validations and edge case handling

---

## 🛠️ Tech Stack
- Backend: Spring Boot (Java)
- Database: PostgreSQL
- Build Tool: Maven
- API Testing: Postman
- Deployment: Railway / Render (suggested platforms)


## 📦 Postman Collection

🧪 Test your APIs via this collection:

📎 **Public Gist:** [Postman Collection](https://gist.github.com/your-username/your-gist-id)

- Uses `{{baseUrl}} = https://your-api-domain.com`
- Organized folders for:
    - Expenses (Add, Update, Delete)
    - Balances & Settlements
    - Invalid Inputs Testing

---

## 🧑‍💻 Local Setup Instructions

### 1. Clone Repository

```bash
git clone https://github.com/JanviSawalkar/SplitApp.git
cd splitapp
