package com.nest5.Nest5Client

class ExtendedUser {

    static belongsTo = [user: User]
    static hasMany = [education: UserEducation, jobs: UserJob]

    String bio
    String gender
    Date birthDate
    String accessToken
    Boolean working
    Boolean educated
    Boolean completed
    String twitterUser
    String fqAccessToken
    String address
    City city
    BigDecimal latitude = 0
    BigDecimal longitude = 0



    static constraints = {
        bio()
        gender(inList: ["male","female","unknown"])
        latitude( scale : 16 )
        longitude( scale : 16 )

    }

    static mapping = {
        bio type: 'text'

    }
}
