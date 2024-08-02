import java.util.Scanner;

class Manager extends Employee {
    public Manager(String username, String password, int ID, String name) {
        super(ID, password, name);
    }

    // Add manager-specific methods here
    // For example: changeEmployeeTimes, createNewTimes
}

class worker extends Employee {
    public worker(String username, String password, int ID, String name) {
        super(ID, password, name);
    }

    // Add worker-specific methods here
    // For example: viewTimes, requestTimeChange
}

public class SchedulerApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        methods print = new methods();

        print.Opening();

        Employee employee = new Employee("employee123", 100, "John Doe");
        Manager manager = new Manager("manager123", 200, "jane Doe");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (manager.authenticate(username, password)) {
            System.out.println("Welcome, Manager!");
            // Implement manager-specific functionality
        } else if (employee.authenticate(username, password)) {
            System.out.println("Welcome, Employee!");
            // Implement employee-specific functionality
        } else {
            System.out.println("Invalid credentials. Please try again.");
        }
    }
}
