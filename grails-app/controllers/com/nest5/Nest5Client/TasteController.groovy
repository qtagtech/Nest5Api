package com.nest5.Nest5Client

import grails.converters.JSON
import grails.plugins.springsecurity.Secured

class TasteController {
   def springSecurityService
    def userService
    def index() {}

    @Secured(["ROLE_USER","ROLE_ADMIN"])
    def userLike(){
        def user = springSecurityService.currentUser
        def result
        if(!user){
            result = [status: 0, message:"no existe el usuario",user: [id:null, name: null, picture: null], element: [id: null, name: null, type: null, picture: null]]
            render result as JSON
            return
        }
        def element
        switch(params.element){
            case "object": element = Object.findById(params.elementid)
                break
            case "category": element = Category.findById(params.elementid)
                break
            case "family": element = Family.findById(params.elementid)
                break
            case "universe": element = Universe.findById(params.elementid)
                break
        }
        if(!element){
            result = [status: 0, message:"no existe el elemento",user: [id:user.id, name: user.name, picture: userService.userImageUrl(user)], element: [id: null, name: null, type: null, picture: null]]
            render result as JSON
            return
        }
        def existing
        switch(params.element){
            case "object": existing = Taste.findByUserAndObject(user,element)
                break
            case "category":existing = Taste.findByUserAndCategory(user,element)
                break
            case "family": existing = Taste.findByUserAndFamily(user,element)
                break
            case "universe":existing = Taste.findByUserAndUniverse(user,element)
                break
        }
        if(existing){
            result = [status: 0, message:"El usuario ya había indicado que le gustaba este elemento",user: [id:user.id, name: user.name, picture: userService.userImageUrl(user)], element: [id: element.id, name: element.name, type: params.element, picture: element.icon?.ruta]]
            render result as JSON
            return
        }
        def taste = new Taste(
                user: user,
                date: new Date(),
                type: params.element
        )
        switch(params.element){
            case "object": taste.object = element
                break
            case "category": taste.category = element
                break
            case "family": taste.family = element
                break
            case "universe": taste.universe = element
                break
        }

        if(!taste.save(flush: true)){
            def errors =  taste.errors.allErrors.toString()
            result = [status: 0, message: errors,user: [id:user.id, name: user.name, picture: userService.userImageUrl(user)], element: [id: null, name: null, type: null, picture: null]]
            render result as JSON
            return

        }
        result = [status: 1, message:"Logrado con éxito",user: [id:user.id, name: user.name, picture: userService.userImageUrl(user)], element: [id: element.id, name: element.name, type: params.element, picture: element.icon?.ruta]]
        render result as JSON
        return


    }
    @Secured(["ROLE_USER"])
    def listUniverse(){
        def user = springSecurityService.currentUser
        def max = params.max ? params.max as Integer : 10
        def offset = params.offset ? params.offset as Integer : 0
        def lista = Universe.findAll([max: max, offset: offset])

        def result = [length: 0, elements: []]

        lista.each{actual->
            def taste = Taste.findByUserAndUniverse(user,actual)
            if(!taste){
                result.elements += [id: actual.id, name: actual.name, picture: actual.icon?.ruta, type: 'universe',description: actual?.description]
                result.length ++
            }

        }
        render result as JSON
        return
    }

    @Secured(["ROLE_USER"])
    def listFamily(){
        def user = springSecurityService.currentUser
        def max = params.max ? params.max as Integer : 10
        def offset = params.offset ? params.offset as Integer : 0
        def lista = Family.findAll([max: max, offset: offset])

        def result = [length: 0, elements: []]

        lista.each{actual->
            def taste = Taste.findByUserAndFamily(user,actual)
            if(!taste){
                result.elements += [id: actual.id, name: actual.name, picture: actual.icon?.ruta, type: 'family',description: actual?.description]
                result.length ++
            }

        }
        render result as JSON
        return
    }

    @Secured(["ROLE_USER"])
    def listCategory(){
        def user = springSecurityService.currentUser
        def max = params.max ? params.max as Integer : 10
        def offset = params.offset ? params.offset as Integer : 0
        def lista = Category.findAll([max: max, offset: offset])

        def result = [length: 0, elements: []]

        lista.each{actual->
            def taste = Taste.findByUserAndCategory(user,actual)
            if(!taste){
                result.elements += [id: actual.id, name: actual.name, picture: actual.icon?.ruta, type: 'category',description: actual?.description]
                result.length ++
            }

        }
        render result as JSON
        return
    }

    @Secured(["ROLE_USER"])
    def listObject(){
        def user = springSecurityService.currentUser
        def max = params.max ? params.max as Integer : 10
        def offset = params.offset ? params.offset as Integer : 0
        def lista = Object.findAll([max: max, offset: offset])
        def result = [length: 0, elements: []]

        lista.each{actual->
            def taste = Taste.findByUserAndObject(user,actual)
            if(!taste){
                result.elements += [id: actual.id, name: actual.name,description: actual.description, picture: actual.icon?.ruta, type: 'object',description: actual?.description]
                result.length ++
            }

        }
        render result as JSON
        return
    }
}
