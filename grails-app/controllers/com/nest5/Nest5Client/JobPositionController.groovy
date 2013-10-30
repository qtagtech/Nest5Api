package com.nest5.Nest5Client

import org.springframework.dao.DataIntegrityViolationException

class JobPositionController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [jobPositionInstanceList: JobPosition.list(params), jobPositionInstanceTotal: JobPosition.count()]
    }

    def create() {
        [jobPositionInstance: new JobPosition(params)]
    }

    def save() {
        def jobPositionInstance = new JobPosition(params)
        if (!jobPositionInstance.save(flush: true)) {
            render(view: "create", model: [jobPositionInstance: jobPositionInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'jobPosition.label', default: 'JobPosition'), jobPositionInstance.id])
        redirect(action: "show", id: jobPositionInstance.id)
    }

    def show(Long id) {
        def jobPositionInstance = JobPosition.get(id)
        if (!jobPositionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'jobPosition.label', default: 'JobPosition'), id])
            redirect(action: "list")
            return
        }

        [jobPositionInstance: jobPositionInstance]
    }

    def edit(Long id) {
        def jobPositionInstance = JobPosition.get(id)
        if (!jobPositionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'jobPosition.label', default: 'JobPosition'), id])
            redirect(action: "list")
            return
        }

        [jobPositionInstance: jobPositionInstance]
    }

    def update(Long id, Long version) {
        def jobPositionInstance = JobPosition.get(id)
        if (!jobPositionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'jobPosition.label', default: 'JobPosition'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (jobPositionInstance.version > version) {
                jobPositionInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'jobPosition.label', default: 'JobPosition')] as Object[],
                        "Another user has updated this JobPosition while you were editing")
                render(view: "edit", model: [jobPositionInstance: jobPositionInstance])
                return
            }
        }

        jobPositionInstance.properties = params

        if (!jobPositionInstance.save(flush: true)) {
            render(view: "edit", model: [jobPositionInstance: jobPositionInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'jobPosition.label', default: 'JobPosition'), jobPositionInstance.id])
        redirect(action: "show", id: jobPositionInstance.id)
    }

    def delete(Long id) {
        def jobPositionInstance = JobPosition.get(id)
        if (!jobPositionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'jobPosition.label', default: 'JobPosition'), id])
            redirect(action: "list")
            return
        }

        try {
            jobPositionInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'jobPosition.label', default: 'JobPosition'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'jobPosition.label', default: 'JobPosition'), id])
            redirect(action: "show", id: id)
        }
    }
}
