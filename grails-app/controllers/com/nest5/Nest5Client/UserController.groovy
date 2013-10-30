package com.nest5.Nest5Client

import grails.converters.JSON
import org.apache.commons.codec.binary.Base64
import org.apache.commons.lang.RandomStringUtils
import org.codehaus.groovy.grails.plugins.qrcode.QRCodeRenderer
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import grails.plugins.springsecurity.Secured



//import com.nest5.Nest5Client.ChallengeCategory
class UserController {
    def springSecurityService
    def userService
    def achievementsService
    def globalStatsService
    def utilityService
    def companyService
    def user



    /*
    *
    * Actions para API Android
    *
    *
    * */

    @Secured(["ROLE_API"])
    def newAndroidUser()
    {
        def email = params.email
        def userReturn = null

        if (email){
            // println(params)
            def cantUser = User.countByEmail(params.email)

            if(cantUser > 0) {
                def user = User.findByEmail(email)
                user.androidID = params.android
                user.iphoneID = "0"
                user.rimID = "0"
               if(!user.save(flush: true))
               {
                   def data =  [status: 0,uid: 0,userInstance : userReturn, errors: "1", errorType: "1", messages: [id: "NA",value:  user.errors] ]
                   log.info user.errors
                   render data  as JSON
                   return
               }
                userReturn = [id: user.id, date: user.date, email: user.email, name: user.name,phone: user.phone, username: user.username]
                def data =  [status: 1, uid: user.id,userInstance : userReturn, errors: "0", errorType: "0", messages: [[id: 12, value: message(code: 'mobilephone.connected', args: [],default: "Hemos actualizado tu cuenta y viculado tu teléfono móvil.")]] ]
                render data  as JSON
                return
            }

            def user = new User()
            user.properties = params
            def userRole = SecRole.findByAuthority('ROLE_USER')


            user.username = params.email
            user.referer = params.referer ? params.referer : "NONE"
            user.smartphone = params.smartphone ? params.smartphone : false
            user.smartphoneBrand = params.smartphone ? params.smartphoneBrand : "NONE"
            user.date = params.date ? params.date : "NONE"
            user.phone = params.phone ? params.phone : "NONE"
            String charset = (('A'..'Z') + ('0'..'9')).join()
            Integer length = 18
            String randomString = RandomStringUtils.random(length, charset.toCharArray())
            user.password = randomString
            user.name = "NONE"
            user.androidID = params.android
            user.enabled = true
            user.iphoneID = "0"
            user.rimID = "0"

            if(user.save(flush: true))
            {
                // userService.sendWelcomeMail(user,randomString)
                SecUserSecRole.create user,userRole
                def data =  [status: 2,uid: user.id,userInstance : userReturn, errors: "0", errorType: "0", messages: [[id:4 , value: message(code: 'user.register.success', args: ["<h2>${(user.name.split(" "))[0]}</h2>","<strong>${params.email}</strong>"],default: "{0}, \nThank You!, we have sent you an email to {1} with your access password for you to start with The Nest5 Experience. \nAs for now, why don't you take a look at what your favorite Brands and Stores are doing to make you fall in love with them.")] ] ]
                render data  as JSON
                return

            }
            else
            {
                def data =  [status: 0,uid: 0, errors: "1", errorType: "1", messages: [id: "NA",value:  user.errors] ]
                render data  as JSON
                return
            }

        }
        else{

        }


    }

    @Secured(["ROLE_API"])
    def updateAndroidUser(){

        def user = User.findById(params.userid)
        if (!user)
        {
            def data =  [status: 0,uid: 0, errors: "1", errorType: "2", messages: [id: "NA",value:  "Hay errores con el usuario y el API"] ]
            render data  as JSON
            return
        }

        user.properties = params

        if (!user.save(flush: true)){
            def data =  [status: 0,uid: 0, errors: "1", errorType: "1", messages: [id: "NA",value:  user.errors] ]
            render data  as JSON
            return
        }

        def data =  [status: 1, uid: user.id, errors: "0", errorType: "0", messages: [[id: 12, value: message(code: 'mobilephone.connected', args: [],default: "Hemos actualizado tu cuenta y viculado tu teléfono móvil.")]] ]
        render data  as JSON
        return


    }
    @Secured(["ROLE_API"])
    def requestAndroidUser(){
        def user = User.findById(params.userid)
        if (user)
        {
            def  userReturn = [id: user.id, date: user.date, email: user.email, name: user.name,phone: user.phone, username: user.username]
            render userReturn  as JSON
            return
        }
    }

