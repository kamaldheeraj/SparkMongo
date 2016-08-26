package com.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import java.util.*;
import com.constants.ResponseCodes;

/**
 * Singleton class that implements the various methods that are required by
 * the UserServiceRouter. This class implements the following methods:
 * <i>getMongoServiceProvider</i>, <i>getAllUserDocuments</i>, <i>createUser</i>,
 * <i>updateUser</i>, <i>getUserDocument</i> and <i>deleteUser</i>.
 * @author Kamal Dandamudi on 8/25/16.
 */

public final class MongoServiceProvider {

    // Singleton pattern to make sure only one instance of MongoServiceProvider is present at all times.
    private static MongoServiceProvider mongoServiceProvider;

    // MongoClient is Thread-safe so one instance of the client will be enough for the whole application.
    // Not closing MongoClient connection anywhere because the same instance is being used throughout.
    private MongoClient mongo;

    // Static Final variables for db connection details, db name and name of users collection.
    private static String dbName;
    private static String dbURL;
    private static int dbPort;
    private static String usersCollectionName;

    //Static block to initialize static final variables with properties from config file.
    static{
            ResourceBundle properties = ResourceBundle.getBundle("config");
            dbName = (String)properties.getObject("dbName");
            dbURL = (String)properties.getObject("dbURL");
            dbPort = Integer.valueOf((String)properties.getObject("dbPort"));
            usersCollectionName = (String)properties.getObject("usersCollectionName");
    }


    /**
     * Private constructor to construct MongoServiceProvider and initialize
     * the mongo client connection. This constructor is called from the static method
     * getMongoServiceProvider.
     */
    private MongoServiceProvider(){
        this.mongo = new MongoClient(dbURL, dbPort);
    }

    /**
     * Creates the singleton instance of the MongoServiceProvider class and returns it.
     * @return singleton instance of the class.
     */
    public static MongoServiceProvider getMongoServiceProvider() {
        //Lazy instantiation of singleton instance
        if (mongoServiceProvider == null) mongoServiceProvider = new MongoServiceProvider();
        return mongoServiceProvider;
    }

    /**
     * Fetches the list of all Documents in the users collection of the database.
     * @return List of Document objects each representing a user.
     */
    public List<Document> getAllUserDocuments() {
        List<Document> documentList = new ArrayList<>();
        FindIterable<Document> iter = mongo.getDatabase(dbName).
                                        getCollection(usersCollectionName).find().
                                        projection(Projections.excludeId());
        for (Document document : iter) {
            documentList.add(document);
        }
        return documentList;
    }

    /**
     * Creates a new user document in the users collection if one does not
     * already exist with the user's id passed in the JSON string. Uses
     * private methods checkUserExistence to check if used with id exists.
     * @param userJSONString JSON string representing the information
     *                       of the user to be created.
     * @return a Document with a Success code if user has been created
     *          or a Failure code if user already exists.
     */
    public Document createUser(String userJSONString) {
        Document userDocument = Document.parse(userJSONString);
        String userID = (String) userDocument.get("id");
        //Using try-with-resources to close client on failure
            Optional<Document> userOptionalDocument = checkUserExistence(userID);
            if (!userOptionalDocument.isPresent()) {
                mongo.getDatabase(dbName).getCollection(usersCollectionName).insertOne(userDocument);
                return new Document(ResponseCodes.Success.getCodeString(), "User successfully created!");
            }
            else return new Document(ResponseCodes.Failure.getCodeString(), "User with id " + userID + " already exists!");
    }

    /**
     * Fetches the user document in the users collection by matching
     * the passed in user's id. If the user exists, updates the details
     * passed in the JSON string. Uses private methods checkUserExistence
     * to check if used with id exists.
     * @param userJSONString JSON string representing the information
     *                       of the user to be updated.
     * @return a Document with a Success code if user has been found
     *          and updated or a Failure code if user does not exist.
     */
    public Document updateUser(String userJSONString) {
        Document updatedDocument = Document.parse(userJSONString);
        String userID = (String) updatedDocument.get("id");
            Optional<Document> userOptionalDocument = checkUserExistence(userID);
            if (userOptionalDocument.isPresent()) {
                Document oldDoc = userOptionalDocument.get();
                mongo.getDatabase(dbName).
                        getCollection(usersCollectionName).updateOne(oldDoc,new Document("$set",updatedDocument));
                return new Document(ResponseCodes.Success.getCodeString(), "User successfully updated!");
            }
            else return new Document(ResponseCodes.Failure.getCodeString(), "User with id " + userID + " does not exists!");
    }

    /**
     * Fetches the user document in the users collection of the database corresponding
     * to the passed userID parameter. Uses private methods checkUserExistence
     * to check if used with id exists.
     * @param userID String representing the id of the user to be fetched.
     * @return a Document with user information if user exists or a Documents with a
     *          Failure code.
     */
    public Document getUserDocument(String userID) {
            Optional<Document> userOptionalDocument = checkUserExistence(userID);
            if (userOptionalDocument.isPresent()) {
                return userOptionalDocument.get();
            }
            else return new Document(ResponseCodes.Failure.getCodeString(), "User with id " + userID + " does not exists!");
    }

    /**
     * Deletes the matching user documents from the users collection. Uses private methods checkUserExistence
     * to check if used with id exists.
     * @param userID String representing the id of the user to be deleted.
     * @return a Document with a Success code if user has been found
     *          and updated or a Failure code if user does not exist.
     */
    public Document deleteUser(String userID){
            Optional<Document> userOptionalDocument = checkUserExistence(userID);
            if (userOptionalDocument.isPresent()) {
                mongo.getDatabase(dbName).getCollection(usersCollectionName).deleteOne(userOptionalDocument.get());
                return new Document(ResponseCodes.Success.getCodeString(), "User with id " + userID + " deleted from Mongo!");
            }
            else return new Document(ResponseCodes.Failure.getCodeString(), "User with id " + userID + " does not exists!");

    }

    /**
     * Internal method to check if a user with the passed in userID exists in
     * the users collection of the database.
     * @param userID String representing the id of the user to be checked for.
     * @return Optional user document as the user may or may not exist.
     *         Would be empty if user does not exist or will contain the document
     *         corresponding to the user.
     */
    private Optional<Document> checkUserExistence(String userID){
        MongoCursor<Document> iter = mongo.getDatabase(dbName).
                                        getCollection(usersCollectionName).find(new Document("id", userID)).
                                        limit(1).projection(Projections.excludeId()).iterator();
        if (iter.hasNext()) {
            return Optional.of(iter.next());
        }
        else return Optional.empty();
    }

}
