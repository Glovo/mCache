package wiki.depasquale.mcache;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class JavaIntegrityTest {

    @Before
    public void setup() {
    }

    @Test
    public final void testIntegrity() {
        try {
            Cache
                    .obtain(Cache.class)
                    .build();

            Cache
                    .give(this)
                    .build();
        } catch (Exception e) {
            assert e instanceof RuntimeException;
            assert e.getMessage().contains("Context");
        }
    }
}
