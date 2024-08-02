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
public class SchedulerApp
{

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
                // Call the method for manager's menu
                System.out.println("Loading Manager's Menu...");
                // managerMenu();
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

