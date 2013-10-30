package com.nest5.Nest5Client

class City {
    static belongsTo = [country: Country]
    String name
    String code
    BigDecimal latitude = 0
    BigDecimal longitude = 0


    static constraints = {
        latitude( scale : 16 )
        longitude( scale : 16 )
    }
}
