package com.nest5.Nest5Client

import org.springframework.dao.DataIntegrityViolationException

class IconController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [iconInstanceList: Icon.list(params), iconInstanceTotal: Icon.count()]
    }

    def create() {
        [iconInstance: new Icon(params)]
    }

    def save() {
        def iconInstance = new Icon(params)
        if (!iconInstance.save(flush: true)) {
            render(view: "create", model: [iconInstance: iconInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'icon.label', default: 'Icon'), iconInstance.id])
        redirect(action: "show", id: iconInstance.id)
    }

    def show(Long id) {
        def iconInstance = Icon.get(id)
        if (!iconInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'icon.label', default: 'Icon'), id])
            redirect(action: "list")
            return
        }

        [iconInstance: iconInstance]
    }

    def edit(Long id) {
        def iconInstance = Icon.get(id)
        if (!iconInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'icon.label', default: 'Icon'), id])
            redirect(action: "list")
            return
        }

        [iconInstance: iconInstance]
    }

    def update(Long id, Long version) {
        def iconInstance = Icon.get(id)
        if (!iconInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'icon.label', default: 'Icon'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (iconInstance.version > version) {
                iconInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'icon.label', default: 'Icon')] as Object[],
                        "Another user has updated this Icon while you were editing")
                render(view: "edit", model: [iconInstance: iconInstance])
                return
            }
        }

        iconInstance.properties = params

        if (!iconInstance.save(flush: true)) {
            render(view: "edit", model: [iconInstance: iconInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'icon.label', default: 'Icon'), iconInstance.id])
        redirect(action: "show", id: iconInstance.id)
    }

    def delete(Long id) {
        def iconInstance = Icon.get(id)
        if (!iconInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'icon.label', default: 'Icon'), id])
            redirect(action: "list")
            return
        }

        try {
            iconInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'icon.label', default: 'Icon'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'icon.label', default: 'Icon'), id])
            redirect(action: "show", id: id)
        }
    }
}
