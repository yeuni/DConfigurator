package nougattechnologies.com.dboylive.Model

class UserLocation {
    var phone: String? = null
    var latitude: String? = null
    var longitude: String? = null

    constructor() {}
    constructor(phone: String?, latitude: String?, longitude: String?) {
        this.phone = phone
        this.latitude = latitude
        this.longitude = longitude
    }

}