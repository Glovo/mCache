package wiki.depasquale.mcache

import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class BadBehaviorTest {

    @Test
    fun nullContext() {
        try {
            Cache
                .obtain(BasicData::class.java)
                .build()
                .getNow()
        } catch (e: RuntimeException) {
            assert(e.message?.contains("Context") == true)
        }
    }
}