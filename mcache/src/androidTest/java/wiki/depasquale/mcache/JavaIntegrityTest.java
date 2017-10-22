package wiki.depasquale.mcache;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * diareuse on 22.10.2017
 */

@MediumTest
@RunWith(AndroidJUnit4.class)
public class JavaIntegrityTest {

  @Before
  public void setup() {
    Cache
        .obtain(Cache.class)
        .build();

    Cache
        .give(this)
        .build();
  }
}
