package com.nest5.Nest5Client

import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import org.apache.commons.lang.RandomStringUtils
import org.springframework.dao.DataIntegrityViolationException

class StoreController {
    def springSecurityService
    def userService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    @Secured(["ROLE_ADMIN"])
    def index() {
        redirect(action: "list", params: params)
    }

    @Secured(["ROLE_ADMIN"])
    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [storeInstanceList: Store.list(params), storeInstanceTotal: Store.count()]
    }

    @Secured(["ROLE_ADMIN"])
    def create() {
        [storeInstance: new Store(params)]
    }


    def save() {
        def fromWizzard = params.wizzard ? params.wizzard : true //saber si está guardando de una vez una oferta porque en el wizzard se hace así o si solo crea una tienda (viene este en false)

        def longi = new BigDecimal(params.longi)
        def lati = new BigDecimal(params.lati)
        def store = new Store()
        store.properties = params
        store.latitude = lati
        store.longitude = longi
        String charset = (('A'..'Z') + ('0'..'9')).join()
        Integer length = 18
        String randomString = RandomStringUtils.random(length, charset.toCharArray())
        store.code = randomString
        //println store.name
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
            store.company = company
        }
        if(store.save(flush: true))
        {
            //println params
           if(params.createdPromo)
               //
           {
               def promo = Promo.get(params.createdPromo)
               def offer = promo.addToStore(store)
               def compa = store.company
               userService.sendCompanyEndMail(compa.email,compa.name,compa.address,compa.telephone,compa.contactName)
           }

            //println offer
            def data =  [errors: "0", errorType: "0",storeId: store.id, messages: [[id:4 , value: message(code: 'store.create.success', args: [],default: "<br>That's It!, There's only one thing left to do with Nest5: Analize your data, youre customer's behavior, get better every day with what we tell you and make them fall in love with your brand by giving and communicating what they really expect from you.<br><br><button id='gotopanel'>Go To Step 4</button>")] ] ]
            render data  as JSON
            return

        }
        else
        {
            def data =  [errors: "1", errorType: "1",storeId: null, messages: [id: "NA",value:  store.errors] ]
            render data  as JSON
            return
        }
    }

    @Secured(["ROLE_ADMIN"])
    def show() {
        def storeInstance = Store.get(params.id)
        if (!storeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'store.label', default: 'Store'), params.id])
            redirect(action: "list")
            return
        }

        [storeInstance: storeInstance]
    }

    @Secured(["ROLE_ADMIN"])
    def edit() {
        def storeInstance = Store.get(params.id)
        if (!storeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'store.label', default: 'Store'), params.id])
            redirect(action: "list")
            return
        }

        [storeInstance: storeInstance]
    }

    @Secured(["ROLE_ADMIN"])
    def update() {
        def storeInstance = Store.get(params.id)
        if (!storeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'store.label', default: 'Store'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (storeInstance.version > version) {
                storeInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'store.label', default: 'Store')] as Object[],
                        "Another user has updated this Store while you were editing")
                render(view: "edit", model: [storeInstance: storeInstance])
                return
            }
        }

        storeInstance.properties = params

        if (!storeInstance.save(flush: true)) {
            render(view: "edit", model: [storeInstance: storeInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'store.label', default: 'Store'), storeInstance.id])
        redirect(action: "show", id: storeInstance.id)
    }

    @Secured(["ROLE_ADMIN","ROLE_COMPANY"])
    def delete() {
        def id = params.id as Integer
        println "aca"
        def storeInstance = Store.get(params.id)
        if (!storeInstance) {
            println "aca1"
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'store.label', default: 'Store'), params.id])
            //redirect(action: "list")
            def devolver = [status: 0, message: "No se enceuntra un local con ese id"]
            render devolver  as JSON
            return
        }
        println "aca2"
        def currentUser = springSecurityService.currentUser
        if(currentUser != storeInstance.company)
        {
            println "aca3"
            def devolver = [status: 0, message: "No tienes autorización para borrar este local."]
            render devolver  as JSON
            return
        }
        println "aca4"
        try {
            println "aca5"
            storeInstance.delete(flush: true)
//            flash.message = message(code: 'default.deleted.message', args: [message(code: 'store.label', default: 'Store'), params.id])
//            redirect(action: "list")
            def devolver = [status: 1, message: "Local eliminado con éxito",id: params.id]
            render devolver  as JSON
            return
        }
        catch (DataIntegrityViolationException e) {
            println "aca6"
            /*flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'store.label', default: 'Store'), params.id])
            redirect(action: "show", id: params.id)*/
            def devolver = [status: 0, message: "Se produjo un error eliminando el local seleccionado."]
            render devolver  as JSON
            return
        }
        println "aca7"
    }

    @Secured(["ROLE_COMPANY"])
    def countOffers(){
        if(request.xhr){
            def store = Store.get(params.storeid)
            def offers = store.offers
            def data =  [offers:offers]
            render data  as JSON
            return
        }

        else
        {
            response.sendError(400)
        }


    }

   /* def leadUser()
    {
        def store = params.store
        def storeInstance = Store.findByCode(store)
        if (!storeInstance)
        {
            params.referer = store
            redirect(uri: '/user/landing')
            return
        }
        def ip = request.getRemoteAddr()
        def date = new Date()
        def dummyUser = User.findByUsername("dummyNest5")

        def leadInstance = new Lead()
        leadInstance.store = storeInstance
        leadInstance.date = date
        leadInstance.ipAddress = ip
        leadInstance.user = dummyUser
        if (!leadInstance.save(flush: true))
        {
            params.referer = store
            redirect(uri: "/user/landing")
            return
        }
        session.setAttribute("STORE_REF",leadInstance.id)
        flash.cobranding = storeInstance.company.logo
        redirect(uri: '/user/store')
    }*/
}
