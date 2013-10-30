package com.nest5.Nest5Client

class Event {

    static belongsTo = [type: EventType, user: User]
    Boolean otherUser
    Boolean otherBrand
    Date date
    Company company
    User oUser
    int stampCount
    String store
    int couponCount




    static constraints = {
    }
    static mapping = {
        sort date: "asc"
    }

    String toString(){

        def grailsApplication = new Event()?.domainClass?.grailsApplication
        def appCtx = grailsApplication?.mainContext

        def message = ""
        switch(this.type.code){


            case "NEI001":
                String[] obj = new Object[3]
                obj[0] = this.user.name
                obj[1] = this.company.name + ' - ' +this.store
                obj[2] = this.stampCount
                message = appCtx?.getMessage("user.stamp.card", obj, Locale.ENGLISH)
                break
            case "NEI002": //message = message(code: 'user.get.coupon', args: [it.user.name,it.company.name,"5","6" ])
                String[] obj = new Object[3]
                obj[0] = this.user.name
                obj[1] = this.couponCount
                obj[2] = this.company.name
                message = appCtx?.getMessage("user.get.coupon", obj, Locale.ENGLISH)
                break
            case "NEI003": //message = message(code: 'user.redeem.coupon', args: [it.user.name,it.company.name,"5","6" ])
                break
            case "NEI004":
                String[] obj = new Object[2]
                obj[0] = this.user.name
                obj[1] = this.oUser.name
                message = appCtx?.getMessage("user.add.friend", obj, Locale.ENGLISH)
                break

        }
        return message
    }
}

