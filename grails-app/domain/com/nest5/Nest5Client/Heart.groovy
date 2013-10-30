package com.nest5.Nest5Client

class Heart {

    static belongsTo = [user: User, event: Event]

    Date date

    static mapping = {
        sort date: "asc"
    }

    String toString(){
        date.toString()
    }

    static constraints = {
    }
}
