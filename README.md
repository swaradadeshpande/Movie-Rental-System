# 🎬 Movie Rental Management System 

The Movie Rental System is a console-based Java application that allows users to browse available movies, search for specific movies, rent them, and track rental transactions. The system uses basic Java concepts like OOP, file handling, and collections to manage data efficiently.
A sleek, professional desktop application for managing movie rentals. This project demonstrates a clean separation between backend logic (File I/O and Data Management) and a modern, user-friendly Graphical User Interface (GUI).

## 🚀 Key Improvements in v4.3
- **Smart Search**: Unlike previous versions, the search bar now dynamically filters by **Title**, **Genre**, and **Director** simultaneously.
- **Improved UX**: Users can now press the **Enter** key to search directly from the text field.

## ✨ Features
- **Modern Dashboard**: Responsive sidebar and high-contrast data table.
- **Persistent Storage**: Data is saved locally in `movies.csv` and `rentals.csv`.
- **Transaction History**: Tracks rental dates, return dates, and customer names.
- **Smart Returns**: Filtered selection menu shows only movies currently held by a specific customer.

## 🛠️ Technology Stack
- **Language**: Java 8 or higher
- **Library**: Java Swing & AWT
- **Storage**: CSV (Flat File Database)

## 📁 Project Structure
- `MovieRentalGUI.java`: The main entry point and UI logic.
- `MovieRentalSystem.java`: Core entities (`Movie`, `Rental`) and data handling.
- `movies.csv`: Pre-loaded sample data of top-rated movies.
- `rentals.csv`: Auto-generated log of all rental activities.

## ⚙️ How to Run
1. **Clone the repo**:
   ```bash
   git clone [https://github.com/your-username/movie-rental-system.git](https://github.com/your-username/movie-rental-system.git)
2. Compile:
   ```bash
   javac MovieRentalSystem.java MovieRentalGUI.java
3. Execute:
   ```bash
   java MovieRentalGUI
