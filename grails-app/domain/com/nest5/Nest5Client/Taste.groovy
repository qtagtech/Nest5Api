package com.nest5.Nest5Client

class Taste {
    static belongsTo = [user: User]

    Date date
    String type
    Object object
    Category category
    Family family
    Universe universe

    static constraints = {
        object nullable: true
        category nullable: true
        family nullable: true
        universe nullable: true
        type(inList: ["object","category","family","universe"])
    }
}
