package com.nest5.Nest5Client

class Media {
    static belongsTo = [user: User, file: File]
    Boolean isMain
    static constraints = {
    }
}
