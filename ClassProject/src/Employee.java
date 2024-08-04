import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.time.LocalDate;
import java.util.Map;

public class Employee 
{
    private String name;
    private String id;
    private String password;
    private Map<LocalDate, String> availability; // Map to store availability per date
    private int maxHours;

    public Employee(String name, String id, String password) 
    {
        this.name = name;
        this.id = id;
        this.password = password;
    }

    public String getName() 
    {
        return name;
    }

    public String getId() 
    {
        return id;
    }

    public String getPassword() 
    {
        return password;
    }

    private static final String EMPLOYEE_FILE = "employee.txt";

    public boolean isAvailable(LocalDate date) {
        return availability.containsKey(date);
    }
    public int getMaxHours() {
        return maxHours;
    }

    public void setAvailability(Map<LocalDate, String> availability) {
        this.availability = availability;
    }
    public String getShiftEndTime(LocalDate date) {
        if (availability.containsKey(date)) {
            // Assuming availability map stores time in the format "HH:mm-HH:mm"
            return availability.get(date).split("-")[1];
        }
        return "";
    }
    public String getShiftStartTime(LocalDate date) {
        return availability.get(date).split(" - ")[0];
    }


    // Method to authenticate an employee based on ID and password
    public static boolean authenticate(String employeeID, String employeePassword) 
    {
        try (Scanner scanner = new Scanner(new File(EMPLOYEE_FILE))) 
        {
            while (scanner.hasNextLine()) 
            {
                String line = scanner.nextLine();
                String[] parts = line.split(": ");
                if (line.startsWith("Employee ID: ") && parts[1].trim().equals(employeeID)) 
                {
                    scanner.nextLine(); // Skip Role
                    scanner.nextLine(); // Skip Name line
                    String passwordLine = scanner.nextLine(); // Password line
                    String storedPassword = passwordLine.split(": ")[1].trim();
                    if (storedPassword.equals(employeePassword)) 
                    {
                        return true;
                    }
                }
            }
        } 
        catch (FileNotFoundException e) 
        {
            System.out.println("Employee file not found.");
        }
        return false;
    }

    // Method to get the name of an employee based on their ID
    public static String getEmployeeName(String employeeID) 
    {
        try (Scanner scanner = new Scanner(new File(EMPLOYEE_FILE))) 
        {
            while (scanner.hasNextLine()) 
            {
                String line = scanner.nextLine();
                String[] parts = line.split(": ");
                if (line.startsWith("Employee ID: ") && parts[1].trim().equals(employeeID)) 
                {
                    scanner.nextLine(); // Skip Role
                    String nameLine = scanner.nextLine(); // Name line
                    return nameLine.split(": ")[1].trim();
                }
            }
        } 
        catch (FileNotFoundException e) 
        {
            System.out.println("Employee file not found.");
        }
        return null;
    }
}