    @Secured(["ROLE_API"])
    def requestUserCoupons(){
        def userId = params.id


           //

        def userInstance = User.findById(userId)
        if(!userId)
        {
            def data =  [status: 0,uid: 0, errors: "1", errorType: "0", messages: [id: "NA",value:  "¡No existe el usuario!"] ]
            render data  as JSON
            return
        }

        def actualDate = new Date()

        def c = Coupon.createCriteria()
        def coupons = c.list{
                      eq("user",userInstance)
                      eq("redeemed",0)
                      gt("validThru",actualDate)
                }


        def payload = []

          coupons.each{
              def act = [id:it.id,company: [id: it.promo.company.id,name:it.promo.company.name],imagen : resource(dir: 'images', file:'logo.png'),promocion: [id: it.promo.id,action: it.promo.activity,reqQTY: it.promo.cantArt,perkQTY: 1,requirement: it.promo.article,perk: it.promo.wins],coupon: it]
              payload += act

          }

        render payload as JSON
    }

    //Funciones iPhone y XPlatform
     @Secured(["ROLE_API"])
    def loginUser()
    {
        def user = User.findByEmail(params.userid)
        def pass = params.pass
        if ((user) && (user.password == springSecurityService.encodePassword(pass)))
        {
            def  userReturn = [status: 1,id: user.id, date: user.date, email: user.email, name: user.name,phone: user.phone, username: user.username]
            render userReturn  as JSON
            return
        }
        else
        {
            def  userReturn = [pas: springSecurityService.encodePassword(pass),status: 0,id: null, date: null, email: null, name: null,phone: null, username: null]
            render userReturn  as JSON
            return
        }
    }


    /*CHECKLOGIN FROM ANDROID APP, USING API CREDENTIALS AND USERNAME AND PASSWROD  -- JULY 16 2013*/

//    @Secured(["ROLE_API"])
    def checkLogin(){

        def username = params.username.trim()
        def pass = params.pass.trim()
        println username+" "+pass
        def result
        if(!username || ! pass){
            result = [status: 0,id: 0, date: "", email: "", name: "",phone: "", username: ""]
            render result as JSON
            return
        }
        def user = User.findByUsername(username);
        if(!user){
            result = [status: 0,id: 0, date: "", email: "", name: "",phone: "", username: ""]
            render result as JSON
            return
        }
        def password = springSecurityService.encodePassword(pass)
        if(password == user.password){
          result = [status: 1,id: user.id, date: user.date, email: user.email, name: user.name,phone: user.phone, username: user.username]
            render result as JSON
            return
        }
        result = [status: 0,id: 0, date: "", email: "", name: "",phone: "", username: ""]
        render result as JSON


        return

    } /*Register FROM ANDROID APP, USING API CREDENTIALS AND USERNAME AND PASSWROD  -- JULY 16 2013*/
    def registerUser(){
        def name = params.name.trim()
        def email = params.email.trim()
        def pass = params.pass.trim()
        def address = params.address.trim()
        def city = params.city.trim()
        def terms = params.terms.trim() as Integer
        def result
        if( terms == 0){
            result = [status: 0,id: 0, date: "", email: "", name: "",phone: "", username: "",message:  'No has aceptado los terminos y condiciones.']
            render result as JSON
            return
        }
        def existing = User.findByEmailOrUsername(email,email)
        if(existing){
            result = [status: 0,id: 0, date: "", email: "", name: "",phone: "", username: "",message:  'El email que indicaste ya se encuentra registrado en Nest5.']
            render result as JSON
            return
        }

        def user = new User(
                name: name,
                email: email,
                username: email,
                password: pass,
                address: address,
                enabled: true,
                accountLocked: false,
                accountExpired: false,
                passwordExpired: false,
                date:  " ",
                smartphone: true,
                phone:  "00000000",
                referer:  "none"
        )

        if(!user.save(flush: true)){
            println user.errors.allErrors
            result = [status: 0,id: 0, date: "", email: "", name: "",phone: "", username: "",message:  'Errores en el registro, intentalo de nuevo.']
            render result as JSON
            return
        }
        def longi = params.lat ? new BigDecimal(params.lat) : new BigDecimal(0)
        def lati =  params.lng ? new BigDecimal(params.lng) :  new BigDecimal(0)
        def cityF = City.findByCode(city) ?: City.findByCode("unknown")
        def extended = new ExtendedUser(
                user: user,
                bio: "",
                gender: "unknown",
                birthDate: new Date(),
                accessToken: "0",
                working: false,
                educated: false,
                completed: false,
                twitterUser: "",
                fqAccessToken: "0",
                address: address ?: "",
                city: cityF,
                latitude: lati,
                longitude: longi
        )
        if(!extended.save(flush: true)){
            println extended.errors.allErrors
            result = [status: 0,id: 0, date: "", email: "", name: "",phone: "", username: "",message:  'Errores en el registro, intentalo de nuevo.']
            render result as JSON
            return
        }

        def userRole = SecRole.findByAuthority('ROLE_USER') ?: new SecRole(authority: 'ROLE_USER').save(failOnError: true)
        def facebookRole = SecRole.findByAuthority('ROLE_FACEBOOK') ?: new SecRole(authority: 'ROLE_FACEBOOK').save(failOnError: true)
        SecUserSecRole.create user, userRole
//        println name+" "+email+" "+pass+" "+address+" "+city+" "+terms
        result = [status: 1,id: user.id, date: extended.birthDate, email: user.email, name: user.name,phone: user.phone, username: user.username]
        render result as JSON
        return
    }

