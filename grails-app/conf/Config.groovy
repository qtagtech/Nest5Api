// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [
    all:           '*/*',
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']
grails.resources.adhoc.includes = []
grails.resources.adhoc.excludes = ["*"]

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

environments {
    development {
        grails.logging.jul.usebridge = true
//        grails.serverURL = "http://nest5.local:8080"

//        grails.serverURL = "http://localhost:8080"
        grails.serverURL = "http://hookthemapp.localhost.com:8080"
//        grails.serverURL = "http://nest5.com"
        grails.app.context = "/"
        /*grails.plugins.springsecurity.facebook.appId='171676339619557'
        grails.plugins.springsecurity.facebook.secret='c75f74270ce8dfdb455f05304fa68fc1'*/
        /*grails.plugins.springsecurity.facebook.appId='550109801691585'//Production
        grails.plugins.springsecurity.facebook.secret='513001ad6358dbb1cadedc820bd56b07'*/
        com.nest5.Nest5Client.bigDataServerURL = "http://localhost:8090"
    }
    production {
        grails.logging.jul.usebridge = true

        grails.serverURL = "http://api.nest5.com"
//        grails.serverURL = "http://hookthemapp.localhost.com:8080"
        grails.app.context = "/"
        /*grails.plugins.springsecurity.facebook.appId='271254856287847'
        grails.plugins.springsecurity.facebook.secret='7c38b8935ccfda16f94d8fe7e2c1295a'*/
        /*grails.plugins.springsecurity.facebook.appId='171676339619557'
        grails.plugins.springsecurity.facebook.secret='c75f74270ce8dfdb455f05304fa68fc1'*/
        /*grails.plugins.springsecurity.facebook.appId='550109801691585'
        grails.plugins.springsecurity.facebook.secret='513001ad6358dbb1cadedc820bd56b07'*/

    }
}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
           'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}

//Email Server Configuration
grails {
    mail {
        host = "smtp.gmail.com"
        port = 465
        username = "multimedia@nest5.com"
        password = "farroyavefami"
        props = ["mail.smtp.auth":"true",
                "mail.smtp.socketFactory.port":"465",
                "mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
                "mail.smtp.socketFactory.fallback":"false"]
    }
}

// Added by the Spring Security Core plugin:
/*grails.plugins.springsecurity.userLookup.userDomainClassName = 'com.nest5.Nest5Client.SecUser'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'com.nest5.Nest5Client.SecUserSecRole'
grails.plugins.springsecurity.authority.className = 'com.nest5.Nest5Client.SecRole'*/
grails.plugins.springsecurity.useBasicAuth = true
grails.plugins.springsecurity.basic.realmName = "Nest5Client"
/*grails.plugins.springsecurity.filterChain.chainMap = [
        '/api*': 'JOINED_FILTERS,-exceptionTranslationFilter',
        '/j_spring_security_facebook_redirect' : 'JOINED_FILTERS,-basicAuthenticationFilter,-basicExceptionTranslationFilter',
        '*': 'JOINED_FILTERS,-basicAuthenticationFilter,-basicExceptionTranslationFilter',]*/




//grails.plugins.springsecurity.facebook.domain.classname='com.nest5.Nest5Client.FacebookUser'
//grails.plugins.springsecurity.facebook.permissions = 'publish_actions,email,user_relationship_details,friends_relationship_details,user_birthday,friends_birthday,user_likes,friends_likes,user_hometown,friends_hometown,user_location,friends_location,user_work_history,friends_work_history'
//grails.plugins.springsecurity.securityConfigType = "Annotation"
//grails.nest5.foursquare.clientId='BSXO1F1NWFFWOKHZZSB1EEU3JRUB1XNZ2XA2NHUFAUPXV5MR'
//grails.nest5.foursquare.secret='R2MY1H30WP5UR3UB2VZWSNU5BFBQE4N31CJZRAJH4YQ03EAN'
//grails.nest5.foursquare.redirectUri="http://hookthemapp.localhost.com:8080/foursquare"


// Added by the Spring Security Core plugin:
grails.plugins.springsecurity.userLookup.userDomainClassName = 'com.nest5.Nest5Client.SecUser'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'com.nest5.Nest5Client.SecUserSecRole'
grails.plugins.springsecurity.authority.className = 'com.nest5.Nest5Client.SecRole'

// Uncomment and edit the following lines to start using Grails encoding & escaping improvements

/* remove this line 
// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside null
                scriptlet = 'none' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
remove this line */
