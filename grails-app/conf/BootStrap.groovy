import com.nest5.Nest5Client.City
import com.nest5.Nest5Client.Company
import com.nest5.Nest5Client.Country
import com.nest5.Nest5Client.EventType
import com.nest5.Nest5Client.Heart
import com.nest5.Nest5Client.Promo
import com.nest5.Nest5Client.SecRole
import com.nest5.Nest5Client.SecUserSecRole
import com.nest5.Nest5Client.User
import com.nest5.Nest5Client.Category
import com.nest5.Nest5Client.Icon

class BootStrap {
    //

    def init = { servletContext ->

        def apiRole = SecRole.findByAuthority('ROLE_API') ?: new SecRole(authority: 'ROLE_API').save(failOnError: true)



        def apiUser = User.findByUsername('apiadmin') ?: new User(
                username: 'apiadmin',
                password: 'zAxEE9U1691Nq0h5JiJ0X20tcayF5RTpmzIOctVGNQNBByHslznDR0VP7rWOuyW',
                enabled: true,
                name : 'Android Client Application',
                email: 'soporte@nest5.com',
                smartphone: true,
                smartphoneBrand: 'Android-Samsung',
                phone: '3014597229',
                referer: 'none',
                date: '1986/01/13').save(failOnError: true)








        if (!apiUser.authorities.contains(apiRole)) {
            SecUserSecRole.create apiUser, apiRole
        }




     }
    def destroy = {
    }
}
