package com.nest5.Nest5Client

class Message {

    static belongsTo = [company:Company, user: User]

    Company company
    User user
    Date time
    Boolean read
    String message


    static Message addLine(company, user,content) {
        def m = Message.findByCompanyAndUser(company, user)
        if (!m)
        {
            m = new Message()
            company?.addToChat(m)
            user?.addToChat(m)
            m.time = new Date()
            m.read = false


            if(!m.save())
            {
                println m.errors.allErrors
            }


        }
        return m
    }

    /*static void deleteChat(company, user) {
        def m = Message.findByCompanyAndUser(company, user)
        if (m)
        {
            company?.removeFromChat(m)
            user?.removeFromChat(m)
            m.delete()
        }
    }*/

    static constraints = {
        message type: 'text'
    }
}


