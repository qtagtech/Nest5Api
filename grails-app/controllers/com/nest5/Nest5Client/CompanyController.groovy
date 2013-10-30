package com.nest5.Nest5Client

import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class CompanyController {
    def springSecurityService
    def userAgentIdentService
    def userService
    def companyService
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    @Secured(["ROLE_ADMIN"])
    def index() {
        redirect(action: "list", params: params)
    }

    @Secured(["ROLE_ADMIN"])
    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [companyInstanceList: Company.list(params), companyInstanceTotal: Company.count()]
    }


    def create() {
        [companyInstance: new Company(params),promoInstance: new Promo(params),storeInstance: new Store(params)]
    }


    def save() {
        //println(params.terms)

        if (params.terms != "accepted")
        {
            def data =  [errors: "1", errorType: "2",companyId: null, messages: [[id:13 , value: message(code: 'user.accept.terms', args: [],default: "You didn´t accept the terms and conditions.")]] ]
            render data  as JSON
            return
        }
        def cantUser = Company.countByEmail(params.email)

        if(cantUser > 0) {
            def data =  [errors: "1", errorType: "2",companyId: null, messages: [[id:3 , value: message(code: 'user.email.taken', args: [params.email],default: "The Email {0} is already registered.")]] ]
            render data  as JSON
            return
        }

        def user = new Company()
        user.properties = params
        def userRole = SecRole.findByAuthority('ROLE_COMPANY')
        userService.sendCompanyInitMail(user.email,user.name,user.address,user.telephone,user.contactName)

        user.username = params.email
        //user.referer = params.referer ? params.referer : "NONE"
        //user.smartphone = params.smartphone ? params.smartphone : false
        //user.smartphoneBrand = params.smartphone ? params.smartphoneBrand : "NONE"
        //user.date = params.date ? params.date : "NONE"
        //user.phone = params.phone ? params.phone : "NONE"
        /*String charset = (('A'..'Z') + ('0'..'9')).join()
        Integer length = 18
        String randomString = RandomStringUtils.random(length, charset.toCharArray())
        user.password = randomString*/

        user.accountExpired = false
        user.enabled = true
        user.logo = "" //poner imagen de poner logo
        user.nit = "XXXXXXXX-X"

        if(user.save(flush: true))
        {
//            userService.sendWelcomeMail(user,randomString)  //Mandar correo a empresa
            SecUserSecRole.create user,userRole
            /*def referer = session.getAttribute("NEST5_REFERER")
            session.removeAttribute("NEST5_REFERER")
            if (referer)
            {
                def refCode =  RefCodes.findByCode(referer)
                if(refCode)
                {
                    def dummy = User.findByUsername("dummyNest5")
                    if(refCode.refered == dummy)
                    {
                        refCode.refered =  user
                        refCode.save(flush: true)
                        achievementsService.createAchievement("03_refe",refCode.referer)
                        //falta guardar un achievement al usuario que se registra con los 10 iniciales

                    }

                }
            }*/
            def data =  [errors: "0", errorType: "0",companyId: user.id, messages: [[id:4 , value: message(code: 'company.register.success', args: ["<h2>${user.name}</h2>"],default: "{0}, <br>Thank You!, Your brand's one step closer to getting ready with <b>Nest5</b><br>Continue with designing benefits for your customers: <br><br><button id='gosteptwo'>Go To Step 2</button>")] ] ]
            render data  as JSON
            return

        }
        else
        {
            def data =  [errors: "1", errorType: "1",companyId: null, messages: [id: "NA",value:  user.errors] ]
            render data  as JSON
            return
        }
    }

    def manualSave() {
        def companyInstance = new Company(params)
        if (!companyInstance.save(flush: true)) {
            render(view: "create", model: [companyInstance: companyInstance])
            return
        }

        def companyRole = SecRole.findByAuthority('ROLE_COMPANY') ?: new SecRole(authority: 'ROLE_COMPANY').save(failOnError: true)
        if (!companyInstance.authorities.contains(companyRole)) {
            SecUserSecRole.create companyInstance, companyRole
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'company.label', default: 'Company'), companyInstance.id])
        redirect(action: "show", id: companyInstance.id)
    }

//    @Secured(["ROLE_ADMIN"])
    def show() {
        def companyInstance = Company.get(params.id)
        if (!companyInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'company.label', default: 'Company'), params.id])
            redirect(action: "list")
            return
        }

        [companyInstance: companyInstance]
    }

//    @Secured(["ROLE_ADMIN"])
    def edit() {
        def companyInstance = Company.get(params.id)
        if (!companyInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'company.label', default: 'Company'), params.id])
            redirect(action: "list")
            return
        }

        [companyInstance: companyInstance]
    }

