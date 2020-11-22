package nougattechnologies.com.dboylive.Model

/**
 * Created by Yeuni on 06/14/2018.
 */
class User {
    var username: String? = null
    var phonenumber: String? = null
    var password: String? = null

    //@Field("username") String username,
    //            @Field("phonenumber") String phonenumber,
    //            @Field("password") String password);
    //public string phone { get; set; }
    //    public string name { get; set; }
    //    public string address { get; set; }
    //    private String phone;
    //    private String name ;
    //    private  String address ;
    var error_msg //it will empty if user return object
            : String? = null
    var avatarUrl: String? = null

}