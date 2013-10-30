package com.nest5.Nest5Client

class Coupon {
    static belongsTo = [user : User,promo: Promo]

    Date date
    int redeemed = 0
    Date validThru


    static constraints = {
    }
}
