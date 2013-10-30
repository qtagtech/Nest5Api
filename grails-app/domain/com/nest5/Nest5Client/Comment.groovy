package com.nest5.Nest5Client

class Comment {
    static belongsTo = [user: User, event: Event]

    Date date
    String content

    static constraints = {
    }

    static mapping = {
        content type: 'text'
        sort date: "desc"
    }

    String toString(){
        content
    }
}
