package com.nest5.Nest5Client

import org.springframework.dao.DataIntegrityViolationException

class ObjectController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [objectInstanceList: Object.list(params), objectInstanceTotal: Object.count()]
    }

    def create() {
        [objectInstance: new Object(params)]
    }

    def save() {
        def objectInstance = new Object(params)
        if (!objectInstance.save(flush: true)) {
            render(view: "create", model: [objectInstance: objectInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'object.label', default: 'Object'), objectInstance.id])
        redirect(action: "show", id: objectInstance.id)
    }

    def show(Long id) {
        def objectInstance = Object.get(id)
        if (!objectInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'object.label', default: 'Object'), id])
            redirect(action: "list")
            return
        }

        [objectInstance: objectInstance]
    }

    def edit(Long id) {
        def objectInstance = Object.get(id)
        if (!objectInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'object.label', default: 'Object'), id])
            redirect(action: "list")
            return
        }

        [objectInstance: objectInstance]
    }

    def update(Long id, Long version) {
        def objectInstance = Object.get(id)
        if (!objectInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'object.label', default: 'Object'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (objectInstance.version > version) {
                objectInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'object.label', default: 'Object')] as Object[],
                        "Another user has updated this Object while you were editing")
                render(view: "edit", model: [objectInstance: objectInstance])
                return
            }
        }

        objectInstance.properties = params

        if (!objectInstance.save(flush: true)) {
            render(view: "edit", model: [objectInstance: objectInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'object.label', default: 'Object'), objectInstance.id])
        redirect(action: "show", id: objectInstance.id)
    }

    def delete(Long id) {
        def objectInstance = Object.get(id)
        if (!objectInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'object.label', default: 'Object'), id])
            redirect(action: "list")
            return
        }

        try {
            objectInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'object.label', default: 'Object'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'object.label', default: 'Object'), id])
            redirect(action: "show", id: id)
        }
    }
}
