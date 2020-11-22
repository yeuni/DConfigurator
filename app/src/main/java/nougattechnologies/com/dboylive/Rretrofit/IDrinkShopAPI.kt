package nougattechnologies.com.dboylive.Rretrofit

import io.reactivex.Observable
import nougattechnologies.com.dboylive.CheckUserResponse
import nougattechnologies.com.dboylive.Model.User
import nougattechnologies.com.dboylive.Model.UserLocation
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Created by Yeuni on 06/14/2018.
 */
interface IDrinkShopAPI {
    @FormUrlEncoded
    @POST("checkuser.php")
    fun  //checkuser.php
            checkuserexists(@Field("phone") phone: String?): Call<CheckUserResponse?>?

    @FormUrlEncoded
    @POST("checkloginuser.php")
    fun  //checkuser.php
            checkuserphoneandpassExists(@Field("phone") phone: String?,
                                        @Field("password") password: String?): Call<CheckUserResponse?>?

    @FormUrlEncoded
    @POST("register.php")
    fun registerNewUser(
            @Field("username") username: String?,
            @Field("phonenumber") phonenumber: String?,
            @Field("password") password: String?): Call<User?>?

    @FormUrlEncoded
    @POST("updateuserloc.php")
    fun updateLocation(
            @Field("phone") phone: String?,
            @Field("latitude") latitude: String?,
            @Field("longitude") longitude: String?
    ): Call<String?>?

    @FormUrlEncoded
    @POST("updatedboyloc.php")
    fun updateDboy(
            @Field("phone") phone: String?,
            @Field("latitude") latitude: String?,
            @Field("longitude") longitude: String?
    ): Call<String?>?

    @GET("gemyloctuser.php")
    fun getuser(): Observable<List<UserLocation?>?>?

    @get:GET("getdboyloc.php")
    val dboy: Observable<List<DeliveryBoyLocation?>?>?
}