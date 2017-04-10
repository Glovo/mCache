package wiki.depasquale.mcachepreview;

import android.support.annotation.NonNull;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import wiki.depasquale.mcache.MCache;
import wiki.depasquale.mcache.RxMCacheUtil;

/**
 * diareuse on 26.03.2017
 */

class Github {

  private static String userUsername = "";

  @NonNull
  public static Observable<User> user(String username) {
    return RxMCacheUtil.wrap(
        generate(create(Service.class).user(username)),
        User.class,
        MCache.DEFAULT_ID,
        !username.equals(userUsername),
        username.equals(userUsername)
    ).map(user -> {
      userUsername = username;
      Log.d("mCachePreview", "DEBUG: saved username: " + username);
      return user;
    });
  }

  private static <T> T create(final Class<T> clazz) {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(client())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build();
    return retrofit.create(clazz);
  }

  private static OkHttpClient client() {
    OkHttpClient.Builder client = new OkHttpClient.Builder();
    client.connectTimeout(200, TimeUnit.SECONDS);
    client.readTimeout(200, TimeUnit.SECONDS);
    client.interceptors().add(chain -> {
      Request request = chain.request();
      okhttp3.Response response = null;
      response = chain.proceed(request);
      if (response != null) {
        Log.i("ServiceInfo", response.request().url().url().toString());
      }
      return response;
    });
    return client.build();
  }

  private static <T> Observable<T> generate(Observable<T> observable) {
    return observable
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread());
  }

  private interface Service {

    @GET("/users/{user}")
    Observable<User> user(@Path("user") String user);
  }

}
