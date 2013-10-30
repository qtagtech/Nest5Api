package com.nest5.Nest5Client

class Country {
    static hasMany = [cities: City]
    String name
    String code
    String locale


    static constraints = {
    }
}
