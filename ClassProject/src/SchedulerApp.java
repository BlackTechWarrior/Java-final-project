import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class SchedulerApp 
{

    private static final String EMPLOYEE_FILE = "employee.txt";
    private static Set<String> existingIDs = new HashSet<>();

    public static void main(String[] args) 
    {
        loadExistingIDs(); // Load existing IDs from the file
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the scheduler.");
        System.out.print("Are you logging in as the Manager or Employee? ");
        String role = scanner.nextLine().trim().toLowerCase();
        switch (role) 
        {
            case "manager":
                handleManagerLogin(scanner);
                break;

            case "employee":
                System.out.print("Enter Employee ID (or enter '0000' to create a new employee): ");
                String employeeID = scanner.nextLine().trim();

                if (employeeID.equals("0000")) 
                {
                    createNewEmployee(scanner);
                } 
                else if (existingIDs.contains(employeeID)) 
                {
                    employeeID = handleEmployeeLogin(scanner, employeeID);  
                    // employeeMenu(employeeID);
                } else 
                {
                    System.out.println("Invalid ID. Please try again.");
                }
                break;

            default:
                System.out.println("Invalid option. Please choose either 'Manager' or 'Employee'.");
                break;
        }

        scanner.close();
    }

    public static String handleEmployeeLogin(Scanner scanner, String employeeID) 
    {
        Console empPassConsole = System.console();
        String employeePassword;
        if (empPassConsole != null) 
        {
            // If console is available (e.g., when running in a terminal)
            char[] empPasswordArray = empPassConsole.readPassword("Enter Employee Password: ");
            employeePassword = new String(empPasswordArray);
        } 
        else 
        {
            // If console is not available (e.g., when running in some IDEs)
            System.out.print("Enter Employee Password: ");
            employeePassword = scanner.nextLine().trim();
        }

        if (Employee.authenticate(employeeID, employeePassword)) 
        {
            String employeeName = Employee.getEmployeeName(employeeID);
            System.out.println("Welcome, " + employeeName + "!");
            employeeMenu(scanner, employeeID);         
            
        } 
        else 
        {
            System.out.println("Invalid ID or Password. Please try again.");
            return null;
        }

        return employeeID; // Return the employeeID to be used in the switch case
    }

    

    private static void handleManagerLogin(Scanner scanner) 
    {
        System.out.print("Enter Manager ID: ");
        String managerID = scanner.nextLine().trim();

        Console manPassConsole = System.console();

        String managerPassword;
        if (manPassConsole != null) 
        {
            // If console is available (e.g., when running in a terminal)
            char[] passwordArray = manPassConsole.readPassword("Enter Manager Password: ");
            managerPassword = new String(passwordArray);
        } 
        else 
        {
            // If console is not available (e.g., when running in some IDEs)
            System.out.print("Enter Manager Password: ");
            managerPassword = scanner.nextLine().trim();
        }
    

        if (Manager.authenticate(managerID, managerPassword)) 
        {
            String managerName = Manager.getManagerName(managerID);
            System.out.println("Welcome, " + managerName + "!");
            // will implement manager specific functionality here
            managerMenu(scanner);
        } 
        else 
        {
            System.out.println("Invalid ID or Password. Please try again.");
        }
    }

    private static void employeeMenu(Scanner scanner, String authenticatedEmployeeID) 
    {
        boolean exit = false;
    
        while (!exit) {
            System.out.println("Employee Menu:");
            System.out.println("1. View Schedule");
            System.out.println("2. Exit");
    
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character
    
            switch (choice) 
            {
                case 1:
                    Schedule.viewLoggedInEmployeeSchedule(authenticatedEmployeeID);
                    break;
                case 2:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void managerMenu(Scanner scanner) 
    {
        boolean exit = false;
        boolean shiftsAssigned = false;

        while (!exit) {
            System.out.println("\nManager Menu:");
            System.out.println("1. Auto-Assign Shifts");
            System.out.println("2. View Weekly Schedule");
            System.out.println("3. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) 
            {
                case 1:
                    List<Schedule.Employee> employees = Schedule.parseEmployeeFile("employee.txt");
                    Schedule.autoAssignShifts(employees);
                    System.out.println("Shifts have been auto-assigned.");
                    shiftsAssigned = true;
                    break;
                case 2:
                    if (shiftsAssigned) 
                    {
                        Schedule.viewWeeklySchedule();
                    } 
                    else 
                    {
                        System.out.println("Please auto-assign shifts first.");
                    }
                    break;
                case 3:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void createNewEmployee(Scanner scanner) 
    {
        System.out.println("New Employee Setup");

        String newID = generateUniqueID(scanner);
        
        System.out.print("Enter your role (lead/member): ");
        String role = scanner.nextLine().trim().toLowerCase();

        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        String password = "";  // Define password variable here

        while (true) 
        {
            Console createEmpPassConsole = System.console();
            if (createEmpPassConsole != null) 
            {
                // If console is available (e.g., when running in a terminal)
                char[] createEmpPasswordArray = createEmpPassConsole.readPassword("Create a Password: ");
                password = new String(createEmpPasswordArray);
            } 
            else 
            {
                // If console is not available (e.g., when running in some IDEs)
                System.out.print("Create a Password: ");
                password = scanner.nextLine().trim();
            }

            System.out.print("Confirm Password: ");
            String confirmPassword = scanner.nextLine().trim();

            if (password.equals(confirmPassword)) 
            {
                break;
            } 
            else 
            {
                System.out.println("Passwords do not match. Please try again.");
            }
        }
        
        String schedule = "";
        String schedulePreference = "";
        if (role.equals("lead")) 
        {
            System.out.println("\nAs a Lead, your schedule will be set to full-time.");
            System.out.println("This will be divided out accordingly during the week.");
            String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
            for (String day : daysOfWeek) 
            {
                schedule += day + ": Open\n";
                if (day.equals(daysOfWeek[6])) 
                {
                    schedule += ": Open";
                }
            }
            schedulePreference = "Full-time (40 hours)";
        } 
        else if (role.equals("member")) 
        {
            System.out.println("\nAs a Member, you can customize your schedule.");
            System.out.print("What's your shift preference? (Part time/ full time)? ");
            String shiftPreference = scanner.nextLine().trim();
            
            while (true) 
            {
                if (shiftPreference.equalsIgnoreCase("part time")) 
                {
                    System.out.print("How many hours per week? ");
                    int hours = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character
                    if(hours < 8)
                    {
                        System.out.println("Invalid hours. Please enter at least 8 hours.");
                        return;
                    }
                    else
                    {
                        schedulePreference = "Part-time (" + hours + " hours)";
                        schedule = setCustomSchedule(scanner);
                        break;
                    }
                } 

                else if (shiftPreference.equalsIgnoreCase("full time")) 
                {
                    schedulePreference = "Full-time (40 hours)";
                    schedule = setCustomSchedule(scanner);
                    break;
                } 

                else 
                {
                    System.out.println("Invalid shift preference. Please enter 'part time' or 'full time'.");
                    return; // You may loop back or handle this differently.
                }
                
            }
            if (shiftPreference.equalsIgnoreCase("part time")) 
            {
                System.out.print("How many hours per week? ");
                int hours = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character
                if(hours < 8)
                {
                    System.out.println("Invalid hours. Please enter at least 8 hours.");
                    return;
                }
                else
                {
                    schedulePreference = "Part-time (" + hours + " hours)";
                    schedule = setCustomSchedule(scanner);
                }
            } 

            else if (shiftPreference.equalsIgnoreCase("full time")) 
            {
                schedulePreference = "Full-time (40 hours)";
                schedule = setCustomSchedule(scanner);
            } 

            else 
            {
                System.out.println("Invalid shift preference. Please enter 'part time' or 'full time'.");
                return; // You may loop back or handle this differently.
            }
        } 
        else 
        {
            System.out.println("Invalid role. Please enter 'lead' or 'member'.");
            return; // You may loop back or handle this differently.
        }

        saveEmployeeToFile(newID, role, name, password, schedulePreference, schedule);

        System.out.println("Employee created successfully! You can now log in using your new ID and password.");
    }

    private static String generateUniqueID(Scanner scanner) 
    {
        String newID;
        do 
        {
            System.out.print("Enter a unique 4-digit Employee ID: ");
            newID = scanner.nextLine().trim();
            if (newID.equals("0000")) 
            {
                System.out.println("ID cannot be '0000'. Please choose a different ID.");
            } 
            else if (newID.length() != 4 || !newID.matches("\\d+")) 
            {
                System.out.println("ID must be a 4-digit number.");
            } 
            else if (existingIDs.contains(newID)) 
            {
                System.out.println("This ID is already taken. Please choose a different one.");
            }
        } 
        while (newID.equals("0000") || newID.length() != 4 || !newID.matches("\\d+") || existingIDs.contains(newID));

        existingIDs.add(newID);
        return newID;
    }

    private static String setCustomSchedule(Scanner scanner) 
    {
        StringBuilder schedule = new StringBuilder();
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        
        System.out.println("Enter your availability for each day: \n");
        System.out.println("AM: Morning (8am - 12pm), PM: Afternoon (1pm - 5pm), EV: Evening (6pm - 11pm), Overnight: (12am - 7am), off: Off, Open: Open");
        System.out.println("Example: 'AM', 'PM', 'EV', 'Overnight', 'off', 'Open'");
        System.out.println("If you are available for multiple shifts, separate them by commas. Example: 'AM, PM'");
        System.out.println("If you are available for the full day, enter 'Open'.");
        System.out.println("If you want a custom schedule, enter 'Custom'.");
        
        for (int i = 0; i < daysOfWeek.length; i++) 
        {
            String day = daysOfWeek[i];
            System.out.print(day + ": ");
            String availability = scanner.nextLine().trim();
    
            availability = availability.isEmpty() ? "Off" : availability;

            if (availability.equalsIgnoreCase("custom")) 
            {
                System.out.print("Enter start time (in 24-hour format, e.g., 08:00): ");
                String startTime = scanner.nextLine().trim();
                System.out.print("Enter end time (in 24-hour format, e.g., 16:00): ");
                String endTime = scanner.nextLine().trim();
                
                // Validate that the time difference is at least 4 hours
                if (isValidCustomTime(startTime, endTime)) 
                {
                    availability = "Custom (" + startTime + " - " + endTime + ")";
                } 
                else 
                {
                    System.out.println("Custom time must be at least 4 hours. Please enter again.");
                    i--; // Go back to re-ask for the current day's availability
                    continue;
                }
            }
    
            schedule.append(day).append(": ").append(availability).append("\n");
        }
        
        return schedule.toString().trim();
    }
    
    private static boolean isValidCustomTime(String startTime, String endTime) 
    {
        try 
        {
            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");
            
            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = Integer.parseInt(startParts[1]);
            
            int endHour = Integer.parseInt(endParts[0]);
            int endMinute = Integer.parseInt(endParts[1]);
            
            int startTotalMinutes = startHour * 60 + startMinute;
            int endTotalMinutes = endHour * 60 + endMinute;
            
            return (endTotalMinutes - startTotalMinutes) >= 240; // 240 minutes = 4 hours
        } 
        catch (NumberFormatException | ArrayIndexOutOfBoundsException e) 
        {
            System.out.println("Invalid time format. Please try again.");
            return false;
        }
    }
    
    

    private static void saveEmployeeToFile(String id, String role, String name, String password, String schedulePreference, String schedule) 
    {
        try (FileWriter writer = new FileWriter(EMPLOYEE_FILE, true)) 
        {
            writer.write("Employee ID: " + id + "\n");
            writer.write("Role: " + role + "\n");
            writer.write("Employee Name: " + name + "\n");
            writer.write("Password: " + password + "\n"); // Save the password
            writer.write("Schedule Preference: " + schedulePreference + "\n");
            writer.write("Schedule: \n" + schedule + "\n");
            writer.write("------------------------------\n");
            System.out.println("Employee information saved successfully.");
        } 
        catch (IOException e) 
        {
            System.out.println("An error occurred while saving employee information.");
            e.printStackTrace();
        }
    }

    private static void loadExistingIDs() 
    {
        File file = new File(EMPLOYEE_FILE);
        if (!file.exists()) 
        {
            System.out.println("Employee file does not exist. No existing IDs to load.");
            return;
        }

        try (Scanner fileScanner = new Scanner(file)) 
        {
            while (fileScanner.hasNextLine()) 
            {
                String line = fileScanner.nextLine();
                if (line.startsWith("Employee ID: ")) 
                {
                    String id = line.substring(13).trim();
                    existingIDs.add(id);
                }
            }
        } 
        catch (IOException e) 
        {
            System.out.println("An error occurred while loading existing employee IDs.");
            e.printStackTrace();
        }
    }
}
