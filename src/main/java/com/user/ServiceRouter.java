package com.user;

import static spark.Spark.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.ResponseTransformer;
import org.bson.Document;

/**
 * Created by Kamal Dandamudi on 8/25/16.
 */
public class ServiceRouter {
    public ServiceRouter(Service service){

        ResponseTransformer jsonTransformer = new GsonBuilder().setPrettyPrinting().create()::toJson;

        //Responding with a 404 error if user with id sent in request already exists.
        post("/createUser",
                (request, response) -> {Document doc = service.createUser(request.body());
                    if(doc.containsKey("Failure"))response.status(404);
                    return doc;},
                jsonTransformer);

        get("/getAllUsers",
                (request,response) -> service.getAllUsers(),
                jsonTransformer);

        //Responding with a 404 error if user with id sent in request does not exist.
        put("/updateUser",
                (request, response) -> {Document doc = service.updateUser(request.body());
                    if(doc.containsKey("Failure"))response.status(404);
                    return doc;},
                jsonTransformer);

        delete("/deleteUser/:id",
                (request,response) -> service.deleteUser(request.params(":id")),
                jsonTransformer);

        get("/getUser/:id",
                (request,response) -> service.getUser(request.params(":id")),
                jsonTransformer);
    }
}
