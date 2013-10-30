package com.nest5.Nest5Client

class Store {

    static belongsTo = [company : Company]
    static hasMany = [offers:Offer]
    String name
    BigDecimal latitude = 0
    BigDecimal longitude = 0
    Company company
    String code

    List promos() {
        return offers.collect{it.promo}
    }

    List addToPromo(Promo promo) {
        Offer.link(this, promo)
        return promos()
    }

    List removeFromPromos(Promo promo) {
        Offer.unlink(this, promo)
        return promos()
    }




    static constraints = {
        latitude( scale : 16 )
        longitude( scale : 16 )
    }

    String toString(){
        name
    }
}
