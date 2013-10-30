package com.nest5.Nest5Client

class SkillType {

    static hasMany = [skills:Skill]
    String name
    String description


    static constraints = {
    }

    String toString()
    {
        name
    }
}
