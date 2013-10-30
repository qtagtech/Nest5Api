package com.nest5.Nest5Client

class  Relation {

    static belongsTo = [company:Company, user: User]

    Company company
    User user
    Date firstTime


    static Relation link(company, user) {
        def m = Relation.findByCompanyAndUser(company, user)
        if (!m)
        {
            m = new Relation()
            company?.addToClients(m)
            user?.addToBrands(m)
            m.firstTime = new Date()


            if(!m.save())
            {
                println m.errors.allErrors
            }


        }
        return m
    }

    static void unlink(company, user) {
        def m = Relation.findByCompanyAndUser(company, user)
        if (m)
        {
            company?.removeFromClients(m)
            user?.removeFromBrands(m)
            m.delete()
        }
    }

    static constraints = {
    }
}


