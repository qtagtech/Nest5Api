package Nest5Client

import com.nest5.Nest5Client.Company
import com.nest5.Nest5Client.Event
import com.nest5.Nest5Client.EventType
import com.nest5.Nest5Client.ExtendedUser
import com.nest5.Nest5Client.Invitation
import com.nest5.Nest5Client.Media
import com.nest5.Nest5Client.User




class UserService {
    def mailService
    def sendWelcomeMail(user,pass,type="NOSOCIAL",network = "NONE")
    {  if (type == "NOSOCIAL"){
        def link = getLink(user.email,user.date,user.name)
        mailService.sendMail {
            to user.email
            from "multimedia@nest5.com"
            subject "¡Estás a solo un clic de ganarte un Galaxy Note!"
            body (
                    view: "/welcome",
                    model: [user:user,password:pass,link:link]
            )
        }


    }
        if(type == "SOCIAL"){
            def link = "http://nest5.com/user/account?activity=password"
            mailService.sendMail {
                to user.email
                from "multimedia@nest5.com"
                subject "¡Estás a solo un clic de ganarte un Galaxy Note!"
                body (
                        view: "/welcomeSocial",
                        model: [user:user,password:pass,link:link,accountType: network]
                )
            }

        }

    }

    def sendInvitationMail(User user, String picture, String email, Invitation inv){

        mailService.sendMail {
            to email
            from "multimedia@nest5.com"
            subject "¡"+user.name.split(" ")[0]+" te ha invitado a Nest5!"
            body (
                    view: "/emails/invite",
                    model: [user:user, picture: picture,invitation: inv]
            )
        }
    }

    def sendContactMail(email)
    {

        mailService.sendMail {
            to 'marketing@nest5.com'
            from "contacto@nest5.com"
            subject "¡Alguien te necesita en Nest5!"
            body email+" Se ha comunicado desde la sección Regístrate en Nest5 pidiendo ayuda para inscribirse."
        }





    }

    def sendCompanyInitMail(email,name,address,telephone,contact)
    {

        mailService.sendMail {
            to 'marketing@nest5.com'
            from "contacto@nest5.com"
            subject "¡AyJueputa una empresa se inscribió!"
            body " Holi Cati :) la empresa "+name+" acaba de iniciar su proceso con los datos: <br />Email: "+email+"<br />Dirección: "+address+"<br />Teléfono: "+telephone+" <br />Contacto: "+contact+"<br />Chai :P"
        }





    }

    def sendCompanyEndMail(email,name,address,telephone,contact)
    {

        mailService.sendMail {
            to 'marketing@nest5.com'
            from "contacto@nest5.com"
            subject "¡Que bien mariquis, una empresa terminó el proceso.!"
            body " Holi Cati :) la empresa "+name+" acaba de finalizar su proceso con los datos: <br />Email: "+email+"<br />Dirección: "+address+"<br />Teléfono: "+telephone+" <br />Contacto: "+contact+"<br />Chai :P"
        }





    }


    def getLink(email,birth,name){
        def complete = birth+"_"+name
        def encrypted = complete.encodeAsMD5()+"::"+email
        def base64 = encrypted.encodeAsBase64()
        return "http://nest5.com/confirm/id/"+base64
    }

    /*******FEED METHODS******
     *
     *
     * **/

    def generateUserEvent(user1,user2){
        def addFriendEvent = EventType.findByCode("NEI004")
        def dummyCompany = Company.findByUsername('dummyCompany') ?: new Company(

                category: com.nest5.Nest5Client.Category.findByName('Ninguna') ?: new com.nest5.Nest5Client.Category(name: 'Ninguna', description: 'Sin clasificacion posible.').save(failOnError: true),
                username: 'dummyCompany',
                password: 'farroyavefami',
                enabled: true,
                name : "Dummy Company",
                address: "NA",
                telephone: "00",
                contactName: "NA",
                email: "NA@NA.com",
                nit: "0000",
                logo : " ",
                url : "http://").save(failOnError: true)
        def event = new Event(
                type: addFriendEvent,
                user: user1,
                otherBrand: false,
                otherUser: true,
                oUser: user2,
                company: dummyCompany,
                date: new Date(),
                stampCount: 0,
                store: "NA",
                couponCount: 0

        ).save(failOnError: true)
    }

    def userImageUrl(User user){
        def media = Media.findByUserAndIsMain(user,true)
        if(!media)    //if there's no profile image in Nest5, then search with facebook uid facebook's picture
        {

          def extended = ExtendedUser.findByUser(user)

            if(!extended)
                return 'https://s3-us-west-2.amazonaws.com/com.nest5.usermedia/mujer.jpg'
            if(user.uid == 0){      //there's no picture and it's not registered through facebook. Send default user pictire
              if(extended.gender == "female")
                  return 'https://s3-us-west-2.amazonaws.com/com.nest5.usermedia/mujer.jpg'
              else
                  return 'https://s3-us-west-2.amazonaws.com/com.nest5.usermedia/hombre.jpg'
            }
            else{
                return "http://graph.facebook.com/${user.uid}/picture?width=200&height=300"
            } //it's logged with facebook retunr user facebook profile pic

        }
        else{ //has picture uploades to nest5 as main, retun picture's url
          return media.file.ruta
        }
    }


    /*def getStats(user)
    {
        def ach = Achievements.findAllByUser(user)
        def points = 0
        def challenges = []
        ach.each {
              points += it.challenge.points
              challenges.add(it.challenge.id)
            
        }
        

        return [points: points, challenges: challenges,achievements: ach]


    }*/

