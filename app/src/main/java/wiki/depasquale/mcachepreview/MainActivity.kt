package wiki.depasquale.mcachepreview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_main.*
import wiki.depasquale.mcache.BuildConfig
import wiki.depasquale.mcache.MCache
import wiki.depasquale.mcache.core.MCacheBuilder
import wiki.depasquale.mcache.core.internal.FileParams
import wiki.depasquale.mcache.core.internal.FileParams.Time
import java.util.*
import java.util.concurrent.*

class MainActivity : AppCompatActivity(), Consumer<User> {

  private var startTime: Long = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    setSupportActionBar(toolbar)

    et.setText("diareuse")
    et.post { fab.performClick() }

    plugin.text = BuildConfig.VERSION_NAME

    fab.setOnClickListener {
      message.text = null
      user.text = null
      responseTime.text = null

      val username = et.text.toString()
      if (username.isEmpty()) {
        input.error = "Please fill this field :)"
        input.isErrorEnabled = true
      } else {
        when {
          username.equals("clean", ignoreCase = true) -> MCache.clean()
          username.equals("all", ignoreCase = true) -> retrieveAll()
          username.equals("removeall", ignoreCase = true) -> removeAll()
          username.equals("time boundaries", ignoreCase = true) -> timeBoundaries()
          else -> retrieveUser(username)
        }
      }
    }

  }

  private fun timeBoundaries() {
    startTime = System.nanoTime()
    val params = FileParams()
    params.read.toChanged = Time.INFINITE
    params.read.fromChanged = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(1)
    MCacheBuilder.request(User::class.java)
        .params(params)
        .with(Observable.empty())
        .map { user -> user.htmlUrl }
        .toList()
        .subscribe { it ->
          user.text = String.format("/users/%s", "all")
          responseTime.append(if (responseTime.text.isNotEmpty()) "\n" else "")
          responseTime.append(String.format(Locale.getDefault(), "%d ms",
              (System.nanoTime() - startTime) / 1000000))
          message.text = Gson().toJson(it)
        }
  }

  private fun removeAll() {
    startTime = System.nanoTime()
    val params = FileParams("")
    params.write.all = true
    MCacheBuilder.request(User::class.java)
        .params(params)
        .remove { success ->
          responseTime.append(if (responseTime.text.isNotEmpty()) "\n" else "")
          responseTime.append(String.format(Locale.getDefault(), "%d ms",
              (System.nanoTime() - startTime) / 1000000))
          message.text = if (success) "OK" else "FAILED"
        }
  }

  private fun retrieveAll() {
    startTime = System.nanoTime()
    val params = FileParams("")
    params.read.all = true
    MCacheBuilder.request(User::class.java)
        .params(params)
        .with(Observable.empty())
        .toList()
        .subscribe { it ->
          user.text = String.format("/users/%s", "all")
          responseTime.append(if (responseTime.text.isNotEmpty()) "\n" else "")
          responseTime.append(String.format(Locale.getDefault(), "%d ms",
              (System.nanoTime() - startTime) / 1000000))
          message.text = Gson().toJson(it)
        }
  }

  private fun retrieveUser(username: String) {
    input.isErrorEnabled = false
    user.text = String.format("/users/%s", username)
    startTime = System.nanoTime()
    Github.user(username).subscribe(this,
        Consumer<Throwable> { error ->
          error.printStackTrace()
          Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
        })
  }

  @Throws(Exception::class)
  override fun accept(user: User) {
    responseTime.append(if (responseTime.text.isNotEmpty()) "\n" else "")
    responseTime.append(String.format(Locale.getDefault(), "%d ms",
        (System.nanoTime() - startTime) / 1000000))
    message.text = Gson().toJson(user)
  }
}
