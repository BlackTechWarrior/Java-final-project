// class Manager extends Employee {
//     public Manager(String username, String password, int ID, String name) {
//         super(ID, password, name);
//     }

//     // Add manager-specific methods here
//     // For example: changeEmployeeTimes, createNewTimes
// }

// class worker extends Employee {
//     public worker(String username, String password, int ID, String name) {
//         super(ID, password, name);
//     }

//     // Add worker-specific methods here
//     // For example: viewTimes, requestTimeChange
// }

// public class SchedulerApp {
//     public static void main(String[] args) {
//         Scanner scanner = new Scanner(System.in);
//         methods print = new methods();

//         print.Opening();

//         Employee employee = new Employee("employee123", 100, "John Doe");
//         Manager manager = new Manager("manager123", 200, "jane Doe");

//         System.out.print("Enter username: ");
//         String username = scanner.nextLine();
//         System.out.print("Enter password: ");
//         String password = scanner.nextLine();

//         if (manager.authenticate(username, password)) {
//             System.out.println("Welcome, Manager!");
//             // Implement manager-specific functionality
//         } else if (employee.authenticate(username, password)) {
//             System.out.println("Welcome, Employee!");
//             // Implement employee-specific functionality
//         } else {
//             System.out.println("Invalid credentials. Please try again.");
//         }
//     }
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Scanner;
public class SchedulerApp {

    private static final String EMPLOYEE_FILE = "employee.txt";
    private static Set<String> existingIDs = new HashSet<>();

    public static void main(String[] args) {
        loadExistingIDs(); // Load existing IDs from the file
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the scheduler.");
        System.out.print("Are you logging in as the Manager or Employee? ");
        String role = scanner.nextLine().trim().toLowerCase();

        switch (role) {
            case "manager":
                handleManagerLogin(scanner);
                break;

            case "employee":
                System.out.print("Enter Employee ID (or enter '0000' to create a new employee): ");
                String employeeID = scanner.nextLine().trim();

                if (employeeID.equals("0000")) {
                    createNewEmployee(scanner);
                } else if (existingIDs.contains(employeeID)) {
                    System.out.println("Loading Employee's Menu...");
                    // employeeMenu(employeeID);
                } else {
                    System.out.println("Invalid ID. Please try again.");
                }
                break;
            default:
                System.out.println("Invalid option. Please choose either 'Manager' or 'Employee'.");
                break;
        }

        scanner.close();
    }

    private static void handleManagerLogin(Scanner scanner) {
        System.out.print("Enter Manager ID: ");
        String managerID = scanner.nextLine().trim();

        System.out.print("Enter Manager Password: ");
        String managerPassword = scanner.nextLine().trim();

        if (Manager.authenticate(managerID, managerPassword)) {
            String managerName = Manager.getManagerName(managerID);
            System.out.println("Welcome, " + managerName + "!");
            // Implement manager-specific functionality
        } else {
            System.out.println("Invalid ID or Password. Please try again.");
        }
    }

    private static void createNewEmployee(Scanner scanner) {
        System.out.println("New Employee Setup");

        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter your role (lead/member): ");
        String role = scanner.nextLine().trim().toLowerCase();

        String newID = generateUniqueID(scanner);

        String schedule = "";
        if (role.equals("lead")) {
            System.out.println("As a Lead, your schedule will be set to full-time.");
            schedule = "Full-time (40 hours)";
        } else if (role.equals("member")) {
            System.out.println("As a Member, you can customize your schedule.");
            schedule = setCustomSchedule(scanner);
        } else {
            System.out.println("Invalid role. Please enter 'lead' or 'member'.");
            return; // You may loop back or handle this differently.
        }

        saveEmployeeToFile(name, role, newID, schedule);

        System.out.println("Employee created successfully! You can now log in using your new ID.");
    }

    private static String generateUniqueID(Scanner scanner) {
        String newID;
        do {
            System.out.print("Enter a unique 4-digit Employee ID: ");
            newID = scanner.nextLine().trim();
            if (newID.equals("0000")) {
                System.out.println("ID cannot be '0000'. Please choose a different ID.");
            } else if (newID.length() != 4 || !newID.matches("\\d+")) {
                System.out.println("ID must be a 4-digit number.");
            } else if (existingIDs.contains(newID)) {
                System.out.println("This ID is already taken. Please choose a different one.");
            }
        } while (newID.equals("0000") || newID.length() != 4 || !newID.matches("\\d+") || existingIDs.contains(newID));

        existingIDs.add(newID);
        return newID;
    }

    private static String setCustomSchedule(Scanner scanner) {
        System.out.println("Enter your availability for each day (e.g. 'M-AM, T-PM, W-AM, TH-AM, F-NO', Sat-open, Sun-PM or 'Full-time')");
        String availability = scanner.nextLine().trim();
        return "Custom schedule: " + availability;
    }

    private static void saveEmployeeToFile(String name, String role, String id, String schedule) {
        try (FileWriter writer = new FileWriter(EMPLOYEE_FILE, true)) {
            writer.write("Employee Name: " + name + "\n");
            writer.write("Role: " + role + "\n");
            writer.write("Employee ID: " + id + "\n");
            writer.write("Schedule: " + schedule + "\n");
            writer.write("------------------------------\n");
            System.out.println("Employee information saved successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while saving employee information.");
            e.printStackTrace();
        }
    }

    private static void loadExistingIDs() {
        File file = new File(EMPLOYEE_FILE);
        if (!file.exists()) {
            System.out.println("Employee file does not exist. No existing IDs to load.");
            return;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.startsWith("Employee ID: ")) {
                    String id = line.substring(13).trim();
                    existingIDs.add(id);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while loading existing employee IDs.");
            e.printStackTrace();
        }
    }
}

// private static void managerMenu() {
//     System.out.println("Loading Manager's Menu...");
//     // Implement manager-specific functionality here
// }
