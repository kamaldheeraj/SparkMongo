package com.user;

import spark.Spark;
import com.google.gson.GsonBuilder;
import spark.ResponseTransformer;
import org.bson.Document;
import static com.mongo.MongoServiceProvider.getMongoServiceProvider;
import com.constants.ResponseCodes;

/**
 * Created by Kamal Dandamudi on 8/25/16.
 * Class to define the various routes the application can support.
 * Routes supported include <i>createUser</i>, <i>getAllUsers</i>,
 * <i>getUser</i>, <i>updateUser</i> and <i>deleteUser</i>.
 * <p>Each route makes use of the corresponding method from the
 * singleton instance of <i>MongoServiceProvider</i> class to update
 * the documents in mongodb corresponding to users</p>
 */
public class UserServiceRouter {

    //Singleton instance class
    private static UserServiceRouter userServiceRouter;
    /**
     * Constructor that adds the different routes this service router can respond to.
     */
    private UserServiceRouter(){

        // Response Transformer implementation using GSON Builder to enable pretty printing
        // of the received response.
        ResponseTransformer jsonTransformer = new GsonBuilder().setPrettyPrinting().create()::toJson;

        /*
         * Route for createUser using POST.
         * Request body should be a JSON with User Information.
         * Response could have a Status 200 code if user is created correctly with a Success JSON
         * or
         * could have a Status 404 code if a User with id already exists with a Failure JSON.
        */
        Spark.post("/createUser",
                (request, response) -> {Document doc = getMongoServiceProvider().createUser(request.body());
                    // Responding with a 404 error if user with id sent in request already exists.
                    if(doc.containsKey(ResponseCodes.Failure.getCodeString())) response.status(404);
                    return doc;},
                jsonTransformer);

        /*
         * Route for getAllUsers using GET.
         * No Request body or parameters required
         * Response with be a Status 200 with an array of JSON objects containing all Users.
        */
        Spark.get("/getAllUsers",
                (request,response) -> getMongoServiceProvider().getAllUserDocuments(),
                jsonTransformer);

        /*
         * Route for updateUser using PUT.
         * Request body should be a JSON with User fields that have to be updated.
         * Response will have a Status 200 code with a Success JSON if user is created correctly
         * or
         * will have a Status 404 code with a Failure JSON Response if a User with id does not exist.
        */
        Spark.put("/updateUser",
                (request, response) -> {Document doc = getMongoServiceProvider().updateUser(request.body());
                    // Responding with a 404 error if user with id sent in request does not exist.
                    if(doc.containsKey(ResponseCodes.Failure.getCodeString()))response.status(404);
                    return doc;},
                jsonTransformer);

        /*
         * Route for deleteUser using DELETE.
         * Request parameter in URL should include the id of the user to be deleted.
         * Response body will be a Success JSON if user exists and is deleted
         * or
         * will be a Failure JSON if user with id does not exist.
        */
        Spark.delete("/deleteUser/:id",
                (request,response) -> getMongoServiceProvider().deleteUser(request.params(":id")),
                jsonTransformer);

        /*
         * Route for getUser using GET.
         * Request parameter in URL should include the id of the user to be fetched.
         * Response body will be a JSON with User information if user exists
         * or
         * will be a Failure JSON if user with id does not exist.
        */
        Spark.get("/getUser/:id",
                (request,response) -> getMongoServiceProvider().getUserDocument(request.params(":id")),
                jsonTransformer);
    }

    public static void initializeUserServiceRouter(){
        if(userServiceRouter==null){
            userServiceRouter = new UserServiceRouter();
        }
    }
}