    def checkFacebookUser(){
//        println params
        def result
        def email = params.email
        def location =  params.location ?: 'medellin' //Medellín, Antioquia, Medellín
        def gender = params.gender ?: "unknown"
        def minus = location.toLowerCase() //medellín
        minus = minus.replaceAll( /([àáâãäå])/, 'a' )
        minus = minus.replaceAll( /([èéêë])/, 'e' )
        minus = minus.replaceAll( /([ìíîï])/, 'i' ) //medellin
        minus = minus.replaceAll( /([òóôõö])/, 'o' )
        minus = minus.replaceAll( /([ùúûü])/, 'u' )
        minus = minus.replaceAll( /([ñ])/, 'n' )
        minus = minus.replaceAll( /([ýÿ])/, 'y' )
        minus = minus.replaceAll( /([ç])/, 'c' )
        minus = minus.replaceAll( /([^a-zA-Z0-9\ ])/, '_' )
        minus = minus.replaceAll( /([\ ])/, '-' )
        def birthday = params.birthday ? Date.parse('MM/dd/yyyy',params.birthday) : new Date()
        def user = User.findByEmail(email)
        if(user){
            def extended = ExtendedUser.findByUser(user)
            if(extended){
                extended.birthDate = birthday
                extended.city = City.findByCode(minus) ?: City.findByCode("unknown")
                if(extended.gender != gender && gender != "unknown")
                    extended.gender = gender
                extended.save(flush: true)
            }
            if(user.uid != params.uid as BigInteger){
                user.uid = params.uid as BigInteger
                user.save(flush:true)
            }
            result = [status: 1, id: user.id, email: user.email, phone:  user.phone, username: user.username, message: "usuario existente actualizado con exito"]
            render result as JSON
            return

        }
        //no existe el usuario, registrarlo
        user = new User(
                username: params.username?: email,
                password: params.utoken,
                enabled: true,
                accountExpired: false,
                passwordExpired: false,
                accountLocked: false,
                name : params.name,
                email: email,
                uid: params.uid as BigInteger,
                date: params.birthday ?: "00/00/0000",
                referer: " ",
                phone: " "

        )
        if(!user.save(flush: true, failOnError: true)){
            println user.errors.allErrors
            result = [status: 0,id: "", date: "", email: "", name: "",phone: "", username: "", message: "errores guardando el usuario"]

            render result as JSON
            return
        }
        def extendedUser = new ExtendedUser(
                user: user,
                bio: " ",
                gender: gender,
                birthDate: birthday,
                accessToken: params.utoken,
                working: false,
                educated: false,
                completed: false,
                twitterUser: "",
                fqAccessToken: "0",
                address: "",
                city: City.findByCode(minus) ?: City.findByCode("unknown"),
                latitude: new BigDecimal(0),
                longitude: new BigDecimal(0)

        )
        if(!extendedUser.save(flush: true, failOnError: true)){
            println extendedUser.errors.allErrors
            result = [status: 0,id: "", date: "", email: "", name: "",phone: "", username: "", message: "errores guardando el usuario extendido"]

            render result as JSON
            return
        }
        def userRole = SecRole.findByAuthority('ROLE_USER') ?: new SecRole(authority: 'ROLE_USER').save(failOnError: true)
        def facebookRole = SecRole.findByAuthority('ROLE_FACEBOOK') ?: new SecRole(authority: 'ROLE_FACEBOOK').save(failOnError: true)
        SecUserSecRole.create user, userRole
        result = [status: 1, id: user.id, email: user.email, phone:  user.phone, username: user.username, message: "usuario creado con exito"]
        render result as JSON
        return

    }

