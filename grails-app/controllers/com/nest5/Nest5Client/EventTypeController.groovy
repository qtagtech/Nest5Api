package com.nest5.Nest5Client

import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException

class EventTypeController {
    def springSecurityService
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    @Secured(["ROLE_ADMIN"])
    def index() {
        redirect(action: "list", params: params)
    }

    @Secured(["ROLE_ADMIN"])
    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [eventTypeInstanceList: EventType.list(params), eventTypeInstanceTotal: EventType.count()]
    }

    @Secured(["ROLE_ADMIN"])
    def create() {
        [eventTypeInstance: new EventType(params)]
    }

    @Secured(["ROLE_ADMIN"])
    def save() {
        def eventTypeInstance = new EventType(params)
        if (!eventTypeInstance.save(flush: true)) {
            render(view: "create", model: [eventTypeInstance: eventTypeInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'eventType.label', default: 'EventType'), eventTypeInstance.id])
        redirect(action: "show", id: eventTypeInstance.id)
    }

    @Secured(["ROLE_ADMIN"])
    def show(Long id) {
        def eventTypeInstance = EventType.get(id)
        if (!eventTypeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'eventType.label', default: 'EventType'), id])
            redirect(action: "list")
            return
        }

        [eventTypeInstance: eventTypeInstance]
    }

    @Secured(["ROLE_ADMIN"])
    def edit(Long id) {
        def eventTypeInstance = EventType.get(id)
        if (!eventTypeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'eventType.label', default: 'EventType'), id])
            redirect(action: "list")
            return
        }

        [eventTypeInstance: eventTypeInstance]
    }

    @Secured(["ROLE_ADMIN"])
    def update(Long id, Long version) {
        def eventTypeInstance = EventType.get(id)
        if (!eventTypeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'eventType.label', default: 'EventType'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (eventTypeInstance.version > version) {
                eventTypeInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'eventType.label', default: 'EventType')] as Object[],
                        "Another user has updated this EventType while you were editing")
                render(view: "edit", model: [eventTypeInstance: eventTypeInstance])
                return
            }
        }

        eventTypeInstance.properties = params

        if (!eventTypeInstance.save(flush: true)) {
            render(view: "edit", model: [eventTypeInstance: eventTypeInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'eventType.label', default: 'EventType'), eventTypeInstance.id])
        redirect(action: "show", id: eventTypeInstance.id)
    }

    @Secured(["ROLE_ADMIN"])
    def delete(Long id) {
        def eventTypeInstance = EventType.get(id)
        if (!eventTypeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'eventType.label', default: 'EventType'), id])
            redirect(action: "list")
            return
        }

        try {
            eventTypeInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'eventType.label', default: 'EventType'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'eventType.label', default: 'EventType'), id])
            redirect(action: "show", id: id)
        }
    }
}
