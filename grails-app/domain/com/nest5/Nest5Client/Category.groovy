package com.nest5.Nest5Client

class Category {
    static hasMany = [families: Family]
    String name
    String description
    Icon icon


    static  mapping = {
        sort name: "asc"
        description type: 'text'
        name type: 'text'
    }

    static constraints = {
    }

    String toString(){
        name
    }
}