//    @Secured(["ROLE_ADMIN"])
    def update() {
        def companyInstance = Company.get(params.id)
        if (!companyInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'company.label', default: 'Company'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (companyInstance.version > version) {
                companyInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'company.label', default: 'Company')] as Object[],
                        "Another user has updated this Company while you were editing")
                render(view: "edit", model: [companyInstance: companyInstance])
                return
            }
        }

        companyInstance.properties = params

        if (!companyInstance.save(flush: true)) {
            render(view: "edit", model: [companyInstance: companyInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'company.label', default: 'Company'), companyInstance.id])
        redirect(action: "show", id: companyInstance.id)
    }

//    @Secured(["ROLE_ADMIN"])
    def delete() {
        def companyInstance = Company.get(params.id)
        if (!companyInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'company.label', default: 'Company'), params.id])
            redirect(action: "list")
            return
        }

        try {
            companyInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'company.label', default: 'Company'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'company.label', default: 'Company'), params.id])
            redirect(action: "show", id: params.id)
        }
    }

    @Secured(['ROLE_COMPANY','ROLE_ADMIN'])
    def panel(){
        def activity = params.activity
        def template
        def menu
        def promos
        def stores
        def clients
        def interactions
        def active
        def paused
        def inactive
        def promo
        def offers
        def clientDetails
        def userStats


        def user = springSecurityService.currentUser
        //def userInstance = springSecurityService.currentUser
        //def stats = userService.getStats(userInstance)
        //def complete = (userInstance.phone != "NONE") && (userInstance.name != "NONE") && (userInstance.email != "NONE") && (userInstance.date != "NONE") ?: false



        switch (activity)
        {
            /*case 'password': template = "password"
                menu = "settings"
                break
            case 'profile' : template = "profile"
                menu = "profile"
                break
            case 'details' : template = "details"
                menu = "settings"
                break
            case 'mobile' : template = "mobile"
                menu = "settings"
                break
            case 'help' : template = "help"
                menu = "help"
                break
            case 'achievements' : template = "achievements"
                menu = "profile"
                break
                */
            case 'displayPromo' : template = "promoQR"
                menu = "profile"
                promo = Promo.findById(params.promocode)
                offers = promo.offers
                break
            case 'stores' : template = "stores"
                menu = "profile"
                stores = user.managedStores
                break
            case 'promos' : template = "promos"
                promos = user.managedPromos.size() < 5 ?  user.managedPromos : user.lazyPromos(0,5)
//                promos =   user.lazyPromos(3,10)
                menu = "profile"
                break
            case 'addpromo' : template = "addPromo"
                //promos = user.managedPromos.size() < 5 ?  user.managedPromos : user.lazyPromos(0,5)
//                promos =   user.lazyPromos(3,10)
                menu = "profile"
                break
            case 'addstore' : template = "addStore"
                //promos = user.managedPromos.size() < 5 ?  user.managedPromos : user.lazyPromos(0,5)
//                promos =   user.lazyPromos(3,10)
                menu = "profile"
                break
            case 'writemessage' : template = "newMessage"
                //promos = user.managedPromos.size() < 5 ?  user.managedPromos : user.lazyPromos(0,5)
//                promos =   user.lazyPromos(3,10)
                menu = "profile"
                break
            case 'users' : template = "users"
                //promos = user.managedPromos.size() < 5 ?  user.managedPromos : user.lazyPromos(0,5)
//                promos =   user.lazyPromos(3,10)
                menu = "profile"
                clients = user.clients()
                clientDetails = []
                clients.each {cliente->
                     def sc = Stamp.createCriteria()
                     def stamps = sc.list{
                         eq('user',cliente)
                         eq('redeemed',false)
                         'in'('promo', user.managedPromos)

                     }
                    def cc = Coupon.createCriteria()
                    def coupons = cc.list{
                        eq('user',cliente)
                        'in'('promo', user.managedPromos)
                    }
                    clientDetails += [client: cliente,stamps: stamps,coupons:coupons]
                }

                break

            case 'stats' : template = "stats"
                //promos = user.managedPromos.size() < 5 ?  user.managedPromos : user.lazyPromos(0,5)
//                promos =   user.lazyPromos(3,10)
                 def c = Stamp.createCriteria()
                 def promociones = user.managedPromos
                def today = new Date()
                def lastMonth = today - 30
                 def lastMonthStamps = c.get{
                     'in'("promo",promociones)
                     gt("date",lastMonth)
                     projections{
                           countDistinct("user")
                     }

                 }
                def r = Relation.createCriteria()

                def firstUser = r.get {
                    eq('user',user)
                    order('firstTime','asc')
                    maxResults(1)
                }
                def rr = Relation.createCriteria()
                def lastUser = rr.get {
                    eq('user',user)
                    order('firstTime','desc')
                    maxResults(1)
                }
                //penetración de la promoción, en el total de usuarios de la empresa y en el total de usuarios de Nest5
                promos = user.managedPromos
                clients = user.clients
                def promoStats = []
                promos.each{actual->
                    def cc  = Stamp.createCriteria()
                    def numberUsers = cc.get{
                      eq('promo',actual)
                      projections{
                          countDistinct('user')
                      }
                    }
                    def nest5Total = User.count()
                    def numberClients = clients.size() != 0 ? clients.size() : 1
                    def pen1 = (Double) (numberUsers / numberClients)
                    def pen2 = (Double) (numberUsers / nest5Total)
                    promoStats += [promo: actual, companyPenetration: pen1, globalPenetration: pen2]

                }
               userStats = [referenceDate: lastMonth,lastMonthStamps: lastMonthStamps,firstUser:firstUser,lastUSer:lastUser,promoStats:promoStats]
                menu = "profile"
                break
            //
            default: template = "general"
                menu = "general"
                    promos = user.managedPromos
                    stores = user.managedStores
                    clients = user.clients()
                    interactions = []
                    active = []
                    paused = []
                    inactive = []
                    promos.each{
                        interactions += it.stamps
                    }
                def today = new Date()
                def lastWeek = today - 7
                def lastMonth = today - 30
               /* println today
                println lastWeek
                println lastMonth*/
                clients.each{cliente->
                    def c = Stamp.createCriteria()
                    def results
//                    println cliente
                    results = c.get {
                        eq("user",cliente)
                        gt("date",lastWeek)
                        maxResults(1)
                    }
//                    println results

                    if(results)
                    {
                        active += cliente
                    }
                    else{
                        c = Stamp.createCriteria()
                       results = c.get{
                           eq("user",cliente)
                           between("date", lastMonth,lastWeek)
                           maxResults(1)
                       }
                        if(results)
                        {
                            paused += cliente
                        }
                        else{
                            c = Stamp.createCriteria()
                            results = c.get{
                                eq("user",cliente)
                                lt("date", lastMonth)
                                maxResults(1)
                            }
                            if(results)
                            {
                                inactive += cliente
                            }
                        }
                        //
                    }

                }

                break
        }


        if (request.xhr) {
            render template: template, model: [user:user,promos:promos,stores:stores,clients:clients,interactions: interactions,active:active,paused:paused,inactive:inactive,promo: promo,offers:offers,clientDetails: clientDetails,userStats:userStats,picture: companyService.companyImageUrl(user)]
        } else {



            render view: "panel", model: [template: template,user: user,direct:'1',submenu: menu,promos:promos,stores:stores,clients: clients,interactions: interactions,active:active,paused:paused,inactive:inactive,promo: promo,offers: offers,clientDetails: clientDetails,userStats:userStats,picture: companyService.companyImageUrl(user)]
        }



        return



    }

    @Secured(["ROLE_COMPANY"])
    def demographics(){
        def company = springSecurityService.currentUser
        def promos = company.managedPromos

      [user: company,picture: companyService.companyImageUrl(company)]

    }
    @Secured(["ROLE_COMPANY"])
    def complementary(){
        def company = springSecurityService.currentUser
//        def promos = company.managedPromos

        [user: company,picture: companyService.companyImageUrl(company)]

    }
    @Secured(["ROLE_COMPANY"])
    def interactions(){
        def company = springSecurityService.currentUser
        def promos = company.managedPromos

        [user: company, picture: companyService.companyImageUrl(company)]

    }

    def firstInteraction(){
        if (request.xhr) {
            def company = Company.findByEmail(params.companyid)
            def promos = company.managedPromos
            def results = []
            promos.each{promocion->
                def newbies = []
                def s = Stamp.createCriteria()
                def users = s.list{
                    eq("promo",promocion)
//                    order("date","asc")
                    projections{
                        distinct("user")

                    }
                }
                    users.each{usuario->

                        def userStamps = usuario.stamps
                        def ss = Stamp.createCriteria()

                        def firstTimeInPromo = ss.get{
                            eq("promo",promocion)
                            eq("user",usuario)
                            order("date","asc")
                            maxResults(1)
                            projections {
                                property("date")
                            }
                        }

                        def firstTimer = true

                        for(def actual : userStamps){
                            if(actual.promo != promocion)
                            {
                               if (actual.date < firstTimeInPromo)
                               {
                                firstTimer = false
                                break

                               }
                            }
                        }
                        if(firstTimer)
                        {
                           newbies += [user: usuario, date: firstTimeInPromo]
                        }

                    }
                results += [promo: promocion,newbies: newbies]

                }

//




        render results as JSON
        return

        }

    }

    def showCompanies()
    {


        // println "Valor: "+parsingForAndroid
// osInfo = detail string inside first parantheses, i.e., for Android - "Linux; U; Android 2.3.3; en-gb; GT-I9100 Build/GINGERBREAD"
        /*if(parsingForAndroid) {
            (userAgentIndentService.operatingSystem, userAgentIndentService.sec, userAgentIndentService.platform, userAgentIndentService.language) = userAgentIndentService.osInfo.split("; ") as List
        } else {
            (userAgentIndentService.platform, userAgentIndentService.sec, userAgentIndentService.operatingSystem, userAgentIndentService.language) = userAgentIndentService.osInfo.split("; ") as List
        }*/

        def cc = Company.createCriteria()
        def companies = cc.list {
            maxResults 6
        }
        //def companies = Company.list();
        def resultSet = []

         companies.each{company->
                            resultSet += [company:[id:company.id,name: company.name],href: company.url,image: resource(dir: 'images/partners',file: 'La_Ostreria.jpg')]

                        }

         /*def companiess = [[company: [id:  1, name: "La Ostreria"],href: "http://www.laostreria.com.co",image: "${resource(dir: 'images/partners', file: 'La_Ostreria.jpg')}"],
                 [company: [id:  2, name: "Winners Burger To Share"],href: "http://www.laostreria.com.co",image: "${resource(dir: 'images/partners', file: 'logo_winners.png')}"],
                 [company: [id:  3, name: "Jawii"],href: "http://www.laostreria.com.co",image: "${resource(dir: 'images/partners', file: 'logoeltrio.png')}"],
                 [company: [id:  4, name: "SushiToGo"],href: "http://www.laostreria.com.co",image: "${resource(dir: 'images/partners', file: 'Goandenjoy.JPG')}"]]
*/
            render resultSet as JSON
        //render text: iphone+" "+android+" "+chrome
            return

    }


    @Secured(["ROLE_COMPANY"])
    def showClients()
    {
        def q = params.q
        def company = springSecurityService.currentUser
        def devolver
        if(!q)
        {
            devolver = []
            render devolver as JSON
            return
        }
        def clients = company.clients()
        def filtered = clients.grep{it.name.toLowerCase().contains(q.toLowerCase()) || it.toString().toLowerCase().contains(q.toLowerCase())}
        def items = []
        filtered.each{cliente->
               items += [value: cliente.id, name: cliente.name]

        }

        render items as JSON
        return


    }

    /*FUNCIONES API NEST5BUSINESS
    *
    *
    * */
     /*HACER LOGIN DESDE APP DE ANDROID NEST5BUSINESS  OCTUBRE 29 DE 2013 JUANDA*/
    def checkLogin(){
        println params
        def username = params.email?.trim()
        def pass = params.password?.trim()
        //println username+" "+pass
        def result
        if(!username || ! pass){
            result = [status: 0,id: 0, email: "", name: "",phone: "", username: ""]
            render result as JSON
            return
        }
        def user = Company.findByUsername(username);
        if(!user){
            result = [status: 0,id: 0, email: "", name: "",phone: "", username: ""]
            render result as JSON
            return
        }
        def password = springSecurityService.encodePassword(pass)
        if(password == user.password){
            result = [status: 1,id: user.id, email: user.email, name: user.name, username: user.username]
            render result as JSON
            return
        }
        result = [status: 0,id: 0, email: "", name: "",phone: "", username: ""]
        render result as JSON
        return

    }

    /*Guardar registro de backup de base de datos en amazon s3 octubre 29 de 2013 Juanda*/

    def saveDB(){
        def ruta = "https://s3.amazonaws.com/com.nest5.businessClient"
        def company = params.company?.trim()
        def filename = params.file?.trim()
        //println username+" "+pass
        def result
        if(!company || ! filename){
            result = [status: 0]
            render result as JSON
            return
        }
        def user = Company.findById(company as Long)
        if(!user){
            result = [status: 0]
            render result as JSON
            return
        }
        def file = new FileCompany(
               name: filename,
                tipo: "database",
                ruta: ruta+"/"+filename,
                description: new Date().toTimestamp().toString()
        )
        if(!file.save(flush: true)){
            result = [status: 0]
            render result as JSON
            return
        }
        def newmedia = new MediaCompany(
                file: file,
                company: user,
                isMain: false
        )
        if(!newmedia.save(flush: true)){
            println newmedia.errors.allErrors
            //
            result = [status: 0]
            render result as JSON
            return
        }
        result = [status: 1]
        //
        render result as JSON
        return

    }

}
