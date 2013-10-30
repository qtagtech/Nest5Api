package com.nest5.Nest5Client

import org.springframework.dao.DataIntegrityViolationException

class SkillTypeController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [skillTypeInstanceList: SkillType.list(params), skillTypeInstanceTotal: SkillType.count()]
    }

    def create() {
        [skillTypeInstance: new SkillType(params)]
    }

    def save() {
        def skillTypeInstance = new SkillType(params)
        if (!skillTypeInstance.save(flush: true)) {
            render(view: "create", model: [skillTypeInstance: skillTypeInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'skillType.label', default: 'SkillType'), skillTypeInstance.id])
        redirect(action: "show", id: skillTypeInstance.id)
    }

    def show(Long id) {
        def skillTypeInstance = SkillType.get(id)
        if (!skillTypeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'skillType.label', default: 'SkillType'), id])
            redirect(action: "list")
            return
        }

        [skillTypeInstance: skillTypeInstance]
    }

    def edit(Long id) {
        def skillTypeInstance = SkillType.get(id)
        if (!skillTypeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'skillType.label', default: 'SkillType'), id])
            redirect(action: "list")
            return
        }

        [skillTypeInstance: skillTypeInstance]
    }

    def update(Long id, Long version) {
        def skillTypeInstance = SkillType.get(id)
        if (!skillTypeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'skillType.label', default: 'SkillType'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (skillTypeInstance.version > version) {
                skillTypeInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'skillType.label', default: 'SkillType')] as Object[],
                        "Another user has updated this SkillType while you were editing")
                render(view: "edit", model: [skillTypeInstance: skillTypeInstance])
                return
            }
        }

        skillTypeInstance.properties = params

        if (!skillTypeInstance.save(flush: true)) {
            render(view: "edit", model: [skillTypeInstance: skillTypeInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'skillType.label', default: 'SkillType'), skillTypeInstance.id])
        redirect(action: "show", id: skillTypeInstance.id)
    }

    def delete(Long id) {
        def skillTypeInstance = SkillType.get(id)
        if (!skillTypeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'skillType.label', default: 'SkillType'), id])
            redirect(action: "list")
            return
        }

        try {
            skillTypeInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'skillType.label', default: 'SkillType'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'skillType.label', default: 'SkillType'), id])
            redirect(action: "show", id: id)
        }
    }
}
