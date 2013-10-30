package com.nest5.Nest5Client

class JobPosition {
    static belongsTo = [member:Member]

    String name
    String company
    Date begin
    Date finish

    String description

    static constraints = {
    }
    static mapping = {
        description type: 'text'
    }
}
