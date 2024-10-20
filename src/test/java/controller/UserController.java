package controller;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.configuration.ConfigurationException;
import setup.UserModel;

import java.util.Properties;

import static io.restassured.RestAssured.given;

public class UserController {
    Properties prop;
    public UserController(Properties prop){
        this.prop=prop;
    }
    public Response userLogin(UserModel userModel) throws ConfigurationException {
        RestAssured.baseURI= "http://dmoney.roadtocareer.net";
        return given().contentType("application/json").body(userModel).when().post("/user/login");
    }
    public Response searchUser(String userId){
        RestAssured.baseURI= "http://dmoney.roadtocareer.net";
        return given().contentType("application/json")
                .header("Authorization","bearer "+prop.getProperty("token"))
                .when().get("/user/search/id/"+userId);
    }
    public Response createUser(UserModel userModel){
        RestAssured.baseURI= "http://dmoney.roadtocareer.net";
        return given().contentType("application/json").body(userModel)
                .header("Authorization","bearer "+ prop.getProperty("token"))
                .header("X-AUTH-SECRET-KEY",prop.getProperty("secretKey"))
                .when().post("/user/create");
    }
    public Response deleteUser(String userId){
        RestAssured.baseURI= "http://dmoney.roadtocareer.net";
        return given().contentType("application/json")
                .header("Authorization","bearer "+prop.getProperty("token"))
                .header("X-AUTH-SECRET-KEY",prop.getProperty("secretKey"))
                .when().delete("/user/delete/"+userId);
    }
}
