import java.io.*;
import java.util.*;

//similar to the Employee class, the Manager class has a constructor, getters, and methods to authenticate and get the name of the manager.

public class Manager 
{
    private String name;
    private String id;
    private String password;

    public Manager(String name, String id, String password) 
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

    private static final String MANAGER_FILE = "managers.txt";

    public static boolean authenticate(String managerID, String managerPassword) 
    {
        try (Scanner scanner = new Scanner(new File(MANAGER_FILE))) 
        {
            while (scanner.hasNextLine()) 
            {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 3) 
                {
                    String id = parts[0].trim();
                    String password = parts[1].trim();
                    parts[2].trim();
                    if (id.equals(managerID) && password.equals(managerPassword)) 
                    {
                        return true;
                    }
                }
            }
        } 
        catch (FileNotFoundException e) 
        {
            System.out.println("Manager file not found.");
        }
        return false;
    }

    public static String getManagerName(String managerID) 
    {
        try (Scanner scanner = new Scanner(new File(MANAGER_FILE))) 
        {
            while (scanner.hasNextLine()) 
            {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 3) 
                {
                    String id = parts[0].trim();
                    String name = parts[2].trim();
                    if (id.equals(managerID)) 
                    {
                        return name;
                    }
                }
            }
        } 
        catch (FileNotFoundException e) 
        {
            System.out.println("Manager file not found.");
        }
        return null;
    }

    
}

