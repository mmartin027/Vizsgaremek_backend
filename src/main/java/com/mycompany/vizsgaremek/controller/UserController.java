/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.config.JWT;
import com.mycompany.vizsgaremek.model.User;
import com.mycompany.vizsgarmek.config.JWT;
import com.mycompany.vizsgarmek.model.User;
import com.mycompany.vizsgarmek.service.UserService;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

@Path("user")
public class UserController {
    @Context
    private UriInfo context;
    
    private UserService layer = new UserService();

    public UserController() {
    }

    /**
     * Retrieves representation of an instance of com.mycompany.gyakorlas.controller.UserController
     * @return an instance of java.lang.String
     */
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of UserController
     * @param content representation for the resource
     */
    
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }
    
    @GET
    @Path("getUserById")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUserById(@QueryParam("id") Integer id){
        JSONObject toReturn = layer.getUserById(id);
        
        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString())).entity(toReturn.toString()).type(MediaType.APPLICATION_JSON).build();
    }
    
    @POST
    @Path("registerUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(String body){
        JSONObject bodyObject = new JSONObject(body);
        
        User registeredUser = new User(
                bodyObject.getString("firstName"),
                bodyObject.getString("lastName"),
                bodyObject.getString("email"),
                bodyObject.getString("password"),
                bodyObject.getString("phone")
        );
        
        JSONObject toReturn = layer.registerUser(registeredUser);
        
        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString())).entity(toReturn.toString()).type(MediaType.APPLICATION_JSON).build();
    }
    
    @PUT
    @Path("updateUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(String body, @QueryParam("userId") Integer userId){
        JSONObject bodyObject = new JSONObject(body);
        
        User updatedUser = new User(
                userId,
                bodyObject.getString("firstName"),
                bodyObject.getString("lastName"),
                bodyObject.getString("email"),
                bodyObject.getString("phone")
        );
        
        JSONObject toReturn = layer.updateUser(updatedUser);
        
        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString())).entity(toReturn.toString()).type(MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Path("getAllUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllUser(@HeaderParam("token") String jwToken){
        Boolean validJwt = JWT.validateJwt(jwToken);
        
        if(validJwt == null) {
            
            return Response.status(401).entity("tokenExpired").type(MediaType.APPLICATION_JSON).build();

        } else if (validJwt == false){
            
            return Response.status(401).entity("invalidToken").type(MediaType.APPLICATION_JSON).build();

        } else{
            
        JSONObject toReturn = layer.getAllUser();
        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString())).entity(toReturn.toString()).type(MediaType.APPLICATION_JSON).build();

        }
    }
    
    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(String body){
        JSONObject bodyObject = new JSONObject(body);
        
        User user = new User(
                bodyObject.getString("email"),
                bodyObject.getString("password")
        );
        
        JSONObject toReturn = layer.login(user);
        
        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString())).entity(toReturn.toString()).type(MediaType.APPLICATION_JSON).build();
    }
}
