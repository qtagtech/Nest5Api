package com.nest5.Nest5Client

class Invitation {

    String type
    User user
    String email
    String uid
    Date date
    Boolean registered
    Boolean opened //when true and not registered send follow back email asking what happened
    Date dateOpened
    Date dateRegistered
    User registeredUser



    static constraints = {
        type(inList: ["facebook","email"])
    }
}
