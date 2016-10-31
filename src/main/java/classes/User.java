package classes;

/**
 * Created by Никита on 25.10.2016.
 */
public class User {
    private static String login;
    private static String password;
    private static String email;
    private static String name;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }
public User () {};

    public User(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
    public String getName() {
        return name;
    }

    public void setLogin(String login) {
        this.login = login;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int hashCode(){
        return this.login.hashCode();
    }




}
