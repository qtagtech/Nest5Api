package com.nest5.Nest5Client

class FileCompany {

    static hasMany = [media: MediaCompany]
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
