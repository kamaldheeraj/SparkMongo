package com.user;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Projections;
import org.bson.Document;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by Kamal Dandamudi on 8/25/16.
 */

public final class MongoServiceProvider {

    // Singleton pattern to make sure only one instance of MongoServiceProvider is present at all times.

    private static MongoServiceProvider mongoServiceProvider;

    public static MongoServiceProvider getMongoServiceProvider() {
        if (mongoServiceProvider == null) mongoServiceProvider = new MongoServiceProvider();
        return mongoServiceProvider;
    }

    public List<Document> getAllUserDocuments() {
        List<Document> documentList = new ArrayList<>();
        MongoClient mongo = new MongoClient("localhost", 27017);
        FindIterable<Document> iter = mongo.getDatabase("egen-be-challenge").
                                        getCollection("users").find().
                                        projection(Projections.excludeId());
        for (Document document : iter) {
            documentList.add(document);
        }
        return documentList;
    }

    public Document createUser(String userJSONString) {
        Document userDocument = Document.parse(userJSONString);
        String userID = (String) userDocument.get("id");
        //Using try-with-resources to close client on failure
        try (MongoClient mongo = new MongoClient("localhost", 27017)) {
            Optional<Document> userOptionalDocument = checkUserExistense(userID,mongo);
            if (!userOptionalDocument.isPresent()) {
                mongo.getDatabase("egen-be-challenge").getCollection("users").insertOne(userDocument);
                return new Document("Success", "User succesfully created!");
            }
            else return new Document("Failure", "User with id " + userID + " already exists!");
        }
    }

    public Document updateUser(String userJSONString) {
        Document updatedDocument = Document.parse(userJSONString);
        String userID = (String) updatedDocument.get("id");
        //Using try-with-resources to close client on failure
        try (MongoClient mongo = new MongoClient("localhost", 27017)) {
            Optional<Document> userOptionalDocument = checkUserExistense(userID,mongo);
            if (userOptionalDocument.isPresent()) {
                Document oldDoc = userOptionalDocument.get();
                mongo.getDatabase("egen-be-challenge").
                        getCollection("users").updateOne(oldDoc,new Document("$set",updatedDocument));
                return new Document("Success", "User succesfully updated!");
            }
            else return new Document("Failure", "User with id " + userID + " does not exists!");
        }
    }

    // Using an Optional return type because a document with the userID may or may not exist.
    public Document getUserDocument(String userID) {
        try (MongoClient mongo = new MongoClient("localhost", 27017)) {
            Optional<Document> userOptionalDocument = checkUserExistense(userID,mongo);
            if (userOptionalDocument.isPresent()) {
                return userOptionalDocument.get();
            } else {
                return new Document("Failure", "User with id " + userID + " does not exists!");
            }
        }
    }

    public Document deleteUser(String userID){
        try (MongoClient mongo = new MongoClient("localhost", 27017)) {
            Optional<Document> userOptionalDocument = checkUserExistense(userID,mongo);
            if (userOptionalDocument.isPresent()) {
                mongo.getDatabase("egen-be-challenge").getCollection("users").deleteOne(userOptionalDocument.get());
                return new Document("Success", "User with id " + userID + " deleted from Mongo!");
            } else {
                return new Document("Failure", "User with id " + userID + " does not exists!");
            }
        }
    }

    private Optional<Document> checkUserExistense(String userID, MongoClient mongo){
        MongoCursor<Document> iter = mongo.getDatabase("egen-be-challenge").getCollection("users").find(new Document("id", userID)).limit(1).projection(Projections.excludeId()).iterator();
        if (iter.hasNext()) {
            return Optional.of(iter.next());
        } else {
            return Optional.empty();
        }
    }


}
