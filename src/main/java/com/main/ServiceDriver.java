package com.main;

import com.user.UserServiceRouter;

/**
 * This class contains the main methods which initiates the Router
 * object to start accepting service requests.
 * @author Kamal Dandamudi on 8/25/16.
 */
public class ServiceDriver {

    static public void main(String...args){
        //Initializing router to listen to service routes defined in UserServiceRouter
        UserServiceRouter.initializeUserServiceRouter();

    }

}
