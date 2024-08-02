class Employee {
    private String name;
    private int ID;
    private String password;

    public Employee(int ID, String password, String name) {
        this.password = password;
        this.ID = ID;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }



    public boolean authenticate(int inputID, String inputPassword) {
        return inputID == ID && password.equals(inputPassword);
    }
}
