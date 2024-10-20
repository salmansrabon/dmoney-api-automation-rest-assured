import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.jupiter.api.Test;
import utils.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;

public class MyRestAssured {
    Properties prop;
    public MyRestAssured() throws IOException {
        prop=new Properties();
        FileInputStream fs=new FileInputStream("./src/test/resources/config.properties");
        prop.load(fs);
    }
    @Test
    public void userLogin() throws ConfigurationException {
        RestAssured.baseURI= "http://dmoney.roadtocareer.net";
        Response res= given().contentType("application/json").body("{\n" +
                        "    \"email\":\"admin@roadtocareer.net\",\n" +
                        "    \"password\":\"1234\"\n" +
                        "}").when().post("/user/login")
                .then().assertThat().statusCode(200).extract().response();

        System.out.println(res.asString());
        JsonPath jsonObj=res.jsonPath();
        String token= jsonObj.get("token");
        System.out.println(token);
        Utils.setEnvVar("token",token);
    }
    @Test
    public void searchUser() throws IOException {
        RestAssured.baseURI= "http://dmoney.roadtocareer.net";
        Response res= given().contentType("application/json")
                .header("Authorization","bearer "+prop.getProperty("token"))
                .when().get("/user/search/id/25037")
                .then().assertThat().statusCode(200).extract().response();

        System.out.println(res.asString());
    }
    @Test
    public void createNewUser(){
        RestAssured.baseURI= "http://dmoney.roadtocareer.net";
        Response res= given().contentType("application/json").body("{\n" +
                        "    \"name\":\"Rest assured test user 1\",\n" +
                        "    \"email\":\"restassureduser1@test.com\",\n" +
                        "    \"password\":\"1234\",\n" +
                        "    \"phone_number\":\"01504454444\",\n" +
                        "    \"nid\":\"123456789\",\n" +
                        "    \"role\":\"customer\"\n" +
                        "}")
                .header("Authorization","bearer "+ prop.getProperty("token"))
                .header("X-AUTH-SECRET-KEY",prop.getProperty("secretKey"))
                .when().post("/user/create")
                .then().assertThat().statusCode(201).extract().response();

        System.out.println(res.asString());
    }
    @Test
    public void deleteUser(){
        RestAssured.baseURI= "http://dmoney.roadtocareer.net";
        Response res= given().contentType("application/json")
                .header("Authorization","bearer "+prop.getProperty("token"))
                .header("X-AUTH-SECRET-KEY",prop.getProperty("secretKey"))
                .when().delete("/user/delete/10517")
                .then().assertThat().statusCode(200).extract().response();

        System.out.println(res.asString());
    }
    public void generateToken() throws ConfigurationException {
        RestAssured.baseURI= "https://oauth2.googleapis.com";
        Response res= given().contentType("application/json")
                .body("{\n" +
                        "    \"client_id\": \""+prop.getProperty("client_id")+"\",\n" +
                        "    \"client_secret\": \""+prop.getProperty("client_secret")+"\",\n" +
                        "    \"refresh_token\":\""+prop.getProperty("refresh_token")+"\",\n" +
                        "    \"grant_type\": \"refresh_token\"\n" +
                        "}")
                .when().post("/token")
                .then().assertThat().statusCode(200).extract().response();

        System.out.println(res.asString());
        JsonPath jsonPath=res.jsonPath();
        String access_token= jsonPath.get("access_token");
        Utils.setEnvVar("google_access_token",access_token);
    }
    public String getInboxList(){
        RestAssured.baseURI= "https://gmail.googleapis.com";
        Response res= given().contentType("application/json")
                .header("Authorization","Bearer "+prop.getProperty("google_access_token"))
                .when().get("/gmail/v1/users/me/messages")
                .then().assertThat().statusCode(200).extract().response();

        //System.out.println(res.asString());
        JsonPath jsonPath=res.jsonPath();
        return jsonPath.get("messages[0].id");
    }
    @Test
    public void readEmail() throws ConfigurationException {
        generateToken();
        String messageId=getInboxList();
        RestAssured.baseURI= "https://gmail.googleapis.com";
        Response res= given().contentType("application/json")
                .header("Authorization","Bearer "+prop.getProperty("google_access_token"))
                .when().get("/gmail/v1/users/me/messages/"+messageId)
                .then().assertThat().statusCode(200).extract().response();
        System.out.println(res.asString());

        JsonPath jsonPath=res.jsonPath();
        String mailBody= jsonPath.get("snippet");
        System.out.println(mailBody);
    }

}
