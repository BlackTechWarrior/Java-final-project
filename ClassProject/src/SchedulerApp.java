import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.*;

public class SchedulerApp 
{

    private static final String EMPLOYEE_FILE = "src/employee.txt"; // File to store employee information
    private static Set<String> existingIDs = new HashSet<>();

    public static void main(String[] args) 
{
    loadExistingIDs(); // Load existing IDs from the file
    Scanner scanner = new Scanner(System.in);

    boolean running = true;
    while (running) {
        System.out.println("\nWelcome to the scheduler.");
        System.out.println("1. Login");
        System.out.println("2. Exit");
        System.out.print("Choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        switch (choice) {
            case 1:
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
                        if (employeeID.equals("0000")) {
                            createNewEmployee(scanner);
                        } else if (existingIDs.contains(employeeID)) {
                            employeeID = handleEmployeeLogin(scanner, employeeID);
                        } else {
                            System.out.println("Invalid ID. Please try again.");
                        }
                        break;
                    default:
                        System.out.println("Invalid option. Please choose either 'Manager' or 'Employee'.");
                        break;
                }
                break;
            case 2:
                running = false;
                System.out.println("Thank you for using the scheduler. Goodbye!");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    scanner.close();
}
    public static String handleEmployeeLogin(Scanner scanner, String employeeID) 
    {
        Console empPassConsole = System.console(); // For reading password without echoing characters
        String employeePassword;
        if (empPassConsole != null) 
        {
            // If console is available (e.g., when running in a terminal)
            char[] empPasswordArray = empPassConsole.readPassword("Enter Employee Password: ");
            employeePassword = new String(empPasswordArray); // Convert char password array to string. Used to compare & authenticate
        } 
        else 
        {
            // If console is not available (e.g., when running in some IDEs)
            System.out.print("Enter Employee Password: "); 
            employeePassword = scanner.nextLine().trim();
        }

        if (Employee.authenticate(employeeID, employeePassword)) 
        {
            String employeeName = Employee.getEmployeeName(employeeID); //matches name to ID and prints Welcome, name
            System.out.println("Welcome, " + employeeName + "!"); 
            employeeMenu(scanner, employeeID);  // Employee menu
            
        } 
        else 
        {
            System.out.println("Invalid ID or Password. Please try again.");
            return null;
        }

        return employeeID; // Return the employeeID to be used in the switch case
    }

    
    //this is a repetation of the employee function but with manager instead of employee
    private static void handleManagerLogin(Scanner scanner) 
    {
        System.out.print("Enter Manager ID: ");
        String managerID = scanner.nextLine().trim();

        Console manPassConsole = System.console();

        String managerPassword;
        if (manPassConsole != null) 
        {
            char[] passwordArray = manPassConsole.readPassword("Enter Manager Password: ");
            managerPassword = new String(passwordArray);
        } 
        else 
        {
            System.out.print("Enter Manager Password: ");
            managerPassword = scanner.nextLine().trim();
        }
    

        if (Manager.authenticate(managerID, managerPassword)) 
        {
            String managerName = Manager.getManagerName(managerID);
            System.out.println("Welcome, " + managerName + "!");
            managerMenu(scanner);
        } 
        else 
        {
            System.out.println("Invalid ID or Password. Please try again.");
        }
    }

    private static void employeeMenu(Scanner scanner, String authenticatedEmployeeID) //employee menu
    {
        boolean exit = false;
    
        while (!exit) {
            System.out.println("Employee Menu:");
            System.out.println("1. View Schedule");
            System.out.println("2. Ping My Manager");
            System.out.println("3. View My Ping Responses");
            System.out.println("4. Update My Schedule");
            System.out.println("5. Exit");
    
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character
    
            switch (choice) 
            {
                case 1:
                    Schedule.viewLoggedInEmployeeSchedule(authenticatedEmployeeID); //view your schedule
                    break;
                case 2:
                    pingMyManager(scanner, authenticatedEmployeeID); //ping your manager ie make requests, ask questions, etc
                    break;
                case 3:
                    viewMyPingResponses(scanner, authenticatedEmployeeID); //view responses to your pings
                    break;
                case 4:
                    updateEmployeeSchedule(scanner, authenticatedEmployeeID); //update your schedule
                    break;
                case 5:
                    exit = true; // Exit the program
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void managerMenu(Scanner scanner) 
    {
        boolean exit = false;
        boolean shiftsAssigned = false; // Check if, this is to allow that the shifts are assigned before viewing the schedule

        if ("schedule.txt" != null) // Check if the schedule file is not empty
        {
            shiftsAssigned = true;
        }

        while (!exit) {
            System.out.println("\nManager Menu:");
            System.out.println("1. Auto-Assign Shifts");
            System.out.println("2. View Weekly Schedule");
            System.out.println("3. View Pings");
            System.out.println("4. Respond to Ping");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) 
            {
                case 1:
                    List<Schedule.Employee> employees = Schedule.parseEmployeeFile("src/employee.txt"); // Parsing the employee file
                    Schedule.autoAssignShifts(employees); // Auto-assign shifts based on schedule availability from the parsed employees
                    System.out.println("Shifts have been auto-assigned.");
                    shiftsAssigned = true; // Set to true after shifts are assigned
                    break;
                case 2:
                    if (shiftsAssigned) 
                    {
                        Schedule.viewWeeklySchedule(); // View the weekly schedule
                    }
                    else 
                    {
                        System.out.println("Please auto-assign shifts first."); // If shifts are not assigned, prompt to auto-assign first
                    }
                    break;
                case 3:
                    viewPings(); // View pings from employees
                    break;
                case 4:
                    respondToPing(scanner); // Respond to a ping
                    break;
                case 5:
                    exit = true; // Exit the program
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }



    private static void createNewEmployee(Scanner scanner)  //create a new employee
    {
        System.out.println("New Employee Setup");

        String newID = generateUniqueID(scanner); // Generate a unique 4-digit ID for the new employee
        
        System.out.print("Enter your role (lead/member): "); // Ask for the role
        String role = scanner.nextLine().trim().toLowerCase(); // Convert to lowercase for easier comparison, and trims any leading/trailing whitespaces

        System.out.print("Enter your name: ");  // Ask for the name
        String name = scanner.nextLine().trim();

        String password = ""; // Initialize password

        while (true) 
        {
            Console createEmpPassConsole = System.console(); // For reading password without echoing characters
            if (createEmpPassConsole != null) 
            {
                char[] createEmpPasswordArray = createEmpPassConsole.readPassword("Create a Password: ");
                password = new String(createEmpPasswordArray);
            } 
            else 
            {
                System.out.print("Create a Password: ");
                password = scanner.nextLine().trim();
            }

            System.out.print("Confirm Password: "); // Confirm the password, no need to hide to help the user confirm
            String confirmPassword = scanner.nextLine().trim(); 

            if (password.equals(confirmPassword)) 
            {
                break; // Break out of the loop if the passwords match
            } 
            else 
            {
                System.out.println("Passwords do not match. Please try again.");
            }
        }
        
        String schedule = ""; // Initializing the schedule and schedule preference
        String schedulePreference = "";
        if (role.equals("lead")) 
        {
            System.out.println("\nAs a Lead, your schedule will be set to full-time.");
            System.out.println("This will be divided out accordingly during the week.");
            String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"}; // Days of the week
            for (String day : daysOfWeek)  // Loop through the days of the week
            {
                schedule += day + ": Open\n"; // Setting the schedule to open for each day
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
            System.out.print("What's your shift preference? (Part time/ full time)? "); // Ask for the shift preference
            String shiftPreference = scanner.nextLine().trim();
            
            while (true) 
            {
                if (shiftPreference.equalsIgnoreCase("part time")) 
                {
                    System.out.print("How many hours per week? "); // Ask for the number of hours to work per week
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
                    return; 
                }
                
            }
        }

        else 
        {
            System.out.println("Invalid role. Please enter 'lead' or 'member'.");
            return; // can add loop to re-enter role
        }

        saveEmployeeToFile(newID, role, name, password, schedulePreference, schedule); // Save the employee information to the file

        System.out.println("Employee created successfully! You can now log in using your new ID and password."); // Confirmation message
    }

    private static String generateUniqueID(Scanner scanner)  //generate a unique ID ie is not 0000, is 4 digits, and is not already in use
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

        existingIDs.add(newID); // Add the new ID to the existing IDs set
        return newID; // Return the new ID 
    }

    private static String setCustomSchedule(Scanner scanner) //set a custom schedule for employee Member
    {
        StringBuilder schedule = new StringBuilder();
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        
        //print statements to explain schedule input
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
    
            availability = availability.isEmpty() ? "Off" : availability; // Set to 'Off' if the input is empty

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
    
            schedule.append(day).append(": ").append(availability).append("\n"); // Add the availability to the schedule
        }
        
        return schedule.toString().trim(); // Return the schedule as a string
    }
    
    private static boolean isValidCustomTime(String startTime, String endTime) //validate custom time for employee Member ie at least 4 hours
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
    
    

    private static void saveEmployeeToFile(String id, String role, String name, String password, String schedulePreference, String schedule) //save employee info to file
    {
        try (FileWriter writer = new FileWriter(EMPLOYEE_FILE, true)) // Append to the file
        {
            writer.write("Employee ID: " + id + "\n");
            writer.write("Role: " + role + "\n");
            writer.write("Employee Name: " + name + "\n");
            writer.write("Password: " + password + "\n"); 
            writer.write("Schedule Preference: " + schedulePreference + "\n");
            writer.write("Schedule: \n" + schedule + "\n");
            writer.write("------------------------------\n");
            System.out.println("Employee information saved successfully.");
        } 
        catch (IOException e) // Catch any exceptions that occur during the file writing process
        {
            System.out.println("An error occurred while saving employee information.");
            e.printStackTrace(); // Print the stack trace of the exception
        }
    }

    private static void loadExistingIDs() //load existing IDs from file
    {
        File file = new File(EMPLOYEE_FILE);
        if (!file.exists()) 
        {
            System.out.println("Employee file does not exist. No existing IDs to load.");
            return;
        }

        try (Scanner fileScanner = new Scanner(file)) // Read the file line by line
        {
            while (fileScanner.hasNextLine()) 
            {
                String line = fileScanner.nextLine();
                if (line.startsWith("Employee ID: "))  // Check if the line contains an employee ID
                {
                    String id = line.substring(13).trim(); // Extract the ID from the line
                    existingIDs.add(id); // Add the ID to the existing IDs set
                }
            }
        } 
        catch (IOException e) 
        {
            System.out.println("An error occurred while loading existing employee IDs.");
            e.printStackTrace();
        }
    }

    private static void updateEmployeeSchedule(Scanner scanner, String employeeID) {
        List<String> fileContent = new ArrayList<>();
        boolean employeeFound = false;
        int scheduleStartIndex = -1;
    
        // Read the entire file content
        try (BufferedReader reader = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {
            String line;
            int lineIndex = 0;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
                if (line.equals("Employee ID: " + employeeID)) {
                    employeeFound = true;
                }
                if (employeeFound && line.startsWith("Schedule:")) {
                    scheduleStartIndex = lineIndex + 1;
                    break;
                }
                lineIndex++;
            }
        } catch (IOException e) {
            System.out.println("Error reading employee file: " + e.getMessage());
            return;
        }
    
        if (!employeeFound) {
            System.out.println("Employee not found.");
            return;
        }
    
        // Get the new schedule
        String newSchedule = setCustomSchedule(scanner);
    
        // Update the file content with the new schedule
        int scheduleEndIndex = scheduleStartIndex;
        while (scheduleEndIndex < fileContent.size() && !fileContent.get(scheduleEndIndex).equals("------------------------------")) {
            scheduleEndIndex++;
        }
    
        fileContent.subList(scheduleStartIndex, scheduleEndIndex).clear();
        fileContent.addAll(scheduleStartIndex, Arrays.asList(newSchedule.split("\n")));
    
        // Write the updated content back to the file
        try (FileWriter writer = new FileWriter(EMPLOYEE_FILE)) {
            for (String line : fileContent) {
                writer.write(line + "\n");
            }
            System.out.println("Schedule updated successfully.");
        } catch (IOException e) {
            System.out.println("Error updating schedule: " + e.getMessage());
        }
    }

    private static void pingMyManager(Scanner scanner, String employeeID) //ping manager ie make requests, ask questions, etc
    {
        String employeeName = Employee.getEmployeeName(employeeID); // Get the employee name from the ID to include in the ping
        String pingID = generateUniquePingId(); // Generate a unique ping ID
        System.out.println("\nPing My Manager");
        System.out.println("1. Request Extra Hours");
        System.out.println("2. Request a Shift Swap");
        System.out.println("3. Request a Shift Drop");
        System.out.println("4. Ask a Question");
        System.out.println("5. Schedule a Meeting");
        System.out.print("Choose an option (1-5): ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        String pingMessage = "";
    
        switch (choice) 
        {
            case 1:
                System.out.print("Enter your request for extra hours: ");
                pingMessage = "Request Extra Hours: " + scanner.nextLine();
                break;
            case 2:
                System.out.print("Enter the shift swap request details: ");
                pingMessage = "Request Shift Swap: " + scanner.nextLine();
                break;
            case 3:
                System.out.print("Enter the shift you want to drop: ");
                pingMessage = "Request Shift Drop: " + scanner.nextLine();
                break;
            case 4:
                System.out.print("Enter your question: ");
                pingMessage = "Question: " + scanner.nextLine();
                break;
            case 5:
                System.out.print("Enter the details for scheduling a meeting: ");
                pingMessage = "Schedule Meeting: " + scanner.nextLine();
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }
    
        // Save the ping message to ping.txt
        try (FileWriter writer = new FileWriter("src/ping.txt", true)) 
        {
            writer.write("Ping ID: " + pingID + "\n");
            writer.write("Employee ID: " + employeeID + "\n");
            writer.write("Employee Name: " + employeeName + "\n");
            writer.write(pingMessage + "\n");
            writer.write("Status: Pending\n");
            writer.write("----------------------------------\n");
            System.out.println("Ping sent to the manager. Your Ping ID is:" +pingID);
        } 
        catch (IOException e) 
        {
            System.out.println("Error saving the ping: " + e.getMessage()); // Print the error message if an exception occurs
        }
    }

    private static String generateUniquePingId() {
        return "PING-" + System.currentTimeMillis();
    }

    private static void viewPings() //view pings from employees
    {
        File pingFile = new File("src/ping.txt"); // Check if the ping file exists
    
        if (!pingFile.exists()) 
        {
            System.out.println("No pings available.");
            return;
        }
    
        System.out.println("\nViewing Pings:");
        try (BufferedReader reader = new BufferedReader(new FileReader(pingFile))) // Read the pings from the file
        {
            String line;
            while ((line = reader.readLine()) != null) // Read each line until the end of the file
            {
                System.out.println(line);
            }
        } 
        catch (IOException e) 
        {
            System.out.println("Error reading pings: " + e.getMessage());
        }
    
        // Delete the ping.txt file after viewing. Like snapChat 
        // cant use with the responding to pings... wil implement later
        // if (pingFile.delete()) 
        // {
        //     System.out.println("Pings have been marked as read and deleted."); // Confirmation message
        // } 
        // else 
        // {
        //     System.out.println("Error deleting the ping file.");
        // }
    }

    private static void respondToPing(Scanner scanner) {
        System.out.print("Enter the Ping ID to respond to: ");
        String pingId = scanner.nextLine().trim();
    
        List<String> fileContent = new ArrayList<>();
        boolean pingFound = false;
        int pingStartIndex = -1;
        
        try (BufferedReader reader = new BufferedReader(new FileReader("src/ping.txt"))) {
            String line;
            int lineIndex = 0;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
                if (line.equals("Ping ID: " + pingId)) {
                    pingFound = true;
                    pingStartIndex = lineIndex;
                    // Read and display the ping details
                    System.out.println("\nPing Details:");
                    for (int i = 0; i < 4 && lineIndex + i < fileContent.size(); i++) {
                        System.out.println(fileContent.get(lineIndex + i));
                    }
                    break;
                }
                lineIndex++;
            }
        } catch (IOException e) {
            System.out.println("Error reading pings: " + e.getMessage());
            return;
        }
    
        if (!pingFound) {
            System.out.println("Ping not found.");
            return;
        }
    
        System.out.print("\nEnter your response: ");
        String response = scanner.nextLine();
    
        // Update the ping.txt file with the response
        try (FileWriter writer = new FileWriter("src/ping.txt")) {
            for (int i = 0; i < fileContent.size(); i++) {
                String line = fileContent.get(i);
                writer.write(line + "\n");
                if (i == pingStartIndex) {
                    // Write the original ping content
                    for (int j = 1; j < 5 && i + j < fileContent.size(); j++) {
                        writer.write(fileContent.get(i + j) + "\n");
                    }
                    // Write the response
                    writer.write("Status: Responded\n");
                    writer.write("Manager Response: " + response + "\n");
                    writer.write("----------------------------------\n");
                    i += 5; // Skip the original ping content
                }
            }
            System.out.println("Response saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving the response: " + e.getMessage());
        }
    }

    private static void viewMyPingResponses(Scanner scanner, String employeeID) {
        System.out.println("\nViewing Your Ping Responses:");
        boolean responsesFound = false;
    
        try (BufferedReader reader = new BufferedReader(new FileReader("src/ping.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("Employee ID: " + employeeID)) {
                    responsesFound = true;
                    // Display the ping and its response
                    for (int i = 0; i < 7; i++) {
                        System.out.println(reader.readLine());
                    }
                    System.out.println("----------------------------------");
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading pings: " + e.getMessage());
        }
    
        if (!responsesFound) {
            System.out.println("No responses found for your pings.");
        }
    }
    
}

