package prac7;
import java.io.*;
import java.util.*;

class Student {
    String name;
    int age;
    String grade;
    
    public Student(String name, int age, String grade) {
        this.name = name;
        this.age = age;
        this.grade = grade;
    }
    
    @Override
    public String toString() {
        return name + "," + age + "," + grade;
    }
}

class Manager {
    private ArrayList<Student> studentList = new ArrayList<>();
    private final String FILE_NAME = "student.txt";
    
    // Constructor automatically loads data on startup
    public Manager() {
        loadFromFile();
    }
    
    public void add_student(Student s) {
        studentList.add(s);
        System.out.println("Student added: " + s.name);
    }
    
    public void display() {
        if (studentList.isEmpty()) {
            System.out.println("No records found!");
        } else {
            System.out.println("\n--- All Student Records ---");
            for (Student s : studentList) {
                System.out.println("Name: " + s.name + " | Age: " + s.age + " | Grade: " + s.grade);
            }
        }
    }

    public void search_name(String name) {
        boolean found = false;
        for (Student s : studentList) {
            if (s.name.equalsIgnoreCase(name)) {
                System.out.println("Student found! Details: Name: " + s.name + " | Age: " + s.age + " | Grade: " + s.grade);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Student not found!");
        }
    }
    
    // Method to Update/Modify existing student details
    public void modify_student(String name, int newAge, String newGrade) {
        boolean found = false;
        for (Student s : studentList) {
            if (s.name.equalsIgnoreCase(name)) {
                s.age = newAge;
                s.grade = newGrade;
                System.out.println("Record updated successfully for: " + name);
                found = true;
                break; // Exit loop once found and updated
            }
        }
        if (!found) {
            System.out.println("Student not found! Cannot modify.");
        }
    }

    public void delete_student(String name) {
        boolean removed = studentList.removeIf(s -> s.name.equalsIgnoreCase(name));
        if (removed) {
            System.out.println("Record of student removed: " + name);
        } else {
            System.out.println("Record not found!");
        }
    }

    // File Handling: Save data to file
    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Student s : studentList) {
                bw.write(s.toString());
                bw.newLine();
            }
            System.out.println("Data saved successfully to " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // File Handling: Load data from file
    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return; // If file doesn't exist yet, just start with an empty list
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0];
                    int age = Integer.parseInt(parts[1]);
                    String grade = parts[2];
                    studentList.add(new Student(name, age, grade));
                }
            }
            System.out.println("Data loaded successfully from " + FILE_NAME);
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }
}

public class student_management {

    public static void main(String[] args) {
        Manager m = new Manager();
        Scanner sc = new Scanner(System.in);
        int c = 0; // Declared outside the loop to be accessible in the while condition
        
        do {
            System.out.println("\n---- Student Management System ----");
            System.out.println("1. Add new student");
            System.out.println("2. Display all students");
            System.out.println("3. Search student by name");
            System.out.println("4. Modify student record");
            System.out.println("5. Delete student");
            System.out.println("6. Save and Exit");
            System.out.print("Choice: ");
            
            // Check to prevent InputMismatchException if user types a letter
            if (sc.hasNextInt()) {
                c = sc.nextInt();
                sc.nextLine(); // Consume the newline character
            } else {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine(); // Clear the bad input
                continue;
            }
            
            switch(c) {
                case 1:
                    System.out.print("Enter name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter age: ");
                    int age = sc.nextInt();
                    sc.nextLine(); // Consume newline
                    System.out.print("Enter grade: ");
                    String grade = sc.nextLine();
                    m.add_student(new Student(name, age, grade));
                    break;
                case 2:
                    m.display();
                    break;
                case 3:
                    System.out.print("Enter name to search: ");
                    String searchName = sc.nextLine();
                    m.search_name(searchName);
                    break;
                case 4:
                    System.out.print("Enter name of student to modify: ");
                    String modName = sc.nextLine();
                    System.out.print("Enter new age: ");
                    int newAge = sc.nextInt();
                    sc.nextLine(); // Consume newline
                    System.out.print("Enter new grade: ");
                    String newGrade = sc.nextLine();
                    m.modify_student(modName, newAge, newGrade);
                    break;
                case 5:
                    System.out.print("Enter name to delete: ");
                    String delName = sc.nextLine();
                    m.delete_student(delName);
                    break;
                case 6:
                    m.saveToFile();
                    System.out.println("Exiting system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice! Please select an option from 1 to 6.");
            }
        } while (c != 6);
        
        sc.close();
    }
}
