package com.nest5.Nest5Client

class Stamp {

    static belongsTo = [promo:Promo, user: User]

    Promo promo
    User user
    Date date
    boolean redeemed = false
    Store store

    static Stamp link(offer,user) {
        //def m = Stamp.findByPromoAndUser(promo, user)


           def m = new Stamp()
            user?.addToStamps(m)
            offer?.promo?.addToStamps m
        def date = new Date()
        m.store = offer.store
            m.date = date


            if(!m.save())
            {
                println m.errors.allErrors
            }
        else
            {
                Relation.link(offer.promo.company,user)
            }



        return m
    }

    static void unlink(offer,user) {
        def m = Stamp.findByPromoAndUser(offer.promo, user)
        if (m)
        {
            user?.removeFromStamps(m)
            offer?.promo?.removeFromStamps(m)
            m.delete()
        }
    }

    static constraints = {
    }
    static mapping = {
        sort date: "asc"
    }
}
