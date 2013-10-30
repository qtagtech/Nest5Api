package com.nest5.Nest5Client

class File {

    static hasMany = [media: Media]
    String name
    String tipo
    String ruta
    String description


    static constraints = {
    }
    static mapping = {
        description type: 'text'
    }
}
