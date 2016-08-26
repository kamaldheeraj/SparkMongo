package com.main;

import com.user.UserServiceRouter;


/**
 * Created by Kamal Dandamudi on 8/25/16.
 * This class contains the main methods which initiates the Router
 * object to start accepting service requests.
 */
public class ServiceDriver {
    static public void main(String...args){
        //Initializing router to listen to service routes defined in UserServiceRouter
        UserServiceRouter.initializeUserServiceRouter();

    }
}