    @Secured(["ROLE_API"])
    def newiPhoneUser()
    {
        def email = params.email
        def userReturn = null

        if (email){
            // println(params)
            def cantUser = User.countByEmail(params.email)

            if(cantUser > 0) {
                def user = User.findByEmail(email)
                user.properties = params
                user.username = params.email

                if(!user.save(flush: true))
                {
                    def data =  [status: 0,uid: 0,userInstance : userReturn, errors: "1", errorType: "1", messages: [id: "NA",value:  user.errors] ]
                    log.info user.errors
                    render data  as JSON
                    return
                }
                userReturn = [id: user.id, date: user.date, email: user.email, name: user.name,phone: user.phone, username: user.username]
                def data =  [status: 1, uid: user.id,userInstance : userReturn, errors: "0", errorType: "0", messages: [[id: 12, value: message(code: 'mobilephone.connected', args: [],default: "Hemos actualizado tu cuenta y viculado tu teléfono móvil.")]] ]
                render data  as JSON
                return
            }

            def user = new User()
            user.properties = params

            def userRole = SecRole.findByAuthority('ROLE_USER')


            user.username = params.email
            user.referer = params.referer ? params.referer : "NONE"
            //user.smartphone = params.smartphone ? params.smartphone : false
            //user.smartphoneBrand = params.smartphone ? params.smartphoneBrand : "NONE"
            //user.date = params.date ? params.date : "NONE"
            user.phone = params.phone ? params.phone : "NONE"
            String charset = (('A'..'Z') + ('0'..'9')).join()
            if(params.isManual != "1")
            {
                Integer length = 18
                String randomString = RandomStringUtils.random(length, charset.toCharArray())
                user.password = randomString
            }

            //user.name = "NONE"
            user.androidID = "0"
            //user.iphoneID = params.iphone
            user.rimID = "0"
            user.enabled = true
            //user.iphoneID = "0"
            //user.rimID = "0"

            if(user.save(flush: true))
            {
                // userService.sendWelcomeMail(user,randomString)
                SecUserSecRole.create user,userRole
                userReturn = [id: user.id, date: user.date, email: user.email, name: user.name,phone: user.phone, username: user.username]
                def data =  [status: 2,uid: user.id,userInstance : userReturn, errors: "0", errorType: "0", messages: [[id:4 , value: message(code: 'user.register.success', args: ["<h2>${(user.name.split(" "))[0]}</h2>","<strong>${params.email}</strong>"],default: "{0}, \nThank You!, we have sent you an email to {1} with your access password for you to start with The Nest5 Experience. \nAs for now, why don't you take a look at what your favorite Brands and Stores are doing to make you fall in love with them.")] ] ]
                render data  as JSON
                return

            }
            else
            {
                def data =  [status: 0,uid: 0, errors: "1", errorType: "1", messages: [id: "NA",value:  user.errors] ]
                render data  as JSON
                return
            }

        }
        else{
            def data =  [status: 0,uid: 0, errors: "1", errorType: "1", messages: [id: "NA",value:  "No se puso un email valido"] ]
            render data  as JSON
            return
        }









    }


    /********************************************************
     *TODAS LAS ACCIONES QUE TIENEN QUE VER CON AMIGOS Y FEED
     ********************************************************/


