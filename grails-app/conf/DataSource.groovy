dataSource {
    pooled = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "update"
            driverClassName = "org.postgresql.Driver"
            dialect = "org.hibernate.dialect.PostgreSQLDialect"
            url = "jdbc:postgresql://localhost:5432/newNest5DB2"
            username = "postgres"
            password = "qtagtech"
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            driverClassName = "org.postgresql.Driver"
            dialect = "org.hibernate.dialect.PostgreSQLDialect"
            host = System.env.OPENSHIFT_POSTGRESQL_DB_HOST
            port = System.env.OPENSHIFT_POSTGRESQL_DB_PORT
            url = "jdbc:postgresql://53061c234382ec92cb0000cf-nest5.rhcloud.com:40451/nest5web"
            username = "admingabhyhr"
            password = "MbUyqu2Z87Za"


        }
    }
        production {
            dataSource {
                dbCreate = "update"
                driverClassName = "org.postgresql.Driver"
                dialect = org.hibernate.dialect.PostgreSQLDialect
                host = System.env.OPENSHIFT_POSTGRESQL_DB_HOST
                port = System.env.OPENSHIFT_POSTGRESQL_DB_PORT
                url = "jdbc:postgresql://53061c234382ec92cb0000cf-nest5.rhcloud.com:40451/nest5web"
                username = "admingabhyhr"
                password = "MbUyqu2Z87Za"

            }
        }


}