package testrunner;

import com.github.javafaker.Faker;
import controller.UserController;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.configuration.ConfigurationException;
import org.testng.Assert;
import org.testng.annotations.Test;
import setup.Setup;
import setup.UserModel;
import utils.Utils;

import static io.restassured.RestAssured.given;

public class TestRunner extends Setup {
    @Test(priority = 1, description = "User Login")
    public void doLogin() throws ConfigurationException, InterruptedException {
        UserController userController=new UserController(prop);
        UserModel model=new UserModel();
        model.setEmail("admin@roadtocareer.net");
        model.setPassword("1234");
        Response res= userController.userLogin(model);
        System.out.println(res.asString());

        JsonPath jsonObj=res.jsonPath();
        String token= jsonObj.get("token");
        System.out.println(token);
        Utils.setEnvVar("token",token);
        Thread.sleep(3000);
    }
    @Test(priority = 2, description = "Create new user")
    public void createUser() throws ConfigurationException {
        UserController userController=new UserController(prop);
        UserModel model=new UserModel();
        Faker faker=new Faker();
        model.setName("Rest Assured "+ faker.name().firstName());
        model.setEmail(faker.internet().emailAddress().toLowerCase());
        model.setPassword("1234");
        model.setPhone_number("0150"+ Utils.generateRandomId(1000000,9999999));
        model.setNid("123456789");
        model.setRole("Customer");
        Response res= userController.createUser(model);
        System.out.println(res.asString());

        JsonPath jsonPath=res.jsonPath();
        String message=jsonPath.get("message");
        Assert.assertTrue(message.contains("User created"));

        int userId= jsonPath.get("user.id");
        Utils.setEnvVar("userId",String.valueOf(userId));
    }
    @Test(priority = 3, description = "Search user by id")
    public void searchUser(){
        UserController userController=new UserController(prop);
        Response res= userController.searchUser(prop.getProperty("userId"));
        System.out.println(res.asString());

        JsonPath jsonPath=res.jsonPath();
        int id= jsonPath.get("user.id");
        String email= jsonPath.get("user.email");
        System.out.println(id+" "+email);
    }

    @Test(priority = 4, description = "Delete user")
    public void deleteUser(){
        UserController userController=new UserController(prop);
        Response res= userController.deleteUser(prop.getProperty("userId"));
        System.out.println(res.asString());
    }
}