    /*----------------------------------------------
   *
   *
   *
   *               addFriend ACTION - MAY 19TH 2013 - Juanda
   *               Permite que desde app movil con api o desde web un usuario loggeado agregue a otro usando un id
   *
   *
   *
   * ------------------------------------------------*/

//    @Secured(["ROLE_API","ROLE_USER"])
    def addFriend(){
        def userRole = SecRole.findByAuthority('ROLE_USER')
        def actual = User.get(springSecurityService.currentUser.id)?.authorities?.contains(userRole) ? springSecurityService.currentUser : User.get(params.uid as Long)
        def friend = User.get(params.friendid as Long)
        def result
        if (!actual)
        {
            result = [status:  0, friend: '', user: '',messages: 'No existe un usuario actual']
            render result as JSON
            return
        }


        if (!friend)
        {
            result = [status:  0, friend: '', user: '',messages: 'No existe el usuario con id: '+params.friendid]
            render result as JSON
            return
        }

        actual.addToFriend(friend)

        //Generate Event of EventType NEI004 - Add Friend
        userService.generateUserEvent(actual,friend)

        result = [status:  1, friend: friend, user: friend,messages: 'Ahora sigues a: '+friend.username]
        render result as JSON
        return

    }

    /*----------------------------------------------
   *
   *
   *
   *              CHECK FRIENDHIP BETWEEN 2 USERS - JUNE 24 2013 JUANDA
   *
   *
   *
   * ------------------------------------------------*/


    def checkFriendship(){
      def result = []
        //possible friendship values : 0--> no, 1--> user1 follows user2, 2--> user1 is followed by user2, 3--> user1 follows user2 and user1 is followed by user2
      def user1 = User.findById(params.user1 as Long)
      def user2 = User.findById(params.user2 as Long)
        if(!user1 || !user2){
          result = [status: 0, friendship: 0]  //friendship : 0 es que nadie sigue a nadie
        }
        def f1 = Friendship.findByUser1AndUser2(user1,user2) ? true : false
        def f2 = Friendship.findByUser1AndUser2(user2,user1) ? true : false

      if(f1 && f2){// el usuario 2 es seguido por el usuario 1
        result = [status: 1,friendship: 3]
      }
        else if(f1 && !f2){
            result = [status: 1,friendship: 1]
        }
        else if(!f1 && f2){
            result = [status: 1,friendship: 2]
        }
        else{
            result = [status: 1,friendship: 0]
        }
        render result as JSON
        return
        //
    }

    /*----------------------------------------------
    *
    *
    *
    *               FEED ACTION - MAY 19TH 2013 - Juanda
    *
    *
    *
    * ------------------------------------------------*/



    @Secured(["ROLE_USER"])
    def feed(){
       def user = springSecurityService.currentUser
        if(!user) return
       def extended = ExtendedUser.findByUser(user)
       if(!extended?.completed)
           redirect(uri: '/user/details', params: [userInstance: user, userPicture: userService.userImageUrl(user), extended: extended])
        [userInstance: user, vUser: user, userPicture: userService.userImageUrl(user)]
    }

   /* @Secured(["ROLE_USER"])
    def syncFriends(){
        def user = springSecurityService.currentUser
        if(!user) return
        def facebook = FacebookUser.findByUser(user)
        def dummy = User.findByUsername('dummyNest5')
        def existing = []
        def invite = []
        if(facebook){
            def http = new HTTPBuilder('https://graph.facebook.com')
            def json = http.get( path : '/me/friends', query : [access_token: facebook?.accessToken] )
            def qty =  json?.data?.size()
            def friends =  json?.data


            friends.each{

                def actual= User.findByUid(it.id as Long)
                if(actual) {
                    def areFriends = (Friendship.findByUser1AndUser2(user,actual) || Friendship.findByUser1AndUser2(actual,user))
                    if (!areFriends)
                        existing += actual
                }
                else{

                    invite += [id: it.id, name: it.name]
                }


            }
        }


        def imfriend = user.isFriendOf
        def followers = []

        imfriend.each{
          def already = Friendship.findByUser1AndUser2(user,it.user1)
            if ((!already) && (it.user1 != user) && (it.user1 != dummy))
                followers += [id: it.user1.id, name: it.user1.name, picture: userService.userImageUrl(it.user1)]
        }

        def followedByFriends = []
        def mine = user.friends
        def iFollow = mine.take(mine.size() > 3 ? 3 : mine.size())
        iFollow.each{aa->
            def his = ((aa.user2).friends).take(aa.user2.friends.size() > 6 ? 6 : aa.user2.friends.size())
            his.each{
                def already = Friendship.findByUser1AndUser2(user,it.user2)
                if ((!already) &&(it.user2 != user)&& (it.user2 != dummy))
                    followedByFriends += [id: it.user2.id, name: it.user2.name, picture: userService.userImageUrl(it.user2)]
            }

        }

        def i = 0
        iFollow.each{
            def already = Friendship.findByUser1AndUser2(user,it.user1)
            if ((!already)&&(it.user1 != user) && (it.user1 != dummy))
                followers += [id: it.user1.id, name: it.user1.name, picture: userService.userImageUrl(it.user1)]
        }

        def followMyBrands = []
        def brands = Relation.findAllByUser(user).collect{it.company}.take(3) //maximun 3 brands i follow

        brands.each{marca->
            def clients = marca.clients().take(10)
            clients.each{cliente->
                def amistad = Friendship.findByUser1AndUser2(user,cliente) ?: Friendship.findByUser1AndUser2(cliente,user)
                if((cliente != user)&& (!amistad) &&(cliente != dummy))
                    followMyBrands += [id: cliente.id, name: cliente.name, picture: userService.userImageUrl(cliente)]
            }

        }
        //



        def result = [status:  1, existing: existing, invite: invite,followers: followers,followedByFriends: followedByFriends, followMyBrands: followMyBrands]
        render result as JSON
        return
    }*/

