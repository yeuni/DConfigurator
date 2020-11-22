package nougattechnologies.com.dboylive.Rretrofit

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

//import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
/**
 * Created by Yeuni on 06/01/2018.
 */
class RetrofitClient {
    var cacheSize = 10 * 1024 * 1024 // 10 MiB

    companion object {
        private var retrofit: Retrofit? = null
        fun getClient(baseUrl: String?): Retrofit? {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
            }
            return retrofit
        }
    }
}