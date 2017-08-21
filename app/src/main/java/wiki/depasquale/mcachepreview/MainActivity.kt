package wiki.depasquale.mcachepreview

import android.os.Bundle
import android.support.design.widget.*
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.Toolbar
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import wiki.depasquale.mcache.BuildConfig
import wiki.depasquale.mcache.MCache
import wiki.depasquale.mcache.core.MCacheBuilder
import wiki.depasquale.mcache.core.internal.FileParams
import wiki.depasquale.mcache.core.internal.FileParams.Time
import java.util.*
import java.util.concurrent.*

class MainActivity : AppCompatActivity(), Consumer<User> {

  @BindView(R.id.toolbar)
  internal var toolbar: Toolbar? = null
  @BindView(R.id.user)
  internal var user: AppCompatTextView? = null
  @BindView(R.id.responseTime)
  internal var responseTime: AppCompatTextView? = null
  @BindView(R.id.message)
  internal var message: TextView? = null
  @BindView(R.id.content)
  internal var content: LinearLayout? = null
  @BindView(R.id.container)
  internal var container: NestedScrollView? = null
  @BindView(R.id.fab)
  internal var fab: FloatingActionButton? = null
  @BindView(R.id.et)
  internal var et: TextInputEditText? = null
  @BindView(R.id.input)
  internal var input: TextInputLayout? = null
  @BindView(R.id.plugin)
  internal var plugin: AppCompatTextView? = null

  private var startTime: Long = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    ButterKnife.bind(this)

    setSupportActionBar(toolbar)

    et!!.setText("diareuse")
    et!!.post { fab!!.performClick() }

    plugin!!.text = BuildConfig.VERSION_NAME

    fab!!.setOnClickListener { v ->
      message!!.text = null
      user!!.text = null
      responseTime!!.text = null

      val username = et!!.text.toString()
      if (username.isEmpty()) {
        input!!.error = "Please fill this field :)"
        input!!.isErrorEnabled = true
      } else {
        if (username.equals("clean", ignoreCase = true)) {
          MCache.clean()
        } else if (username.equals("all", ignoreCase = true)) {
          retrieveAll()
        } else if (username.equals("removeall", ignoreCase = true)) {
          removeAll()
        } else if (username.equals("time boundaries", ignoreCase = true)) {
          timeBoundaries()
        } else {
          retrieveUser(username)
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
          user!!.text = String.format("/users/%s", "all")
          responseTime!!.append(if (responseTime!!.text.length > 0) "\n" else "")
          responseTime!!.append(String.format(Locale.getDefault(), "%d ms",
              (System.nanoTime() - startTime) / 1000000))
          message!!.text = Gson().toJson(it)
        }
  }

  private fun removeAll() {
    startTime = System.nanoTime()
    val params = FileParams("")
    params.write.all = true
    MCacheBuilder.request(User::class.java)
        .params(params)
        .remove { success ->
          responseTime!!.append(if (responseTime!!.text.length > 0) "\n" else "")
          responseTime!!.append(String.format(Locale.getDefault(), "%d ms",
              (System.nanoTime() - startTime) / 1000000))
          message!!.text = if (success) "OK" else "FAILED"
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
          user!!.text = String.format("/users/%s", "all")
          responseTime!!.append(if (responseTime!!.text.length > 0) "\n" else "")
          responseTime!!.append(String.format(Locale.getDefault(), "%d ms",
              (System.nanoTime() - startTime) / 1000000))
          message!!.text = Gson().toJson(it)
        }
  }

  private fun retrieveUser(username: String) {
    input!!.isErrorEnabled = false
    user!!.text = String.format("/users/%s", username)
    startTime = System.nanoTime()
    Github.user(username).subscribe(this,
        Consumer<Throwable> { error ->
          error.printStackTrace()
          Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
        })
  }

  @Throws(Exception::class)
  override fun accept(user: User) {
    responseTime!!.append(if (responseTime!!.text.length > 0) "\n" else "")
    responseTime!!.append(String.format(Locale.getDefault(), "%d ms",
        (System.nanoTime() - startTime) / 1000000))
    message!!.text = Gson().toJson(user)
  }
}
