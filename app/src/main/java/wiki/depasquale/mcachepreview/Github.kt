package wiki.depasquale.mcachepreview

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import wiki.depasquale.mcache.Cache
import java.util.concurrent.TimeUnit

/**
 * diareuse on 26.03.2017
 */

object Github {

    private var retrofit: Retrofit? = null
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
            retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .client(client())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }
        if (service == null) {
            service = retrofit!!.create(Service::class.java)
        }
        return service!!
    }

    private fun client(): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.connectTimeout(200, TimeUnit.SECONDS)
        client.readTimeout(200, TimeUnit.SECONDS)
        client.interceptors().add(Interceptor { chain ->
            val request = chain.request()
            val response: Response? = chain.proceed(request)
            Log.i("ServiceInfo", response?.request()?.url()?.url().toString())
            response
        })
        return client.build()
    }

    private interface Service {

        @GET("/users/{user}")
        fun user(@Path("user") user: String): Observable<User>
    }

}
