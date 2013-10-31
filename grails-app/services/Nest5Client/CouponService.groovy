package Nest5Client

import com.nest5.Nest5Client.Coupon
import com.nest5.Nest5Client.Event
import com.nest5.Nest5Client.EventType
import com.nest5.Nest5Client.Stamp
import com.nest5.Nest5Client.User
import groovy.time.DatumDependentDuration

class CouponService {

    def saveCoupons(user,promo,store){
        def tStamps = 0
        def savedCoupons = []
        def s = Stamp.createCriteria()
        def stampList = s.list{
            eq("user",user)
            eq("promo",promo)
            eq("redeemed", false)
        }
        tStamps = stampList.size()

        if (tStamps >= promo.cantArt)
        {
            def coupons = 0
            coupons = (tStamps/promo.cantArt).toInteger()



            if (coupons >= 1)
            {

                def error = false
                def l = 0
                def durations = promo.couponValidity.split()
                while(l < coupons)
                {
                    def coupon = new Coupon()
                    coupon.promo = promo
                    coupon.user = user
                    def now = new Date()
                    coupon.date = now

                    def period = new DatumDependentDuration((durations[0]).toInteger(),(durations[1]).toInteger(),(durations[2]).toInteger(),(durations[3]).toInteger(),(durations[4]).toInteger(),(durations[5]).toInteger(),0)
                    coupon.validThru = period + now
                    coupon.redeemed = 0
                    if(!coupon.save(flush:  true))
                    {
                        error = true
                    }
                    if(!error)
                    {
                        savedCoupons += coupon
                    }
                    def i = 0
                    def j = 0
                    while(i < promo.cantArt)
                    {
                        if(!stampList[j].redeemed)
                        {
                            stampList[j].redeemed = true
                            stampList[j].save()
                            i++
                        }
                        j++



                    }
                    l++
                }



            }
        }
        def c = Stamp.createCriteria()
        stampList = c.list{
            eq("user",user)
            eq("promo",promo)
            eq("redeemed", false)
        }

//        save stampCard event with the number of stamps the user has in the current offer and  the current store
        /*new event-> othercompany true, issatmp true, */

       /* def stampType = EventType.findByCode("NEI001")
        def duUser = User.findByUsername("dummyNest5")
        println duUser.username
        def stampEvent = new Event(
                type: stampType,
                user: user,
                otherUser: false,
                otherBrand: true,
                company: promo?.company,
                oUser: duUser,
                stampCount: stampList?.size(),
                store: store,
                couponCount: 0,
                date: new Date()
        )
        if(!stampEvent.save(flush: true))
        {
            println stampEvent.errors.allErrors
        }

//        println stampEvent

        if(savedCoupons.size() > 0){
//            save getCoupon Event for the number of coupons won by the user.
            def couponType = EventType.findByCode("NEI002")
            def couponEvent = new Event(
                    type: couponType,
                    user: user,
                    otherUser: false,
                    otherBrand: true,
                    company: promo?.company,
                    oUser: User.findByUsername("Nest5Dummy"),
                    stampCount: 0,
                    store: store,
                    couponCount: savedCoupons.size(),
                    date: new Date()
            ).save(flush: true)
        }*/




        return [coupons: savedCoupons, stamps: stampList]


    }

    def showMyDeals(user,limit,offset)
    {
        def tStamps = 0
        def savedCoupons = []
        def s = Stamp.createCriteria()
        //s.setFirstResult(offset)
        def appliedPromos = s.list{
            /*eq("user",user)
            eq("redeemed", false)
            maxResults(limit)
            order("promo", "desc")*/
            projections {
                eq("redeemed", false)
                eq("user",user)
                maxResults(limit)
                groupProperty("promo")

            }
        }

        return appliedPromos
    }

    def getStamps(user,promo)
    {
        def c = Stamp.createCriteria()
        def stampList = c.list{
            eq("user",user)
            eq("promo",promo)
            eq("redeemed", false)
        }
    }
}
