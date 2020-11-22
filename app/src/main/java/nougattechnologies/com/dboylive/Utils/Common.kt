package nougattechnologies.com.dboylive.Utils

import nougattechnologies.com.dboylive.Model.User
import nougattechnologies.com.dboylive.Model.UserLocation
import nougattechnologies.com.dboylive.Rretrofit.IDrinkShopAPI
import nougattechnologies.com.dboylive.Rretrofit.RetrofitClient

/**
 * Created by Yeuni on 06/01/2018.
 */
object Common {
    const val BASE_URL = ""


    var currentuser: User? = null
    var userLocations: UserLocation? = null

    //
    //
    //    }
    @JvmStatic
    val aPI: IDrinkShopAPI
        get() = RetrofitClient.getClient(BASE_URL).create(IDrinkShopAPI::class.java)
}