package com.mongo;

import com.constants.ResponseCodes;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import static com.mongo.MongoServiceProvider.*;
import java.util.List;

/**
 * Created by Kamal Dandamudi on 8/26/16.
 * Class to test the different methods in MongoServiceProvider class
 * independently.
 */
public class MongoServiceProviderTest {

    // String and Document variables to use as parameters or to assert against
    private final static String userJSONStringNew = "{\"id\":\"1630215c-2608-44b9-aad4-9d56d8aafd9f\", \"firstName\":\"Forris\", \"lastName\":\"Reeling\", \"email\":\" " + "Forris_Reeling@gmail.com\", \"address\":{\"street\":\"143 Tyson Valley\", \"city\":\"Dekalb\", \"zip\":\"60115\", \"state\":\"IL\",\"country\":\"US\"},\"dateCreated\":\"2016-03-15T07:02:40.896Z\",\"company\":{\"name\":\"Denesik Group\",\"website\":\"http://jodie.org\"}, \"profilePic\":\"http://lorempixel.com/640/480/people\" }";
    private final static String userJSONStringExists = "{\"id\":\"1630215c-2608-44b9-aad4-9d56d8aafd5d\", \"firstName\":\"Forris\", \"lastName\":\"Reeling\", \"email\":\" " + "Forris_Reeling@gmail.com\", \"address\":{\"street\":\"143 Tyson Valley\", \"city\":\"Dekalb\", \"zip\":\"60115\", \"state\":\"IL\",\"country\":\"US\"},\"dateCreated\":\"2016-03-15T07:02:40.896Z\",\"company\":{\"name\":\"Denesik Group\",\"website\":\"http://jodie.org\"}, \"profilePic\":\"http://lorempixel.com/640/480/people\" }";
    private final static String userJSONStringExists2 = "{\"id\":\"1630215c-2608-44b9-aad4-9d56d8bbfd6f\", \"firstName\":\"Merry\", \"lastName\":\"Pippin\", \"email\":\" " + "Forris_Reeling@gmail.com\", \"address\":{\"street\":\"143 Tyson Valley\", \"city\":\"Dekalb\", \"zip\":\"60115\", \"state\":\"IL\",\"country\":\"US\"},\"dateCreated\":\"2016-03-15T07:02:40.896Z\",\"company\":{\"name\":\"Denesik Group\",\"website\":\"http://jodie.org\"}, \"profilePic\":\"http://lorempixel.com/640/480/people\" }";

    private final static String existingUserID1 = "1630215c-2608-44b9-aad4-9d56d8aafd5d";
    private final static String existingUserID2 = "1630215c-2608-44b9-aad4-9d56d8bbfd6f";
    private final static String newUserID = "12345";

    private final static String updateJSONStringExists = "{\"id\":\"1630215c-2608-44b9-aad4-9d56d8aafd5d\", \"firstName\":\"Forris\", \"lastName\":\"Reeling\" }";
    private final static String updateJSONStringNew = "{\"id\":\"1630215c-2608-44b9-aad4-9d56d8aafddd\", \"firstName\":\"Forris\", \"lastName\":\"Reeling\" }";

    private final static Document oldUserDocument = Document.parse(userJSONStringExists);

    private final static String successfulCreateResponse = "{\"Success\": \"User successfully created!\"}";
    private final static String failureCreaterResponse = "{\"Failure\": \"User with id 1630215c-2608-44b9-aad4-9d56d8aafd5d already exists!\"}";

    private final static Document successfulCreateDocument = Document.parse(successfulCreateResponse);
    private final static Document failedCreateDocument = Document.parse(failureCreaterResponse);

    /**
     * setUp before method to create 2 user documents in collection
     */
    @Before
    public void setUp(){
        //Creating 2 users to later on test for failed create and succesful delete conditions
        getMongoServiceProvider().createUser(userJSONStringExists);
        getMongoServiceProvider().createUser(userJSONStringExists2);
    }

    @Test
    public void createUserSuccessTest() throws Exception {
        Document success = getMongoServiceProvider().createUser(userJSONStringNew);
        assert(success.toString().equals(successfulCreateDocument.toString()));
    }

    @Test
    public void createUserFailureTest() throws Exception {
        Document failure = getMongoServiceProvider().createUser(userJSONStringExists);
        assert(failure.toString().equals(failedCreateDocument.toString()));
    }

    @Test
    public void getAllUserDocumentsSuccessTest() throws Exception {
        List<Document> userDocuments = getMongoServiceProvider().getAllUserDocuments();
        //To check if at least 1 user document is returned. The one created during setUp()
        assert(userDocuments.size()>=1);
        //To check if the first user document is the same one that was created during setUp()
        assert(userDocuments.get(0).toString().equals(oldUserDocument.toString()));
    }

    @Test
    public void updateUserSuccess() throws Exception {
        Document success = getMongoServiceProvider().updateUser(updateJSONStringExists);
        assert(success.toString().contains(ResponseCodes.Success.getCodeString()));
    }

    @Test
    public void updateUserFailure() throws Exception {
        Document failure = getMongoServiceProvider().updateUser(updateJSONStringNew);
        assert(failure.toString().contains(ResponseCodes.Failure.getCodeString()));
    }

    @Test
    public void getUserDocumentSuccess() throws Exception {
        Document success = getMongoServiceProvider().getUserDocument(existingUserID1);
        assert(success.toString().equals(oldUserDocument.toString()));
    }

    @Test
    public void getUserDocumentFailure() throws Exception {
        Document failure = getMongoServiceProvider().getUserDocument(newUserID);
        assert(failure.toString().contains(ResponseCodes.Failure.getCodeString()));
    }

    @Test
    public void deleteUserSuccess() throws Exception {
        Document success = getMongoServiceProvider().deleteUser(existingUserID2);
        assert(success.toString().contains(ResponseCodes.Success.getCodeString()));
    }

    @Test
    public void deleteUserFailure() throws Exception {
        Document failure = getMongoServiceProvider().deleteUser(newUserID);
        System.out.println(failure.toString());
        assert(failure.toString().contains(ResponseCodes.Failure.getCodeString()));
    }

}