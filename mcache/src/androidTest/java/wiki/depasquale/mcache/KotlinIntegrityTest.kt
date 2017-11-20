package wiki.depasquale.mcache

import android.app.Application
import android.support.test.InstrumentationRegistry
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.security.SecureRandom

@MediumTest
@RunWith(AndroidJUnit4::class)
class KotlinIntegrityTest {

  private val random = SecureRandom(System.currentTimeMillis().toString().toByteArray())
  private val innerData = InnerData(random.nextLong().toString())
  private val data = BasicData(random.nextLong().toString(), innerData)
  private val data2 = BasicData(random.nextLong().toString() + "_boo", innerData)

  @Before
  fun setup() {
    //init with context
    Cache
        .withGlobalMode(CacheMode.FILE)
        .with(InstrumentationRegistry.getContext())
    //delete everything for dry run to succeed
    Cache
        .obtain(Cache::class.java)
        .build()
        .delete()
  }

  @Test
  fun initialize() {
    Cache.with(Mockito.mock(Application::class.java))
  }

  @Test
  fun dryLoadAsync() {
    try {
      val gotData = Cache.obtain(BasicData::class.java)
          .build()
          .getLater()
          .blockingGet()
      assert(gotData == null)
    } catch (e: Exception) {
      assert(e is UnsureSuccessException)
    }
  }

  @Test
  fun saveAsync() {
    val givenData = Cache.give(data)
        .build()
        .getLater()
        .blockingGet()

    assert(givenData === data)
  }

  @Test
  fun saveAsyncWithCacheMode() {
    val givenData = Cache.give(data)
        .ofMode(CacheMode.CACHE)
        .build()
        .getLater()
        .blockingGet()

    assert(givenData === data)
  }

  @Test
  fun loadAsync() {
    saveAsync()
    val gotData = Cache.obtain(BasicData::class.java)
        .build()
        .getLater()
        .blockingGet()

    assert(gotData.name == data.name)
    gotData.validateInnerData()
    assert(gotData != data)
  }

  @Test
  fun loadAsyncWithFollowup() {
    saveAsync()
    Cache.obtain(BasicData::class.java)
        .build()
        .getLaterWithFollowup(Observable.empty())
        .subscribe()
  }

  @Test
  fun saveWithIndex() {
    val givenData = Cache.give(data2)
        .ofIndex(TEST_INDEX)
        .build()
        .getNow()

    assert(givenData === data2)
  }

  @Test
  fun loadWithIndex() {
    saveWithIndex()
    val gotData = Cache.obtain(BasicData::class.java)
        .ofIndex(TEST_INDEX)
        .build()
        .getNow()!!

    assert(gotData.name.endsWith("_boo"))
    assert(gotData.name == data2.name)
    gotData.validateInnerData()
    assert(gotData != data2)
  }

  @Test
  fun fetchAll() {
    saveAsync()
    saveWithIndex()
    val gotData = Cache.obtain(BasicData::class.java)
        .build()
        .getAllLater()
        .toList()
        .blockingGet()

    assert(gotData.size == 2)
  }

  @Test
  fun deleteSingle() {
    saveWithIndex()
    Cache.obtain(BasicData::class.java)
        .ofIndex(TEST_INDEX)
        .build()
        .deleteLater()
  }

  @Test
  fun deleteAllClass() {
    saveAsync()
    saveWithIndex()
    Cache.obtain(BasicData::class.java)
        .ofIndex("")
        .build()
        .delete()
  }

  @Test
  fun testKotlin() {
    give()
        .ofMode { CacheMode.CACHE }
        .ofIndex { TEST_INDEX }
        .build()

    /*obtain<BasicData>()
        .ofMode { CacheMode.FILE }
        .ofIndex { TEST_INDEX }
        .build()*/
  }

  private fun BasicData.validateInnerData() {
    assert(this.innerData == this@KotlinIntegrityTest.innerData)
    assert(this.innerData.contents == this@KotlinIntegrityTest.innerData.contents)
  }

  companion object {
    const val TEST_INDEX = "test_index"
  }
}