package com.nest5.Nest5Client

class Promo {

    static belongsTo = [company : Company]
    static hasMany = [offers:Offer,stamps:Stamp,coupons: Coupon]
    static mappedBy = [coupons: "promo"]
    String activity
    int cantArt
    String article
    String wins
    String code = ""
    Date createdAt   //Agregar esta columna en base de datos bd db OJO
    String description = ""
    Date validThru
    String couponValidity




    List stores() {
        return offers.collect{it.store}
    }
    List users() {
        return stamps.collect{it.user}
    }

    List addToStore(Store store) {
        Offer.link(store, this)
        return stores()
    }


    List removeFromStore(Store store) {
        Offer.unlink(store, this)
        return stores()
    }


    static constraints = {
        activity(inList:[ "Compra", "Visita"])
        cantArt(nullable:false)
        article(nullable:false, maxLength:50)
        wins(nullable:false, maxLength:50)


    }
    static  mapping = {

        sort createdAt: "desc"
        description type: 'text'

    }

    String toString(){
        def action = "Veces"
        if (activity != "Visita")
        {
            action = ""
        }
        else{
            if (cantArt ==  1)
            {
                action = "Vez"
            }
        }
        activity + " " + cantArt + " "+ action +" " + article + " y obtén con Nest5: "+ wins
    }


}
