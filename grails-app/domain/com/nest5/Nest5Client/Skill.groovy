package com.nest5.Nest5Client

class Skill {

    static belongsTo = [member:Member,type: SkillType]

    String name
    int skillValue
    Member member

    static constraints = {
    }

    String toString()
    {
        name
    }
}
