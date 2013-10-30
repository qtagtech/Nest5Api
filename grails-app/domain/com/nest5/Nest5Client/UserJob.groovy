package com.nest5.Nest5Client

class UserJob {
    static belongsTo = [user: ExtendedUser]

    String name
    String company
    Date begin
    Date finish
    String description

    static constraints = {
        name()
        company()
        description(nullable: true)
        begin()
        finish()

    }
    static mapping = {
        description type: 'text'
    }
}
