package com.nest5.Nest5Client

import org.springframework.dao.DataIntegrityViolationException

class FamilyController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [familyInstanceList: Family.list(params), familyInstanceTotal: Family.count()]
    }

    def create() {
        [familyInstance: new Family(params)]
    }

    def save() {
        def familyInstance = new Family(params)
        if (!familyInstance.save(flush: true)) {
            render(view: "create", model: [familyInstance: familyInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'family.label', default: 'Family'), familyInstance.id])
        redirect(action: "show", id: familyInstance.id)
    }

    def show(Long id) {
        def familyInstance = Family.get(id)
        if (!familyInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'family.label', default: 'Family'), id])
            redirect(action: "list")
            return
        }

        [familyInstance: familyInstance]
    }

    def edit(Long id) {
        def familyInstance = Family.get(id)
        if (!familyInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'family.label', default: 'Family'), id])
            redirect(action: "list")
            return
        }

        [familyInstance: familyInstance]
    }

    def update(Long id, Long version) {
        def familyInstance = Family.get(id)
        if (!familyInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'family.label', default: 'Family'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (familyInstance.version > version) {
                familyInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'family.label', default: 'Family')] as Object[],
                        "Another user has updated this Family while you were editing")
                render(view: "edit", model: [familyInstance: familyInstance])
                return
            }
        }

        familyInstance.properties = params

        if (!familyInstance.save(flush: true)) {
            render(view: "edit", model: [familyInstance: familyInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'family.label', default: 'Family'), familyInstance.id])
        redirect(action: "show", id: familyInstance.id)
    }

    def delete(Long id) {
        def familyInstance = Family.get(id)
        if (!familyInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'family.label', default: 'Family'), id])
            redirect(action: "list")
            return
        }

        try {
            familyInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'family.label', default: 'Family'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'family.label', default: 'Family'), id])
            redirect(action: "show", id: id)
        }
    }
}
