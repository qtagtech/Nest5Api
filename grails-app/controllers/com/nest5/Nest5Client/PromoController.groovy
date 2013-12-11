package com.nest5.Nest5Client

import grails.converters.JSON
import grails.plugins.springsecurity.Secured

import org.springframework.dao.DataIntegrityViolationException

class PromoController {
    def springSecurityService
    def utilityService
    def couponService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [promoInstanceList: Promo.list(params), promoInstanceTotal: Promo.count()]
    }

    def create() {
        [promoInstance: new Promo(params)]
    }

    def save() {
        def promo = new Promo()
        promo.properties = params
        promo.cantArt = 5;
        def date = new Date()
        promo.createdAt = date
        promo.validThru = date + 60
        if(!params.yearqty)
        {
           promo.couponValidity = "0 2 0 0 0 0"
        }
        else
        {

//            def period = new DatumDependentDuration((params.yearqty).toInteger(),(params.monthqty).toInteger(),(params.dayqty).toInteger(),(params.hourqty).toInteger(),(params.minuteqty).toInteger(),(params.secondqty).toInteger(),0)
            def period = params.yearqty+" "+params.monthqty+" "+params.dayqty+" "+params.hourqty+" "+params.minuteqty+" "+params.secondqty
            promo.couponValidity = period.trim()
        }

        //println params
        if (!params.company)
        {
//            println "aca2"
          def company = springSecurityService.currentUser
            if (!company)
            {

                def data =  [errors: "0", errorType: "0",promoId: promo.id, messages: [[id:4 , value: message(code: 'promo.notloggedin', args: [],default: "<br>Error!, No te encuentras loggeado actualmente")] ] ]
                render data  as JSON
                return
            }
            promo.company = company
        }
        if(promo.save(flush: true))
        {
            def data =  [errors: "0", errorType: "0",promoId: promo.id, messages: [[id:4 , value: message(code: 'promo.create.success', args: [],default: "<br>Great!, Your Brand is now ready to attract customers by benefits in Nest5<br>Continue with the last Steps and get used to Nest5: <br><br><button id='gostepthree'>Go To Step 3</button>")] ] ]
            render data  as JSON
            return

        }
        else
        {
            def data =  [errors: "1", errorType: "1",promoId: null, messages: [id: "NA",value:  promo.errors] ]
            render data  as JSON
            return
        }
    }

    def show() {
        def promoInstance = Promo.get(params.id)
        if (!promoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'promo.label', default: 'Promo'), params.id])
            redirect(action: "list")
            return
        }

        [promoInstance: promoInstance]
    }

    def edit() {
        def promoInstance = Promo.get(params.id)
        if (!promoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'promo.label', default: 'Promo'), params.id])
            redirect(action: "list")
            return
        }

        [promoInstance: promoInstance]
    }

    def update() {
        def promoInstance = Promo.get(params.id)
        if (!promoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'promo.label', default: 'Promo'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (promoInstance.version > version) {
                promoInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'promo.label', default: 'Promo')] as Object[],
                        "Another user has updated this Promo while you were editing")
                render(view: "edit", model: [promoInstance: promoInstance])
                return
            }
        }

        promoInstance.properties = params

        if (!promoInstance.save(flush: true)) {
            render(view: "edit", model: [promoInstance: promoInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'promo.label', default: 'Promo'), promoInstance.id])
        redirect(action: "show", id: promoInstance.id)
    }

    def delete() {
        def promoInstance = Promo.get(params.id)
        if (!promoInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'promo.label', default: 'Promo'), params.id])
            redirect(action: "list")
            return
        }

        try {
            promoInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'promo.label', default: 'Promo'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'promo.label', default: 'Promo'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
    /*@Secured(["ROLE_COMPANY"])
    def createQR() {
        def userInstance = springSecurityService.currentUser
        response.contentType = "image/png"
//        def promos = userInstance.managedPromos


        def offer = Offer.findById(params.promocode.toInteger())
//
//        response.setHeader("Content-disposition", "attachment; filename=book.png") //para poner que descargue archivo
        def outputStream = response.outputStream
        QRCodeRenderer qrcodeRenderer = new QRCodeRenderer()
        qrcodeRenderer.renderPng(offer.id.toString(), 400, outputStream)

    }*/
    @Secured(["ROLE_COMPANY"])
    def createOffer()  {
        def store = Store.get(params.storeid)
        def promo = Promo.get(params.promoid)
        println store
        println promo
        def m = Offer.findByStoreAndPromo(store, promo)
        if(m)
        {
            def data =  [errors: "1", errorType: "1",storeId: store.id, messages: [[id:4 , value: message(code: 'store.create.success', args: [],default: "<br>That's It!, There's only one thing left to do with Nest5: Analize your data, youre customer's behavior, get better every day with what we tell you and make them fall in love with your brand by giving and communicating what they really expect from you.<br><br><button id='gotopanel'>Go To Step 4</button>")] ] ]
            render data  as JSON
            return
        }
        else
        {
            def offer = promo.addToStore(store)
            if(offer)
            {
                def data =  [errors: "0", errorType: "0",storeId: store.id, messages: [[id:4 , value: message(code: 'store.create.success', args: [],default: "<br>That's It!, There's only one thing left to do with Nest5: Analize your data, youre customer's behavior, get better every day with what we tell you and make them fall in love with your brand by giving and communicating what they really expect from you.<br><br><button id='gotopanel'>Go To Step 4</button>")] ] ]
                render data  as JSON
                return
            }
            else
            {
                def data =  [errors: "1", errorType: "0",storeId: store.id, messages: [[id:4 , value: message(code: 'store.create.success', args: [],default: "<br>That's It!, There's only one thing left to do with Nest5: Analize your data, youre customer's behavior, get better every day with what we tell you and make them fall in love with your brand by giving and communicating what they really expect from you.<br><br><button id='gotopanel'>Go To Step 4</button>")] ] ]
                render data  as JSON
                return
            }
        }

    }

    @Secured(["ROLE_COMPANY"])
    def searchOffer()
    {
       def promo = Promo.get(params.promoid)
        def store = Store.get(params.storeid)
       def offer = Offer.findByPromoAndStore(promo,store)
       def data
        if(!offer)
        {
           data = [offer:null]
        }
        else
        {
           data = [offer:offer,promo: [promo: offer.promo,display: offer.promo.toString()],store:offer.store]

        }
        render data as JSON
        return
        //
    }

    @Secured(["ROLE_COMPANY"])
    def countStores(){
        if(request.xhr){
            def promo = Promo.get(params.promoid)
            def offers = promo.offers
            def data =  [offers:offers]
            render data  as JSON
            return
        }

        else
        {
            response.sendError(400)
        }


    }

    def showDealsOld(){
       /* def deals = [[company : [id: 1,name:"Sushi ToGo"], promos: [[id: 1, action: "buy", reqQTY: 1, perkQTY: 1, requirement: "Plato Asiatico de dos o mas ingredientes",perk:"Postre de la casa gratis"],[id: 1, action: "buy", reqQTY: 1, perkQTY: 1, requirement: "Plato Asiatico de dos o mas ingredientes",perk:"Postre de la casa gratis"],[id: 1, action: "buy", reqQTY: 1, perkQTY: 1, requirement: "Plato Asiatico de dos o mas ingredientes",perk:"Postre de la casa gratis"]], imagen : resource(dir: 'images', file:'logo.png') ],
                [company : [id: 2,name:"La Ostrería"], promos: [[id: 1, action: "buy", reqQTY: 2, perkQTY: 1, requirement: "fajitas Mexicanas Especiales",perk:"igual con 20% de Descuento"]], imagen : resource(dir: 'images', file:'logo.png')],
                [company : [id: 3,name:"Puerto Camarón"], promos: [[id: 1, action: "visit", reqQTY: 5, perkQTY: 1, requirement: "Jueves",perk:"Pase para ingresar todas las mujeres gratis cualquier dia."]], imagen : resource(dir: 'images', file:'logo.png')],
                [company : [id: 4,name:"El Trío"], promos: [[id: 1, action: "buy", reqQTY: 5, perkQTY: 1, requirement: "Promociones de 3x1 en Cocktails",perk:"Gratis"]], imagen : resource(dir: 'images', file:'logo.png')],
                [company : [id: 5,name:"Pizzettas"], promos: [[id: 1, action: "buy", reqQTY: 5, perkQTY: 1, requirement: "Promociones de 3x1 en Cocktails",perk:"Gratis"]], imagen : resource(dir: 'images', file:'logo.png')]]
        render deals as JSON*/

//        def companies = Company.list()
//        def pruebas = Company.findByEmail("juan.perez@miempresa.com")
        //recibe latitude y longitude

        def lat = params.latitude ? new BigDecimal(params.latitude) : new BigDecimal(6.209002)
        def lng = params.longitude ? new BigDecimal(params.longitude) : new BigDecimal(-75.567582)

        def rectangle = utilityService.getRectangle(lat,lng,2);
        /*render rectangle as JSON
        return*/

        def testCompany = Company.findByEmail("juan.perez@miempresa.com")
        def cc = Store.createCriteria()
        //revisar si hay promociones en la hora actual

        def stores = cc.list{
            ne("company",testCompany)
            between("latitude",rectangle.lat1,rectangle.lat2)
            between("longitude",rectangle.lon1,rectangle.lon2)
        }


//        println stores
        def deals = []
        stores.each{
            def offers = it.offers
            def benefits = []
            def distance = utilityService.getDistance(lat,lng,it.latitude,it.longitude)
            offers.each{cur->
                def act = [id: cur.promo.id,action: cur.promo.activity,reqQTY: cur.promo.cantArt,perkQTY: 1,requirement: cur.promo.article,perk: cur.promo.wins]
                benefits += act

            }
            def deal = [company: [id: it.company.id,name:it.name,address:it.company.address],distance: distance,latitude: it.latitude,longitude:it.longitude,promos: benefits,imagen : resource(dir: 'images', file:'logo.png')]
            deals += deal
        }
        /*def c = Company.createCriteria()
        def companies = c.list {
                ne("email","juan.perez@miempresa.com")
        }
        def deals = []

        companies.each{
            def promos = it.managedPromos
            def benefits = []
            promos.each{cur->
                  def act = [id: cur.id,action: cur.activity,reqQTY: cur.cantArt,perkQTY: 1,requirement: cur.article,perk: cur.wins]
                benefits += act
            }
                 def deal = [company: [id: it.id,name:it.name,address:it.address],promos: benefits,imagen : resource(dir: 'images', file:'logo.png')]
            deals += deal
        }*/

        render deals.sort({it.distance}) as JSON
        return

    }

    def showDirectory(){
        /* def deals = [[company : [id: 1,name:"Sushi ToGo"], promos: [[id: 1, action: "buy", reqQTY: 1, perkQTY: 1, requirement: "Plato Asiatico de dos o mas ingredientes",perk:"Postre de la casa gratis"],[id: 1, action: "buy", reqQTY: 1, perkQTY: 1, requirement: "Plato Asiatico de dos o mas ingredientes",perk:"Postre de la casa gratis"],[id: 1, action: "buy", reqQTY: 1, perkQTY: 1, requirement: "Plato Asiatico de dos o mas ingredientes",perk:"Postre de la casa gratis"]], imagen : resource(dir: 'images', file:'logo.png') ],
             [company : [id: 2,name:"La Ostrería"], promos: [[id: 1, action: "buy", reqQTY: 2, perkQTY: 1, requirement: "fajitas Mexicanas Especiales",perk:"igual con 20% de Descuento"]], imagen : resource(dir: 'images', file:'logo.png')],
             [company : [id: 3,name:"Puerto Camarón"], promos: [[id: 1, action: "visit", reqQTY: 5, perkQTY: 1, requirement: "Jueves",perk:"Pase para ingresar todas las mujeres gratis cualquier dia."]], imagen : resource(dir: 'images', file:'logo.png')],
             [company : [id: 4,name:"El Trío"], promos: [[id: 1, action: "buy", reqQTY: 5, perkQTY: 1, requirement: "Promociones de 3x1 en Cocktails",perk:"Gratis"]], imagen : resource(dir: 'images', file:'logo.png')],
             [company : [id: 5,name:"Pizzettas"], promos: [[id: 1, action: "buy", reqQTY: 5, perkQTY: 1, requirement: "Promociones de 3x1 en Cocktails",perk:"Gratis"]], imagen : resource(dir: 'images', file:'logo.png')]]
     render deals as JSON*/

       // def companies = Company.list()
        def pruebas = Company.findByEmail("juan.perez@miempresa.com")
        //recibe latitude y longitude

//        def lat = params.latitude ? new BigDecimal(params.latitude) : new BigDecimal(6.209002)
//        def lng = params.longitude ? new BigDecimal(params.longitude) : new BigDecimal(-75.567582)

//        def rectangle = utilityService.getRectangle(lat,lng,2);
        /*render rectangle as JSON
        return*/

//        def testCompany = Company.findByEmail("juan.perez@miempresa.com")
        def cc = Store.createCriteria()
        def stores = cc.list{
            ne("company",pruebas)
//            between("latitude",rectangle.lat1,rectangle.lat2)
//            between("longitude",rectangle.lon1,rectangle.lon2)
        }


//        println stores
        def deals = []
        stores.each{
            def offers = it.offers
            def benefits = []
            def distance = 0
            offers.each{cur->
                def act = [id: cur.promo.id,action: cur.promo.activity,reqQTY: cur.promo.cantArt,perkQTY: 1,requirement: cur.promo.article,perk: cur.promo.wins]
                benefits += act

            }
            def deal = [company: [id: it.company.id,name:it.name,address:it.company.address],distance: distance,latitude: it.latitude,longitude:it.longitude,promos: benefits,imagen : resource(dir: 'images', file:'logo.png'),store:  [id: it.id,name: it.name]]
            deals += deal
        }
        /*def c = Company.createCriteria()
        def companies = c.list {
                ne("email","juan.perez@miempresa.com")
        }
        def deals = []

        companies.each{
            def promos = it.managedPromos
            def benefits = []
            promos.each{cur->
                  def act = [id: cur.id,action: cur.activity,reqQTY: cur.cantArt,perkQTY: 1,requirement: cur.article,perk: cur.wins]
                benefits += act
            }
                 def deal = [company: [id: it.id,name:it.name,address:it.address],promos: benefits,imagen : resource(dir: 'images', file:'logo.png')]
            deals += deal
        }*/

        render deals.sort({it.company.name}) as JSON
        return

    }

    //Asegurar para API, cuadrar en android la autenticación al ejecutar asynctask
    @Secured(["ROLE_API"])
    def showMyDeals(){

        def userId = params.id
        println userId


        def userInstance = User.findById(userId)
        println userInstance
        if(!userId)
        {
            def data =  [status: 0,uid: 0, errors: "1", errorType: "0", messages: [id: "NA",value:  "¡No existe el usuario!"] ]
            render data  as JSON
            return
        }
        def promos = couponService.showMyDeals(userInstance,10,0)
        def benefits = []
        promos.each{cur->

            def stamps = couponService.getStamps(userInstance,cur)
            def act = [company: [id: cur.company.id,name:cur.company.name],imagen : resource(dir: 'images', file:'logo.png'),promocion: [id: cur.id,action: cur.activity,reqQTY: cur.cantArt,perkQTY: 1,requirement: cur.article,perk: cur.wins],stamps: stamps]
            benefits += act
        }
        //



        render benefits as JSON
        return



    }

//    @Secured(["ROLE_API"])
    def beLucky()
    {
        try {
            def userId = params.id?.trim() as Long


            def code = params.code
            def userInstance = User.findById(userId)

            if(!userInstance)
            {
                def data =  [status: 0,uid: 0, errors: "1", errorType: "0", messages: [id: "NA",value:  "¡No existe el usuario!"],stamps: null, coupons: null ]
                render data  as JSON
                return
            }

            def offer = Offer.findById((Double)code.toInteger())
            if(!offer)
            {
                println params.code
                def data =  [status: 0,uid: 0, errors: "1", errorType: "0", messages: [id: "NA",value:  "¡No existe la promoción!"],stamps: null, coupons: null]
                render data  as JSON
                return
            }
            // println userInstance
            //println promo.company
            userInstance.addToPromo(offer)
            Category.withSession { session ->
                session.flush()
            }
            def results = couponService.saveCoupons(userInstance,offer.promo, offer.store.name)

            if(params.frombusiness != "claro"){
                //render text: "listo"
                //devolver json con todo lo que sea para validar si recibió bien el código
                def promoDesc = [id: offer.promo.id,action: offer.promo.activity,reqQTY: offer.promo.cantArt,perkQTY: 1,requirement: offer.promo.article,perk: offer.promo.wins]
                def payload = [promo: promoDesc, coupons: results.coupons, stamps: results.stamps, company: offer.promo.company]
                def data =  [status: 1,uid: userInstance.id, errors: "0", errorType: "0", messages: [id: "NA",value:  "¡Éxitoo!"],payload: payload  ]
                println data
                render data  as JSON
                return
            }
            def cupones = Coupon.findAllByUser(userInstance)
            def data =  [status: 1, errors: "0", errorType: "0", messages: [id: "NA",value:  "¡Éxitoo!"],stamps: results.stamps.size(), coupons: cupones.size(), uid: 0  ]
            println data
            render data  as JSON
            return
        }
        catch (Exception e){

            def data =  [status: 0,uid: 0, errors: "1", errorType: "0", messages: [id: "NA",value:  "¡Error con el servidor, inténtalo de nuevo por favor!"],stamps: null, coupons: null]
            render data  as JSON
            return
        }



    }

    /*Redimir cupon desde app de business
    *
    * */


    def redeemCouponBusiness()
    {
        try {
            def userId = params.id?.trim() as Long


            def code = params.code
            def userInstance = User.findById(userId)

            if(!userInstance)
            {
                def data =  [status: 0,uid: 0, errors: "1", errorType: "0", messages: [id: "NA",value:  "¡No existe el usuario!"],stamps: null, coupons: null ]
                render data  as JSON
                return
            }

            def offer = Offer.findById((Double)code.toInteger())
            if(!offer)
            {
                println params.code
                def data =  [status: 0,uid: 0, errors: "1", errorType: "0", messages: [id: "NA",value:  "¡No existe la promoción!"],stamps: null, coupons: null]
                render data  as JSON
                return
            }
            // println userInstance
            //println promo.company
            def c = Coupon.createCriteria()
            def coupon = c.list{
                eq("user",userInstance)
                eq("redeemed",0)
                eq("promo",offer.promo)

            }

            if(coupon.size() == 0){
                def data =  [status: 0,uid: 0, errors: "1", errorType: "0", messages: [id: "NA",value:  "¡No hay beneficios disponibles para este usuario y esta promoción.!"],stamps: null, coupons: null ]
                render data  as JSON
                return
            }

            def cou = coupon.first()
            cou.redeemed = 1
            if(!cou.save(flush: true)){
                def data =  [status: 0,uid: 0, errors: "1", errorType: "0", messages: [id: "NA",value:  "¡Error guardando el beneficio redimido, inténtalo de nuevo por favor.!"],stamps: null, coupons: null ]
                render data  as JSON
                return
            }




            def data =  [status: 1, errors: "0", errorType: "0", messages: [id: "NA",value:  "¡Éxitoo!"],stamps: null, coupons: coupon.size() - 1, uid: 0  ]
            println data
            render data  as JSON
            return
        }
        catch (Exception e){

            def data =  [status: 0,uid: 0, errors: "1", errorType: "0", messages: [id: "NA",value:  "¡Error con el servidor, inténtalo de nuevo por favor!"],stamps: null, coupons: null]
            render data  as JSON
            return
        }



    }
    @Secured(["ROLE_API"])
    def beLuckyTest()
    {
        def userId = params.id


        def code = params.code
        def userInstance = User.findById(userId)
        if(!userInstance)
        {
            def data =  [status: 0,uid: 0, errors: "1", errorType: "0", messages: [id: "NA",value:  "¡No existe el usuario!"] ]
            render data  as JSON
            return
        }

        def offer = Offer.findById((Double)code.toInteger())
        if(!offer)
        {
            def data =  [status: 0,uid: 0, errors: "1", errorType: "0", messages: [id: "NA",value:  "¡No existe la promoción!"] ]
            render data  as JSON
            return
        }
        def serverTime = Calendar.getInstance()   //estará en UTC
        def localTime = new GregorianCalendar(TimeZone.getTimeZone(offer.timeZone))
        localTime.setTimeInMillis(serverTime.getTimeInMillis())
        def hour = localTime.get(Calendar.HOUR_OF_DAY)
        //def minute = localTime.get(Calendar.MINUTE)
        //def second = localTime.get(Calendar.SECOND)
        def weekDay = localTime.get(Calendar.DAY_OF_WEEK)
        //def hora = [server: serverTime,local: localTime,hourofdaylocal: hour,minute:minute,second:second,day:weekDay]
        //horas += hora
        def validDays = offer.validDays.split("|")
        if (!(weekDay.toString() in validDays) || !(offer.validHourMin <= hour) || !(offer.validHourMax >= hour))
        {
            def data =  [status: 0,uid: 0, errors: "1", errorType: "0", messages: [id: "NA",value:  "¡Esta Promoción no es válida en estos momentos!"] ]
            render data  as JSON
            return
        }
        // println userInstance
        //println promo.company
        userInstance.addToPromo(offer)
        def results = couponService.saveCoupons(userInstance,offer.promo,offer.store.name)


        //render text: "listo"
        //devolver json con todo lo que sea para validar si recibió bien el código
        def promoDesc = [id: offer.promo.id,action: offer.promo.activity,reqQTY: offer.promo.cantArt,perkQTY: 1,requirement: offer.promo.article,perk: offer.promo.wins]
        def payload = [promo: promoDesc, coupons: results.coupons, stamps: results.stamps, company: offer.promo.company]
        def data =  [status: 1,uid: userInstance.id, errors: "0", errorType: "0", messages: [id: "NA",value:  "¡Éxitoo!"],payload: payload  ]
        println data
        render data  as JSON
        return

    }

    @Secured(["ROLE_API"])
    def redeemCoupon()
    {
        def userId = params.id
        def code = params.code
        def couponid = (params.couponid).toInteger()
        def userInstance = User.findById(userId)
        if(!userId)
        {
            def data =  [status: 0,uid: 0, errors: "1", errorType: "0", messages: [id: "NA",value:  "¡No existe el usuario!"] ]
            render data  as JSON
            return
        }

        def offer = Offer.findById(code.toInteger())
        if(!offer)
        {
            def data =  [status: 0,uid: 0, errors: "1", errorType: "0", messages: [id: "NA",value:  "¡No existe la promoción!"] ]
            render data  as JSON
            return
        }

        /*def c = Coupon.createCriteria()
        def coupon = c.list{
            eq("user",userInstance)
            eq("redeemed",0)
            eq("promo",offer.promo)
        }*/
        def coupon = null
        try
        {
           coupon = Coupon.get(couponid)
        }
        catch(e)
        {
            def data =  [status: 0,uid: 0, errors: "1", errorType: "0", messages: [id: "NA",value:  "¡Error usando get"] ]
            render data  as JSON
            return
        }


        if(!coupon)
        {
            def data =  [status: 0,uid: 0, errors: "1", errorType: "0", messages: [id: "NA",value:  "¡No tienes cupones válidos!"] ]
            render data  as JSON
            return
        }
        //def cou = coupon[0] //código viejo
        def cou = coupon
        cou.redeemed = 1
        //Actualizar fecha de redención para mantener base de datos organizada
        if(!cou.save())
        {
            def data =  [status: 0,uid: 0, errors: "1", errorType: "0", messages: [id: "NA",value:  "¡Error guardando cupón!"] ]
            render data  as JSON
            return
        }


        //render text: "listo"
        //devolver json con todo lo que sea para validar si recibió bien el código

        def data =  [status: 1,uid: userInstance.id, errors: "0", errorType: "0", messages: [id: "NA",value:  "¡Éxitoo!"]]
        render data  as JSON
        return
    }

    def showDeals(){

        println "aca"
        def lat = params.latitude ? new BigDecimal(params.latitude) : new BigDecimal(6.209002)
        def lng = params.longitude ? new BigDecimal(params.longitude) : new BigDecimal(-75.567582)

        def rectangle = utilityService.getRectangle(lat,lng,2);
        /*render rectangle as JSON
        return*/

        def testCompany = Company.findByEmail("juan.perez@miempresa.com")
        println testCompany
        def cc = Store.createCriteria()
        //revisar si hay promociones en la hora actual
        def serverTime = Calendar.getInstance()   //estará en UTC
        //println currentTime

        //def start = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        //println start
        //
        //start.set(year: 2012, month: 9, date: 25, hourOfDay: 11, minute: 51,second: 0,millisecond: 0)
        //def end = Calendar.getInstance(TimeZone.getTimeZone(grailsApplication.config.app.timezone))
        //end.set(year: fecha2[2].toInteger(), month: month2, date: fecha2[1].toInteger() + 1, hourOfDay: 0, minute: 0,second: 0,millisecond: 0)
        //println start
        //println end
        //def startDate = start.getTime()
        //SimpleDateFormat sdf = new SimpleDateFormat ("MM/dd/yyyy HH:mm:ss") //poner que esto siempre sea 00:00:00
        //sdf.setTimeZone (TimeZone.getTimeZone ("UTC"))
        //startDate = new Date().parse('MM/dd/yyyy HH:mm:ss',startDate.time)
        //println startDate

        //startDate = sdf.format(startDate)
        //println startDate
        //endDate = end.getTime()
        def stores = cc.list{
            ne("company",testCompany)
            between("latitude",rectangle.lat1,rectangle.lat2)
            between("longitude",rectangle.lon1,rectangle.lon2)
        }

        stores = Store.findAll()

        println stores

        def horas = []
//        println stores
        def deals = []
        //println stores
        stores.each{
            def offers = it.offers
            println "ofertas disponibles: "
            println offers
            def benefits = []
            def distance = utilityService.getDistance(lat,lng,it.latitude,it.longitude)
           // println offers
            offers.each{cur->
                def localTime = new GregorianCalendar(TimeZone.getTimeZone(cur.timeZone))
                localTime.setTimeInMillis(serverTime.getTimeInMillis())
                def hour = localTime.get(Calendar.HOUR_OF_DAY)
                def minute = localTime.get(Calendar.MINUTE)
                def second = localTime.get(Calendar.SECOND)
                def weekDay = localTime.get(Calendar.DAY_OF_WEEK)
                //def hora = [server: serverTime,local: localTime,hourofdaylocal: hour,minute:minute,second:second,day:weekDay]
                //horas += hora
                def validDays = cur.validDays.split("|")
                if ((weekDay.toString() in validDays) && (cur.validHourMin <= hour) && (cur.validHourMax >= hour))
                {
                    //println cur.toString() + " es valida hoy, el dia"+weekDay.toString()+" entre las "+cur.validHourMin+" y las "+cur.validHourMax
                    def act = [id: cur.promo.id,action: cur.promo.activity,reqQTY: cur.promo.cantArt,perkQTY: 1,requirement: cur.promo.article,perk: cur.promo.wins,minHour: cur.validHourMin,maxHour:cur.validHourMax,hour:hour]
                    benefits += act
                }


            }
            if(benefits.size() > 0)
            {
                def deal = [company: [id: it.company.id,name:it.company.name,address:it.company.address],store: it.name,distance: distance,latitude: it.latitude,longitude:it.longitude,promos: benefits,imagen : resource(dir: 'images', file:'logo.png')]
                deals += deal
            }

        }
        /*def c = Company.createCriteria()
        def companies = c.list {
                ne("email","juan.perez@miempresa.com")
        }
        def deals = []

        companies.each{
            def promos = it.managedPromos
            def benefits = []
            promos.each{cur->
                  def act = [id: cur.id,action: cur.activity,reqQTY: cur.cantArt,perkQTY: 1,requirement: cur.article,perk: cur.wins]
                benefits += act
            }
                 def deal = [company: [id: it.id,name:it.name,address:it.address],promos: benefits,imagen : resource(dir: 'images', file:'logo.png')]
            deals += deal
        }*/
         //render horas as JSON
        println deals
        render deals.sort({it.distance}) as JSON
        //
        return

    }

    def showCompanyOffers(){


        def id = params.id ? params.id.toInteger() : 0
        def cc = Store.createCriteria()
        def serverTime = Calendar.getInstance()   //estará en UTC
        def company = Company.get(id)
        if(!company)
        {
            def result = [company: [id: null,name:null,address:null],promos: null,imagen : null]
            render result as JSON
            return
        }

        def stores = Store.findAllByCompany(company)


        def horas = []
//        println stores
        //def deals = []
        //println stores
        def benefits = []
        def locations = []
        stores.each{
            def offers = it.offers

            //def distance = utilityService.getDistance(lat,lng,it.latitude,it.longitude)
            // println offers
            offers.each{cur->
                def localTime = new GregorianCalendar(TimeZone.getTimeZone(cur.timeZone))
                localTime.setTimeInMillis(serverTime.getTimeInMillis())
                def hour = localTime.get(Calendar.HOUR_OF_DAY)
                def minute = localTime.get(Calendar.MINUTE)
                def second = localTime.get(Calendar.SECOND)
                def weekDay = localTime.get(Calendar.DAY_OF_WEEK)
                //def hora = [server: serverTime,local: localTime,hourofdaylocal: hour,minute:minute,second:second,day:weekDay]
                //horas += hora
                def validDays = cur.validDays.split("|")
                if ((weekDay.toString() in validDays) && (cur.validHourMin <= hour) && (cur.validHourMax >= hour))
                {
                    //println cur.toString() + " es valida hoy, el dia"+weekDay.toString()+" entre las "+cur.validHourMin+" y las "+cur.validHourMax
                    def act = [id: cur.promo.id,action: cur.promo.activity,reqQTY: cur.promo.cantArt,perkQTY: 1,requirement: cur.promo.article,perk: cur.promo.wins,minHour: cur.validHourMin,maxHour:cur.validHourMax,hour:hour]
                    benefits += act
                }

                locations += [it.latitude,it.longitude]



            }


        }
        //locations += [6.242151,-75.585853]
        def deals = [company: [id: company.id,name:company.name,address:company.address],locations: locations,promos: benefits,imagen : resource(dir: 'images/partners', file:'La_Ostreria.jpg')]

        render deals as JSON
        //
        return

    }

    def showStoreOffers()
    {
        def id = params.id ? params.id.toInteger() : 0

        def serverTime = Calendar.getInstance()   //estará en UTC
        def store = Store.get(id)
        if(!store)
        {
            def result = [company: [id: null,name:null,address:null],promos: null,imagen : null]
            render result as JSON
            return
        }

       // def stores = Store.findAllByCompany(company)


        def horas = []
//        println stores
        //def deals = []
        //println stores
        def benefits = []


            def offers = store.offers

            offers.each{cur->
                def localTime = new GregorianCalendar(TimeZone.getTimeZone(cur.timeZone))
                localTime.setTimeInMillis(serverTime.getTimeInMillis())
                def hour = localTime.get(Calendar.HOUR_OF_DAY)
                def minute = localTime.get(Calendar.MINUTE)
                def second = localTime.get(Calendar.SECOND)
                def weekDay = localTime.get(Calendar.DAY_OF_WEEK)
                //def hora = [server: serverTime,local: localTime,hourofdaylocal: hour,minute:minute,second:second,day:weekDay]
                //horas += hora
                def validDays = cur.validDays.split("|")
                if ((weekDay.toString() in validDays) && (cur.validHourMin <= hour) && (cur.validHourMax >= hour))
                {
                    //println cur.toString() + " es valida hoy, el dia"+weekDay.toString()+" entre las "+cur.validHourMin+" y las "+cur.validHourMax
                    def act = [id: cur.promo.id,action: cur.promo.activity,reqQTY: cur.promo.cantArt,perkQTY: 1,requirement: cur.promo.article,perk: cur.promo.wins,minHour: cur.validHourMin,maxHour:cur.validHourMax,hour:hour]
                    benefits += act
                }





            }



        //locations += [6.242151,-75.585853]
        def deals = [company: [id: store.company.id,name:store.company.name,address:store.company.address],name: store.name,locations: [store.latitude,store.longitude],promos: benefits,imagen : resource(dir: 'images/partners', file:'La_Ostreria.jpg')]

        render deals as JSON
        //
        return
    }

}
