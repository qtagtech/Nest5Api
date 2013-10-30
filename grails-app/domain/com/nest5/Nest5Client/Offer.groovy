package com.nest5.Nest5Client

class Offer {
    static belongsTo = [store:Store, promo: Promo]

    Store store
    Promo promo
    Date createdAt
    Date ValidThru
    String validDays = "1|2|3|4|5|6|7"
    int validHourMin = 0
    int validHourMax = 24
    String timeZone = "America/Bogota"

    static Offer link(store, promo) {
        def m = Offer.findByStoreAndPromo(store, promo)
        if (!m)
        {
            m = new Offer()
            def date = new Date()
            m.createdAt = date
            m.ValidThru = date + 60
            store?.addToOffers(m)
            promo?.addToOffers(m)


            if(!m.save())
            {
                println m.errors.allErrors
            }


        }
        return m
    }
    List addToUser(User user) {
        Stamp.link(this,user)
        return users()
    }
    List removeFromUser(User user) {
        Stamp.unlink(this,user)
        return users()
    }
    List users() {
        return this.promo.stamps.collect{it.user}
    }

    static void unlink(store, promo) {
        def m = Offer.findByStoreAndPromo(store, promo)
        if (m)
        {
            store?.removeFromOffers(m)
            promo?.removeFromOffers(m)
            m.delete()
        }
    }

    static constraints = {
    }
}
