package com.nest5.Nest5Client

class Member {
    static hasMany = [jobs: JobPosition, skills: Skill,studies: Education]
    String name
    String position
    String webaddress
    Date date
    String bio
    String address
    String email
    String twitter
    String linkedin
    String gplus
    String website


    static constraints = {
    }

    String toString()
    {
        name
    }
    static mapping = {
        bio type: 'text'
    }
}