    /*----------------------------------------------
    *
    *
    *
    *               COMPLETE PROFILE - MAY 21TH 2013 - Juanda
    *
    *
    *
    * ------------------------------------------------*/




    /*----------------------------------------------
    *
    *
    *
    *               GET FRIEND'S ACTIVITY ON NEST5 - JUNE 4TH 2013 - Juanda
    *
    *
    *
    * ------------------------------------------------*/

//    @Secured(["ROLE_USER"])
    def activity(){
        def vUser = springSecurityService.currentUser
        def user = springSecurityService.currentUser
        if(!user) return
        def extended = ExtendedUser.findByUser(user)
        def friends = user.friends() //gets both friends and isFriendOf
        def events = []
        if(friends)
            events = Event.findAll("from Event as e where e.user in (:friends) order by e.date desc",[friends: friends],[max: params.max as Integer ?: 20, offset: params.offset as Integer ?: 0])
        def result =  []


        events.each {
            def comments = Comment.findAll("from Comment as c where c.event=:event order by c.date asc", [event: it], [max: 10, offset: 0])
            def commentsResult = []

            comments.each{com->

                commentsResult += [id: com.id, date: utilityService.timeFrom(com.date), content: com.content, user: [id: com.user.id, name: com.user.name, picture: userService.userImageUrl(com.user)]]
            }
            def userHearts = Heart.findByUserAndEvent(user, it) ?: false
            switch(it.type.code){
                case "NEI001": result += [id: it.id, type: [code: it.type.code,name: it.type.name], message: it.toString(), user: [id: it.user.id, uid: it.user.uid,name: it.user.name,picture: userService.userImageUrl(it.user)],element:[id: it.company.id,uid: null,name: it.company.name,picture: "http://lorempixel.com/300/200/nightlife/" ,stamps: it.stampCount,total_stamps: 5, store: it.store,coupons: 0], comments: commentsResult,userHearts: userHearts,userInstance: user, vUser: vUser]
                    break
                case "NEI002": //message = message(code: 'user.get.coupon', args: [it.user.name,it.company.name,"5","6" ])
                    result += [id: it.id, type: [code: it.type.code,name: it.type.name], message: it.toString(), user: [id: it.user.id, uid: it.user.uid,name: it.user.name,picture: userService.userImageUrl(it.user)],element:[id: it.company.id,uid: null,name: it.company.name,picture: "http://lorempixel.com/300/200/food/" ,stamps: 0,total_stamps: 0, store: it.store,coupons: it.couponCount], comments: commentsResult,userHearts: userHearts,userInstance: user, vUser: vUser]
                    break
                case "NEI003": //message = message(code: 'user.redeem.coupon', args: [it.user.name,it.company.name,"5","6" ])
                    break
                case "NEI004": result += [id: it.id,type: [code: it.type.code,name: it.type.name], message: it.toString(), user: [id: it.user.id, uid: it.user.uid,name: it.user.name,picture: userService.userImageUrl(it.user)],element:[id: it.oUser.id,uid: it.oUser.uid,name: it.oUser.name,picture: userService.userImageUrl(it.oUser),stamps: 0,total_stamps: 0, store:null,coupons: 0], comments: commentsResult,userHearts: userHearts]
                    break
            }

        }
        render result as JSON
        return

    }

