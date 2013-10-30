package com.nest5.Nest5Client

class UserEducation {
    static belongsTo = [user: ExtendedUser]

    String name
    String institution
    Date begin
    Date finish
    String description

    static constraints = {
    }
    static mapping = {
        description type: 'text'
    }
}