    /*def saveAchievement(user,chall){
        def challenge = Challenge.findByCode(chall)
        def ach = Achievements.findByUserAndChallenge(user,challenge)
        if (!ach)
        {
            def newAch = new Achievements()
            def dateR = new Date()
            newAch.user = user
            newAch.challenge = challenge
            newAch.date = dateR
            newAch.save(flush: true)
        }

    }*/

    /*def setAchievement(userInstance,challenge){

        def listAch = Achievements.findByUserAndChallenge(userInstance,challenge)
        if ((listAch) && (challenge.onetimeonly))
        {
            def data =  [errors: "1", errorType: "2"]

            return data
        }
        def achievementsInstance = new Achievements()

        achievementsInstance.challenge = challenge

        def date = new Date();
        achievementsInstance.date = date
        if (!userInstance)
        {
            def data =  [errors: "1", errorType: "2"]

            return data
        }
        achievementsInstance.user = userInstance

        if (!achievementsInstance.save(flush: true)) {
            def data =  [errors: "1", errorType: "1"]

            return data
        }
        def data =  [errors: "0", errorType: "0"]

        return data
    }*/

    /*def Twitt(user,message)
    {
        def tUser = TwitterUser.findByUser(user)
        if(tUser)
        {
            def conf = SpringSecurityUtils.securityConfig
            def consumerKey = conf.twitter.app.consumerKey
            def consumerSecret = conf.twitter.app.consumerSecret
            Twitter twitter
            twitter = new TwitterFactory().getInstance()
            twitter.setOAuthConsumer(consumerKey, consumerSecret)
            AccessToken accessToken = new AccessToken(tUser.token, tUser.tokenSecret)
            twitter.setOAuthAccessToken(accessToken)
            twitter.updateStatus(message)
        }




    }*/

    /*def getPoints(user,type='GLOBAL')
    {
        def ach
        def cat
        def points = 0

           cat = ChallengeCategory.findByCode(type)
           def challenges = cat.challenge
           if (challenges){
               def c = Achievements.createCriteria()
               def achs = c.list{
                   eq("user",user)
                   'in'("challenge",challenges)
                   *//*projections{

                       count("challenge")
                   }*//*
               }
               achs.each {
                   points += it.challenge.points
               }
           }


           *//*cat.challenge.each {challenge->
               challenge.achievemnts.each {achi->
                   if(achi.user == user) {
                       points += challenge.points
                   }
               }

           }*//*



        return points

    }*/

    //def getAllUserPoints()



   /*def getAchCount(user,type)
    {
        def cant = 0
        def cat = ChallengeCategory.findByCode(type)
        def challenges = cat.challenge
        if(challenges){
            def c = Achievements.createCriteria()
            cant = c.get{
                eq("user",user)
                'in'("challenge",challenges)
                projections{

                    count("challenge")
                }
            }
        }

        return cant

    }*/

    /*def getCachedPoints(user,type){   //Gets all values grom the globalstats table updated every hour

        def userStats =  GlobalStats.findByUser(user)
        switch (type)
        {

            case 'START': return userStats.startPoints
                break;
            case 'SOCIAL': return userStats.socialPoints
                break;
            case 'GEEK': return userStats.geekPoints
            break;
            case 'ROMANTIC': return userStats.romanticPoints
                break;
            default: return userStats.totalPoints
            break


        }


    }*/

    /*def getCachedAchCount(user,type){  //Gets all values grom the globalstats table updated every hour
        def userStats =  GlobalStats.findByUser(user)
        switch (type)
        {

            case 'START': return userStats.startAch
                break;
            case 'SOCIAL': return userStats.socialAch
                break;
            case 'GEEK': return userStats.geekAch
                break;
            case 'ROMANTIC': return userStats.romanticAch
                break;
            default: return userStats.totalAch
                break


        }
    }*/

   /* def getAchByDate(user,days = 10)
    {
        def today = new Date()
//        println "hoy : "+today
        def sinceDate = today - days
//        println "Since : "+sinceDate
        def c = Achievements.createCriteria()

        def achs =  c.list {
            eq("user",user)
            ge("date", sinceDate)
        }
        def map = [:]
        def actualDate = sinceDate
        def lastDate = achs.last().date
//        println "Last : "+lastDate
        def cant = 0
        def i = 0

        while(actualDate.format('yyyy-MM-dd') <= lastDate.format('yyyy-MM-dd')){
//            println "actual : "+actualDate.format('yyyy-MM-dd')

          if(actualDate.format('yyyy-MM-dd') == achs[i].date.format('yyyy-MM-dd'))
          {
              cant++
              i++
              if(!(achs[i]))
              {
                  map[actualDate.format('yyyy-MM-dd')] = cant
                  break;
              }
          }

          else
          {

              map[actualDate.format('yyyy-MM-dd')] = cant
              actualDate = actualDate + 1
              cant = 0
          }


        }
        return map

    }*/

    /*def getPointsByDate(user,days){
        def today = new Date()
        def sinceDate = today - days
        def c = Achievements.createCriteria()

        def achs =  c.list {
            eq("user",user)
            ge("date", sinceDate)
        }
        def map = [:]
        def actualDate = achs[0].date
        def cant = 0
        achs.each{
            if(actualDate.format('yyyy-MM-dd') == it.date.format('yyyy-MM-dd'))
            {
                cant+= it.challenge.points
            }

            else
            {
                actualDate = it.date
                cant = it.challenge.points
            }
            map[it.date.format('yyyy-MM-dd')] = cant
        }
        return map
    }*/


}
