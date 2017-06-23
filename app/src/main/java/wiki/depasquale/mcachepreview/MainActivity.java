package wiki.depasquale.mcachepreview;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.gson.Gson;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import java.util.Locale;
import wiki.depasquale.mcache.MCache;
import wiki.depasquale.mcache.core.MCacheBuilder;
import wiki.depasquale.mcache.core.internal.FileParams;

public class MainActivity extends AppCompatActivity implements Consumer<User> {

  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.user)
  AppCompatTextView user;
  @BindView(R.id.responseTime)
  AppCompatTextView responseTime;
  @BindView(R.id.message)
  TextView message;
  @BindView(R.id.content)
  LinearLayout content;
  @BindView(R.id.container)
  NestedScrollView container;
  @BindView(R.id.fab)
  FloatingActionButton fab;
  @BindView(R.id.et)
  TextInputEditText et;
  @BindView(R.id.input)
  TextInputLayout input;

  private long startTime;

  @Override
  protected final void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    setSupportActionBar(toolbar);

    et.setText("diareuse");
    et.post(() -> fab.performClick());

    fab.setOnClickListener(v -> {
      message.setText(null);
      user.setText(null);
      responseTime.setText(null);

      String username = et.getText().toString();
      if (username.isEmpty()) {
        input.setError("Please fill this field :)");
        input.setErrorEnabled(true);
      } else {
        if (username.equalsIgnoreCase("clean")) {
          MCache.clean();
        } else if (username.equalsIgnoreCase("all")) {
          retrieveAll();
        } else if (username.equalsIgnoreCase("removeall")) {
          removeAll();
        } else {
          retrieveUser(username);
        }
      }
    });

  }

  private void removeAll() {
    startTime = System.nanoTime();
    FileParams params = new FileParams("");
    params.setRemoveAll(true);
    params.setListener(success -> {
      responseTime.append(responseTime.getText().length() > 0 ? "\n" : "");
      responseTime.append(String.format(Locale.getDefault(), "%d ms",
          (System.nanoTime() - startTime) / 1000000));
      message.setText(success ? "OK" : "FAILED");
      return null;
    });
    MCacheBuilder.request(User.class)
        .params(params)
        .remove();
  }

  private void retrieveAll() {
    startTime = System.nanoTime();
    FileParams params = new FileParams("");
    params.setAll(true);
    MCacheBuilder.request(User.class)
        .params(params)
        .with(Observable.empty())
        .toList()
        .subscribe(it -> {
          user.setText(String.format("/users/%s", "all"));
          responseTime.append(responseTime.getText().length() > 0 ? "\n" : "");
          responseTime.append(String.format(Locale.getDefault(), "%d ms",
              (System.nanoTime() - startTime) / 1000000));
          message.setText(new Gson().toJson(it));
        });
  }

  private void retrieveUser(String username) {
    input.setErrorEnabled(false);
    user.setText(String.format("/users/%s", username));
    startTime = System.nanoTime();
    Github.user(username).subscribe(this,
        error -> {
          error.printStackTrace();
          Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
        });
  }

  @Override
  public void accept(@NonNull User user) throws Exception {
    responseTime.append(responseTime.getText().length() > 0 ? "\n" : "");
    responseTime.append(String.format(Locale.getDefault(), "%d ms",
        (System.nanoTime() - startTime) / 1000000));
    message.setText(new Gson().toJson(user));
  }
}
