package Nest5Client

import groovy.time.TimeCategory
import groovy.time.TimeDuration

class UtilityService {

    def getRectangle(mylat,mylon,distance) {
       //1 degree of latitude is 69 miles or 111km
       //1 degree of longitude is cos(latitude)*69miles
        // 1 degree of longitude is cos(latitude)*111km
        //distance given in km

        def lon1 = mylon - distance/Math.abs(Math.cos(Math.toRadians(mylat))*111)
        def lon2 = mylon + distance/Math.abs(Math.cos(Math.toRadians(mylat))*111)
        def lat1 = mylat - (distance/111)
        def lat2 = mylat + (distance/111)

        def rect = [lat1: new BigDecimal(lat1),lat2:new BigDecimal(lat2),lon1:new BigDecimal(lon1),lon2:new BigDecimal(lon2)]
        return rect
    }

    def getDistance(mylat,mylon,destlat,destlon)
    {
        def R = 3956
        def dLat = mylat - destlat
        def dLon = mylon - destlon
        def a = Math.pow(Math.sin(Math.toRadians(dLat)/2),2) + Math.cos(Math.toRadians(mylat))*Math.cos(Math.toRadians(destlat))*Math.pow(Math.sin(Math.toRadians(dLon)/2),2)
        def c = 2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a))
//        def c = 2*Math.asin(Math.sqrt(a))
        def d = R*c
        return d
    }

    def timeFrom(Date start){
        def stop = new Date()
        TimeDuration td = TimeCategory.minus( stop, start )
        def sTime = td.toString()
        def split = sTime.split(',')
        def val = split[0]
        def sepa = val.split(" ")
        sepa[1] = sepa[1][0]
        def result = sepa[0] + sepa[1]
        return result

    }
}
