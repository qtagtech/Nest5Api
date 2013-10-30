package com.nest5.Nest5Client

class MediaCompany {
    static belongsTo = [company: Company, file: FileCompany]
    Boolean isMain
    static constraints = {
    }
}
