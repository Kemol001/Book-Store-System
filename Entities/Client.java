package Entities;

public class Client {
    private String name;
    private String uName;
    private String password;

    public Client(String name,String uName,String password){
        this.name = name;
        this.uName = uName;
        this.password = password;
    }
    
    public String getName(){return this.name;}
    public String getuName(){return this.uName;}
    public String getPassword(){return this.password;}
}
