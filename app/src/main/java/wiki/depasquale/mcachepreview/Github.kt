package wiki.depasquale.mcachepreview

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import wiki.depasquale.mcache.adapters.FilesIOHandler
import wiki.depasquale.mcache.core.MCacheBuilder
import wiki.depasquale.mcache.core.internal.FileParams
import java.util.concurrent.*

/**
 * diareuse on 26.03.2017
 */

internal object Github {

  private var retrofit: Retrofit? = null
  private var service: Service? = null

  @SuppressLint("LogConditional")
  fun user(username: String): Observable<User> {
    return MCacheBuilder
        .request(User::class.java)
        .using(FilesIOHandler::class.java)
        .force(true)
        .params(FileParams(username))
        .with(getRetrofit()
            .user(username)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()))
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
      var response: Response? = null
      response = chain.proceed(request)
      if (response != null) {
        Log.i("ServiceInfo", response.request().url().url().toString())
      }
      response
    })
    return client.build()
  }

  private interface Service {

    @GET("/users/{user}")
    fun user(@Path("user") user: String): Observable<User>
  }

}