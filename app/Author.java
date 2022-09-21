/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

/**
 *
 * @author samac
 */
public class Author {
    private String name;
    private int id;
    private int permissions;
    private String dateOfBirth;
    private String email;
    private String password = "";

    public Author(String name, int id, int permissions) {
        this.name = name;
        this.id = id;
        this.permissions = permissions;
    }

    public Author(String name, int id, int permissions, String psw) {
        this.name = name;
        this.id = id;
        this.permissions = permissions;
        this.password = psw;
    }
    
    public String getPassword(){
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getPermissions() {
        return permissions;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEmail() {
        return email;
    }
    
}
