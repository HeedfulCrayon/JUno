import java.util.ArrayList;
import java.util.List;

class WhoIsUser {
    private String userName;
    private List<String> modules;

    WhoIsUser(String user){
        userName = user;
        modules = new ArrayList<>();
    }

    String getUserName(){
        return userName;
    }

    List<String> getModules(){
        return modules;
    }

    void addModule(String game){
        modules.add(game);
    }
}
