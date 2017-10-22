package wiki.depasquale.mcache

import android.support.test.InstrumentationRegistry
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
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
  fun dryLoad() {
    val gotData = Cache.obtain(BasicData::class.java)
        .build()
        .getNow()

    assert(gotData == null)
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
  fun save() {
    val givenData = Cache.give(data)
        .build()
        .getNow()

    assert(givenData === data)
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
  fun load() {
    save()
    val gotData = Cache.obtain(BasicData::class.java)
        .build()
        .getNow()!!

    assert(gotData.name == data.name)
    gotData.validateInnerData()
    assert(gotData != data)
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

  private fun BasicData.validateInnerData() {
    assert(this.innerData == this@KotlinIntegrityTest.innerData)
    assert(this.innerData.contents == this@KotlinIntegrityTest.innerData.contents)
  }

  companion object {
    const val TEST_INDEX = "test_index"
  }
}