    /*----------------------------------------------
    *
    *
    *
    *               GO TO USER PROFILE - JUNE 16 2013 JUANDA
    *
    *
    *
    * ------------------------------------------------*/



    /*----------------------------------------------
   *
   *
   *
   *               GO TO USER INFO PAGE - JUNE 20 2013 JUANDA
   *
   *
   *
   * ------------------------------------------------*/




    /*----------------------------------------------
   *
   *
   *
   *              SIMPLIFIED USER REGISTER, ONLY EMAIL, NAME AND ADDRESS FOR CARD AND LETTER - JUNE 23 2013 JUANDA
   *
   *
   *
   * ------------------------------------------------*/


    def simpleCreate(){
        def result  = []
        //enviar email, crear extended con address
        def userRole = SecRole.findByAuthority('ROLE_USER')
        println params
        def email = params.email
        def user = new User(
                name: params.name,
                email: email,
                username: email,
                date: "00/00/0000",
                phone: "0000000",
                referer: "none",
                password: params.password,
                enabled: true,
                accountExpired: false,
                accountLocked: false,
                passwordExpired: false
        )
        if(!user.save(flush: true)){
            result = [status: 0, errors: "hubo errores creando el usuario, inténtalo de nuevo."]
            println user.errors.allErrors
        }
        SecUserSecRole.create user,userRole
        def longi = params.lat ? new BigDecimal(params.lat) : new BigDecimal(0)
        def lati =  params.lng ? new BigDecimal(params.lng) :  new BigDecimal(0)
        def city = City.findByCode(params.city) ?: City.findByCode("unknown")
        def extended = new ExtendedUser(
                user: user,
                bio: "",
                gender: "unknown",
                birthDate: new Date(),
                accessToken: "0",
                working: false,
                educated: false,
                completed: false,
                twitterUser: "",
                fqAccessToken: "0",
                address: params.address ?: "",
                city: city,
                latitude: lati,
                longitude: longi
        )
        if(!extended.save(flush : true)){
            result = [status: 0, errors: "hubo errores creando el usuario, inténtalo de nuevo."]
            println extended.errors.allErrors

        }
        if(session.referer)
        {
            def inv = Invitation.findById(session.referer as Long)
            if(inv){
                inv.registered = true
                inv.dateRegistered = new Date()
                inv.registeredUser = user
                inv.email = user.email
                inv.save(flush: true)
                session.referer = null
                session.invalidate()
            }
        }

        render result as JSON
        return
    }
    /*
    *
    *    SEND INVITATION TO MAXIMUN 10 FRIENDS -- JUNE 25 2013 JUANDA
    *
    *
    * */






   /*
    *
    *    FOLLOW BRAND (BECOME CUSTOMER) - JUANDA JULY 1 2013
    *
    *
    * */

//    @Secured(["ROLE_USER","ROLE_ADMIN"])
    def followBrand(){
       User user = springSecurityService.currentUser
        if(!user) return
        def brand = Company.findById(params.company as Long)
        def result = []
        if(!brand){
           result = [status: 0, message: "no existe la marca seleccionada", brand: [id: null, name: null, picture: null ]]
            render result as JSON
            return
        }
        user.addToCompany(brand)
        //generar Evento de usuario siguiendo marca
        result = [status: 1, message: "Siguiendo marca con éxito", brand: [id: brand.id, name: brand.name, picture: companyService.companyImageUrl(brand) ]]
        render result as JSON
        return

    }

//    @Secured(["ROLE_USER","ROLE_ADMIN"])
    def suggestBrands(){
        def user = springSecurityService.currentUser
        if(!user) return
        def brands = Company.findAll()
        def result = []
        def dummy = Company.findByUsername('dummyCompany')
        brands.each{actual->
            def relation = Relation.findByCompanyAndUser(actual, user)
            if ((actual != dummy)&& !relation)
                result += [id: actual.id, name: actual.name, picture: companyService.companyImageUrl(actual)]
        }
        render result as JSON
        return
    }









}
