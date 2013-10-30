package com.nest5.Nest5Client

import org.springframework.dao.DataIntegrityViolationException

class UniverseController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [universeInstanceList: Universe.list(params), universeInstanceTotal: Universe.count()]
    }

    def create() {
        [universeInstance: new Universe(params)]
    }

    def save() {
        def universeInstance = new Universe(params)
        if (!universeInstance.save(flush: true)) {
            render(view: "create", model: [universeInstance: universeInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'universe.label', default: 'Universe'), universeInstance.id])
        redirect(action: "show", id: universeInstance.id)
    }

    def show(Long id) {
        def universeInstance = Universe.get(id)
        if (!universeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'universe.label', default: 'Universe'), id])
            redirect(action: "list")
            return
        }

        [universeInstance: universeInstance]
    }

    def edit(Long id) {
        def universeInstance = Universe.get(id)
        if (!universeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'universe.label', default: 'Universe'), id])
            redirect(action: "list")
            return
        }

        [universeInstance: universeInstance]
    }

    def update(Long id, Long version) {
        def universeInstance = Universe.get(id)
        if (!universeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'universe.label', default: 'Universe'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (universeInstance.version > version) {
                universeInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'universe.label', default: 'Universe')] as Object[],
                        "Another user has updated this Universe while you were editing")
                render(view: "edit", model: [universeInstance: universeInstance])
                return
            }
        }

        universeInstance.properties = params

        if (!universeInstance.save(flush: true)) {
            render(view: "edit", model: [universeInstance: universeInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'universe.label', default: 'Universe'), universeInstance.id])
        redirect(action: "show", id: universeInstance.id)
    }

    def delete(Long id) {
        def universeInstance = Universe.get(id)
        if (!universeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'universe.label', default: 'Universe'), id])
            redirect(action: "list")
            return
        }

        try {
            universeInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'universe.label', default: 'Universe'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'universe.label', default: 'Universe'), id])
            redirect(action: "show", id: id)
        }
    }
}
