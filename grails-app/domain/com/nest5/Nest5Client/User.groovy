package com.nest5.Nest5Client

class User extends SecUser {
    static mappedBy = [refCodes: 'referer',coupons: "user",friends: "user1", isFriendOf: "user2"]
    static hasMany = [/*achievements : Achievements,*//*refCodes: RefCodes,*/stamps:Stamp,brands:Relation,coupons: Coupon,chats: Message,friends: Friendship, isFriendOf: Friendship,media: Media]

    
    String name
    String email
    String date
    Boolean smartphone = false
    String smartphoneBrand = "NONE"
    String phone
    String referer
    int watched = 0
    double points = 0
    BigInteger uid = 0
    int tuid = 0
    String androidID = "0"
    String iphoneID = "0"
    String rimID = "0"
    String magnetic5 = "00000000"



    List promos() {
        return stamps.collect{it.promo}
    }
    List chats() {
        return chats.collect{it.company}
    }
    List brands(){
        return brands.collect{it.company}
    }
    List friends(){
        return friends.collect{it.user2} + isFriendOf.collect{it.user1}
    }


    List addToPromo(Offer offer) {
        Stamp.link(offer,this)
        return promos()
    }
    List addToChat(Company company, String content) {
        Message.addLine(company,this, content)
        return chats()
    }

    List removeFromPromo(Offer offer) {
        Stamp.unlink(offer,this)
        return promos()
    }

    List addToCompany(Company company) {
        Relation.link(company,this)
        return brands()
    }

    List removeFromCompany(Company company) {
        Relation.unlink(company,this)
        return brands()
    }

    List addToFriend(User user){
        Friendship.link(this,user)
        return friends()
    }
    List removeFromFriend(User user){
        Friendship.unlink(this,user)
        return friends()

    }







    static constraints = {

    }
    static  mapping = {

        magnetic5 type: 'text'

    }
    
    String toString(){
        name
    }
}
