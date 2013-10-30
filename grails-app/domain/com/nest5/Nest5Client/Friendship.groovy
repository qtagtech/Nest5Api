package com.nest5.Nest5Client

class Friendship {

    static belongsTo = [user1: User, user2: User]

    User user1
    User user2
    Date firstTime
    Boolean accepted1
    Boolean accepted2



    static Friendship link(user1, user2) {
        def m = Friendship.findByUser1AndUser2(user1, user2) /*?: Friendship.findByUser1AndUser2(user2,user1)*/ //Ya permite si uno lo sigue y el otro no, son diferentes amistades//Busca si hay algun vinculo en cualquier orden, estando uno en una posicion o en la otra
        if (!m)
        {
            m = new Friendship()
            m.user1 = user1
            m.user2 = user2
            user1?.addToFriends(m)
            user2?.addToIsFriendOf(m)
            m.firstTime = new Date()
            m.accepted1 = true
            m.accepted2 = true


            if(!m.save())
            {
                println m.errors.allErrors
            }


        }
        return m
    }

    static void unlink(user1, user2) {
        def m = Friendship.findByUser1AndUser2(user1, user2)
        if (m)
        {
            user1?.removeFromFriends(m)
            user2.removeFromIsFriendOf(m)
            m.delete()
        }
        /*def n = Friendship.findByUser1AndUser2(user2, user1)
        if (n){
            user2.removeFromFriends(n)
            user1.removeFromIsFriendOf(n)
            n.delete()
        }*/
    }

    static constraints = {
    }
}
