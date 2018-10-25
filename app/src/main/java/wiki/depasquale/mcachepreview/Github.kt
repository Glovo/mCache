package wiki.depasquale.mcachepreview

import android.annotation.SuppressLint
import android.util.Log
import com.squareup.okhttp.Interceptor
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Response
import retrofit.RestAdapter
import retrofit.client.OkClient
import retrofit.http.GET
import retrofit.http.Path
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import wiki.depasquale.mcache.Cache
import java.util.concurrent.TimeUnit

/**
 * diareuse on 26.03.2017
 */

object Github {

    private var retrofit: RestAdapter? = null
    private var service: Service? = null

    @SuppressLint("LogConditional")
    fun user(username: String): Observable<User> {
        return Cache.obtain(User::class.java)
            .ofIndex(username)
            .build()
            .getLaterConcat(
                getRetrofit()
                    .user(username)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
            )
    }

    private fun getRetrofit(): Service {
        if (retrofit == null) {
            retrofit =  RestAdapter.Builder()
                    .setEndpoint("https://api.github.com/")
                    .setClient(OkClient(client()))
                    .build()
        }
        if (service == null) {
            service = retrofit!!.create(Service::class.java)
        }
        return service!!
    }

    private fun client(): OkHttpClient {
        val client = OkHttpClient()
        client.setConnectTimeout(200, TimeUnit.SECONDS)
        client.setReadTimeout(200, TimeUnit.SECONDS)
        client.interceptors().add(Interceptor { chain ->
            val request = chain.request()
            val response: Response? = chain.proceed(request)
            Log.i("ServiceInfo", response?.request()?.url()?.toString())
            response
        })
        return client
    }

    private interface Service {

        @GET("/users/{user}")
        fun user(@Path("user") user: String): Observable<User>
    }

}
