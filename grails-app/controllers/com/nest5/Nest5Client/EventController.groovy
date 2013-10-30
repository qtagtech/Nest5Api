package com.nest5.Nest5Client

import grails.converters.JSON
import grails.plugins.springsecurity.Secured

class EventController {
    def springSecurityService
    def userService
    def utilityService
    def index() {}

    @Secured(["ROLE_USER","ROLE_ADMIN"])
    def comment(){
        def event = Event.findById(params.event as Long)
        def user = springSecurityService.currentUser
        def content = params.content
        def comment = new Comment(
                user: user,
                event: event,
                date: new Date(),
                content: content
        ).save(failOnError: true)

        def result = [status: 1, event: [id: event.id], id: comment.id, date: utilityService.timeFrom(comment.date), content: comment.content, user: [id: user.id, name: user.name, picture: userService.userImageUrl(user)]]
        render result as JSON
        return
    }

    @Secured(["ROLE_USER","ROLE_ADMIN"])
    def heart(){
        def event = Event.findById(params.event as Long)
        def user = springSecurityService.currentUser
        def heart = new Heart(
                user: user,
                event: event,
                date: new Date()
        )
        def result
        if(!heart.save(flush: true))
            result = [status: 0, event: [id: event.id], id: null, date: null, user: [id: user.id, name: user.name, picture: userService.userImageUrl(user)]]
        else
            result = [status: 1, event: [id: event.id], id: heart.id, date: heart.date, user: [id: user.id, name: user.name, picture: userService.userImageUrl(user)]]

        render result as JSON
        return
    }
    @Secured(["ROLE_USER","ROLE_ADMIN"])
    def unheart(){
        def event = Event.findById(params.event as Long)
        def user = springSecurityService.currentUser
        def heart = Heart.findByEventAndUser(event, user)
        def result
        if(!heart){
            result = [status: 0, event: [id: event.id], id: null, date: null, user: [id: user.id, name: user.name, picture: userService.userImageUrl(user)]]
            render result as JSON
            return
        }
        if(!heart.delete()){
            result = [status: 0, event: [id: event.id], id: null, date: null, user: [id: user.id, name: user.name, picture: userService.userImageUrl(user)]]
            render result as JSON
            return
        }

            result = [status: 1, event: [id: event.id], user: [id: user.id, name: user.name, picture: userService.userImageUrl(user)]]


    }


}
