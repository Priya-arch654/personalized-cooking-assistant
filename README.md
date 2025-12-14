
# 🍳 Personalized Cooking Assistant & Meal Planner

A smart AI-powered cooking assistant and meal planning system that helps users generate recipes based on available ingredients, manage pantry items, and plan healthy meals efficiently. Built using **Java / Java Swing (UI)** with **API integration** and **database support**.


## 📋 Table of Contents

* Features
* Technology Stack
* Project Structure
* Installation & Setup
* Running the Application
* Usage Guide
* Troubleshooting
* API Integration
* Database Schema
* Key Features Explained


## ✨ Features

### 👤 User Features

* 🔐 **User Registration & Login** – Secure authentication system
* 🧺 **Pantry Management** – Add, update, and remove ingredients
* 🍽️ **Recipe Generation** – Get recipes based on available ingredients
* 📅 **Meal Planning** – Plan meals for the day/week
* 🔍 **Recipe Search** – Search recipes by ingredient or category
* 🧾 **History Tracking** – View previously generated recipes



### 🛠️ Admin Features (Optional)

* 👥 **User Management** – View registered users
* 📊 **Usage Monitoring** – Track recipe requests
* 🗂️ **Content Control** – Manage recipe categories



### 🤖 AI & Automation

* Ingredient-based recipe suggestions
* Real-time API response handling
* Smart filtering for missing ingredients



## 🛠️ Technology Stack

### Frontend

* **Java Swing** – GUI design
* **AWT** – UI components

### Backend

* **Java** – Core logic
* **REST API** – Recipe generation

### Database

* **MySQL** – User & pantry data storage
* **JDBC** – Database connectivity

### Tools

* **VS Code / Eclipse**
* **Git & GitHub**



## 📁 Project Structure

```
personalized-cooking-assistant/
├── src/
│   ├── com.project/
│   │   ├── Login.java
│   │   ├── Register.java
│   │   ├── PantryPanel.java
│   │   ├── RecipeGenerator.java
│   │   ├── MealPlanner.java
│   │   └── Main.java
├── assets/
│   └── images/
├── database/
│   └── schema.sql
├── README.md
└── .env (API key)
```



## ⚙️ Installation & Setup

### Prerequisites

* Java JDK **17 or above**
* MySQL Server
* VS Code or Eclipse
* Internet connection (for recipe API)



### Step 1: Clone the Repository

```bash
git clone https://github.com/Priya-arch654/personalized-cooking-assistant.git
```



### Step 2: Open in IDE

* Open **VS Code / Eclipse**
* Select **Open Folder**
* Choose the project directory



### Step 3: Database Setup

1. Start MySQL server
2. Create database:

```sql
CREATE DATABASE cooking_assistant;
```

3. Import `schema.sql`
4. Update DB credentials in code:

```java
String url = "jdbc:mysql://localhost:3306/cooking_assistant";
String user = "root";
String password = "your_password";
```



### Step 4: API Key Configuration

* Open API config file
* Replace with your key:

```java
String API_KEY = "your_api_key_here";
```



## 🚀 Running the Application

### Method 1: Using IDE

1. Open `Main.java`
2. Right-click → **Run**
3. Application window will open



### Method 2: Using Terminal

```bash
javac Main.java
java Main
```



## 👤 Usage Guide

### 🧑 For Users

1. **Register / Login**
2. **Add Ingredients** to pantry
3. Click **Generate Recipe**
4. View recipe instructions
5. Save recipe or add to meal plan



### 🧺 Pantry Management

* Add ingredient name & quantity
* Update or delete items
* Pantry updates reflect instantly



### 🍲 Recipe Generation

* Uses AI API
* Shows ingredients, steps, and cooking time
* Suggests alternatives if ingredients are missing



## 🔧 Troubleshooting

### Common Issues

#### 1. API Not Responding

✔ Check internet connection
✔ Verify API key
✔ Ensure correct endpoint URL



#### 2. Database Connection Error

✔ Ensure MySQL is running
✔ Check username/password
✔ Verify database name



#### 3. Application Not Opening

✔ Run correct `Main.java` file
✔ Check JDK version
✔ Resolve missing libraries



## 🔌 API Integration

* Recipe Generation API
* JSON-based responses
* Error handling for empty results

Example:

```json
{
  "recipe": "Vegetable Fried Rice",
  "ingredients": ["Rice", "Carrot", "Beans"],
  "steps": ["Boil rice", "Stir fry vegetables", "Mix together"]
}
```

---

## 🗃️ Database Schema

### User Table

```
id | name | email | password
```

### Pantry Table

```
id | user_id | ingredient | quantity
```

### Recipe History

```
id | user_id | recipe_name | date
```



## 🎯 Key Features Explained

### Smart Recipe Suggestions

* Uses available ingredients
* Avoids unavailable items
* Reduces food waste

### Secure Authentication

* Password encryption
* Session handling

### User-Friendly UI

* Simple buttons
* Clear instructions
* Beginner-friendly design



## 🚀 Deployment Notes

* Use environment variables for API keys
* Secure database credentials
* Optimize API calls



## 🎉 Conclusion

The **Personalized Cooking Assistant & Meal Planner** simplifies cooking decisions using AI, making daily meal planning efficient, smart, and user-friendly.


>>>>>>> d37358f (Add project code and README)

