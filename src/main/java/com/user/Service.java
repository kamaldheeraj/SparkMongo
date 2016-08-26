package com.user;

import static com.user.MongoServiceProvider.getMongoServiceProvider;
import org.bson.Document;
import java.util.List;

/**
 * Created by Kamal Dandamudi on 8/25/16.
 */
public class Service {

    public Document createUser(String userJSONString){
        return getMongoServiceProvider().createUser(userJSONString);
    }

    public List<Document> getAllUsers(){
        return getMongoServiceProvider().getAllUserDocuments();
    }

    public Document updateUser(String userJSONString){
        return getMongoServiceProvider().updateUser(userJSONString);
    }

    public Document deleteUser(String id){
        return getMongoServiceProvider().deleteUser(id);
    }

    public Document getUser(String id){
        return getMongoServiceProvider().getUserDocument(id);
    }
}
