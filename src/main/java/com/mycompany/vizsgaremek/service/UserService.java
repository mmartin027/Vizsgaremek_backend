package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.config.JWT;
import com.mycompany.vizsgaremek.model.Role;
import com.mycompany.vizsgaremek.model.User;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author schzs
 */
public class UserService {

    private User layer = new User();
    private static final Pattern EMAIL_PATTERN
            = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Legalább 8 karakter, tartalmaz nagybetűt, számot és speciális karaktert
    private static final Pattern PASSWORD_PATTERN
            = Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");

    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public JSONObject getUserById(Integer id) {
        JSONObject toReturn = new JSONObject();
        String status = "success";
        Integer statusCode = 200;

        if (id > 0) {
            User modelResult = new User(id);

            JSONObject result = new JSONObject();
            result.put("id", modelResult.getId());
            result.put("firstName", modelResult.getFirstName());
            result.put("lastName", modelResult.getLastName());
            result.put("email", modelResult.getEmail());
            result.put("phone", modelResult.getPhone());
            toReturn.put("result", result);
        } else {
            status = "InvalidParamValue";
            statusCode = 417;
        }

        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }

    public JSONObject registerUser(User registeredUser) {
        JSONObject toReturn = new JSONObject();
        String status = "success";
        Integer statusCode = 200;

        if (isValidEmail(registeredUser.getEmail()) == false) {
            status = "InvalidEmail";
            statusCode = 417;
        } else if (isValidPassword(registeredUser.getPassword()) == false) {
            status = "InvalidPassword";
            statusCode = 417;
        } else {
            Boolean modelResult = User.registerUser(registeredUser);
            if (modelResult == false) {
                status = "ServerError";
                statusCode = 500;
            }
            toReturn.put("result", modelResult);
        }

        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }

    public JSONObject updateUser(User updatedUser) {
        JSONObject toReturn = new JSONObject();
        String status = "success";
        Integer statusCode = 200;

        User userToUpdate = new User(updatedUser.getId());

        if (userToUpdate.getId() == null) {
            status = "InvalidUser";
            statusCode = 417;
        } else {
            if (isValidEmail(updatedUser.getEmail()) == false) {
                status = "InvalidEmail";
                statusCode = 417;
            } else {
                Boolean modelResult = User.updateUser(updatedUser);
                if (modelResult == false) {
                    status = "ServerError";
                    statusCode = 500;
                }
                toReturn.put("result", modelResult);
            }
        }
        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }
    
    public JSONObject getAllUser() {
        JSONObject toReturn = new JSONObject();
        String status = "success";
        Integer statusCode = 200;

        ArrayList<User> modelResult = User.getAllUser();
        
        if(modelResult == null){
            statusCode = 500;
            status = "ModelException";
        } else if(modelResult.isEmpty()){
            status = "NoRecordFound";
        } else{
            JSONArray result = new JSONArray();
            
            for(User actualUser : modelResult){
                JSONObject actualUserObject = new JSONObject();
                
                actualUserObject.put("id", actualUser.getId());
                actualUserObject.put("firstName", actualUser.getFirstName());
                actualUserObject.put("lastName", actualUser.getLastName());
                actualUserObject.put("img", actualUser.getImg());
                actualUserObject.put("email", actualUser.getEmail());
                actualUserObject.put("phone", actualUser.getPhone());
                actualUserObject.put("guid", actualUser.getGuid());
                actualUserObject.put("createdAt", actualUser.getCreatedAt().toString());
                actualUserObject.put("lastLogin", actualUser.getLastLogin() == null ? "" : actualUser.getLastLogin().toString());
                actualUserObject.put("registerFinishedAt", actualUser.getRegisterFinishedAt().toString());
                
                result.put(actualUserObject);
            }
            
            toReturn.put("result", result);
        }
        
        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }
    
    public JSONObject login(User u) {
        JSONObject toReturn = new JSONObject();
        String status = "success";
        Integer statusCode = 200;

        User modelResult = layer.login(u);
        
        if(modelResult == null){
            statusCode = 500;
            status = "ModelException";
        } else if(modelResult.getId() == null){
            status = "NoRecordFound";
        } else{
            JSONObject result = new JSONObject();
            result.put("id", modelResult.getId());
            result.put("firstName", modelResult.getFirstName());
            result.put("lastName", modelResult.getLastName());
            result.put("img", modelResult.getImg());
            result.put("phone", modelResult.getPhone());
            result.put("lastLogin", modelResult.getLastLogin() == null ? "" : modelResult.getLastLogin().toString());
            
            JSONArray roles = new JSONArray();
            for(Role r : modelResult.getRoles()){
                JSONObject role = new JSONObject();
                role.put("id", r.getId());
                role.put("name", r.getName());
                
                roles.put(role);
            }
            
            result.put("roles", roles);
            result.put("jwt", JWT.createJWT(modelResult));
            toReturn.put("result", result);
            
            
        }
        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }
}
