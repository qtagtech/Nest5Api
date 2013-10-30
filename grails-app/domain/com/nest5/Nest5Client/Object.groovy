package com.nest5.Nest5Client

class Object {
    static hasMany = [categories:  Category]
    String name
    String description
    Icon icon

    static constraints = {
    }

    String toString(){
        name
    }
}
