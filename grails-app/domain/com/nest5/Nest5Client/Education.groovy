package com.nest5.Nest5Client

class Education {
//    static belongsTo = [member:Member]

    String name
    String institution
    Date begin
    Date finish
    Member member
    String description

    static constraints = {
    }
    static mapping = {
        description type: 'text'
    }
}
