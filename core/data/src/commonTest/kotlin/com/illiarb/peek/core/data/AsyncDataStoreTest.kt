package com.illiarb.peek.core.data

import app.cash.turbine.test
import com.illiarb.peek.core.data.AsyncDataStore.LoadStrategy.TimeBased
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

internal class AsyncDataStoreTest {

  @Test
  fun `it should emit loading and content from network when cache is empty`() = runTest {
    val params = TestParams("test1")
    val expectedContent = TestDomain("network-test1")
    val dataStore = createDataStore()

    dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
      assertIs<Async.Loading>(awaitItem())
      val contentItem = awaitItem()
      assertIs<Async.Content<TestDomain>>(contentItem)
      assertEquals(expectedContent, contentItem.content)
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `it should emit memory cache first then fetch from network with CacheFirst`() = runTest {
    val params = TestParams("test2")
    val memoryContent = TestDomain("memory-test2")
    val networkContent = TestDomain("network-test2")
    var networkCallCount = 0

    val dataStore = createDataStore(
      networkFetcher = {
        ++networkCallCount
        TestDomain("network-${it.id}")
      },
      fromMemory = { memoryContent }
    )

    dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
      val memoryItem = awaitItem()
      assertIs<Async.Content<TestDomain>>(memoryItem)
      assertEquals(memoryContent, memoryItem.content)

      val networkItem = awaitItem()
      assertIs<Async.Content<TestDomain>>(networkItem)
      assertEquals(networkContent, networkItem.content)

      cancelAndIgnoreRemainingEvents()
    }

    assertEquals(1, networkCallCount)
  }

  @Test
  fun `it should emit storage cache and warm memory before fetching from network`() = runTest {
    val params = TestParams("test3")
    val storageContent = TestDomain("storage-test3")
    val networkContent = TestDomain("network-test3")
    val memoryWarmupCalls = mutableListOf<TestDomain>()

    val dataStore = createDataStore(
      fromStorage = { storageContent },
      intoMemory = { _, domain -> memoryWarmupCalls.add(domain) }
    )

    dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
      val storageItem = awaitItem()
      assertIs<Async.Content<TestDomain>>(storageItem)
      assertEquals(storageContent, storageItem.content)
      assertTrue(storageItem.contentRefreshing)

      val networkItem = awaitItem()
      assertIs<Async.Content<TestDomain>>(networkItem)
      assertEquals(networkContent, networkItem.content)
      assertFalse(networkItem.contentRefreshing)

      cancelAndIgnoreRemainingEvents()
    }

    assertEquals(listOf(storageContent, networkContent), memoryWarmupCalls)
  }

  @Test
  fun `it should update both storage and memory after successful network fetch`() = runTest {
    val params = TestParams("test8")
    val networkContent = TestDomain("network-test8")
    val storageUpdates = mutableListOf<TestDomain>()
    val memoryUpdates = mutableListOf<TestDomain>()

    val dataStore = createDataStore(
      networkFetcher = { networkContent },
      intoStorage = { _, domain -> storageUpdates.add(domain) },
      intoMemory = { _, domain -> memoryUpdates.add(domain) }
    )

    dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
      awaitItem() // Loading
      awaitItem() // Content
      cancelAndIgnoreRemainingEvents()
    }

    assertEquals(listOf(networkContent), storageUpdates)
    assertEquals(listOf(networkContent), memoryUpdates)
  }

  @Test
  fun `it should emit error when network fails and cache is empty`() = runTest {
    val params = TestParams("test4")
    val networkError = RuntimeException("Network error")

    val dataStore = createDataStore(
      networkFetcher = { throw networkError }
    )

    dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
      assertIs<Async.Loading>(awaitItem())
      val errorItem = awaitItem()
      assertIs<Async.Error>(errorItem)
      assertEquals(networkError, errorItem.error)
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `it should emit cache with suppressed error when network fails but cache exists`() = runTest {
    val params = TestParams("test5")
    val memoryContent = TestDomain("memory-test5")
    val networkError = RuntimeException("Network error")

    val dataStore = createDataStore(
      networkFetcher = { throw networkError },
      fromMemory = { memoryContent }
    )

    dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
      val contentItem = awaitItem()
      assertIs<Async.Content<TestDomain>>(contentItem)
      assertEquals(memoryContent, contentItem.content)

      val contentWithError = awaitItem()
      assertIs<Async.Content<TestDomain>>(contentWithError)
      assertEquals(memoryContent, contentWithError.content)
      assertEquals(networkError, contentWithError.suppressedError)

      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `it should fetch from network with CacheOnly when cache is empty`() = runTest {
    val params = TestParams("test6")
    val networkContent = TestDomain("network-test6")

    val dataStore = createDataStore(
      networkFetcher = { networkContent }
    )

    dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheOnly).test {
      assertIs<Async.Loading>(awaitItem())
      val contentItem = awaitItem()
      assertIs<Async.Content<TestDomain>>(contentItem)
      assertEquals(networkContent, contentItem.content)
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `it should return cache without network call with CacheOnly strategy`() = runTest {
    val params = TestParams("test7")
    val memoryContent = TestDomain("memory-test7")
    var networkCallCount = 0

    val dataStore = createDataStore(
      networkFetcher = {
        ++networkCallCount
        TestDomain("network-${it.id}")
      },
      fromMemory = { memoryContent }
    )

    dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheOnly).test {
      val contentItem = awaitItem()
      assertIs<Async.Content<TestDomain>>(contentItem)
      assertEquals(memoryContent, contentItem.content)
      cancelAndIgnoreRemainingEvents()
    }

    assertEquals(0, networkCallCount)
  }

  @Test
  fun `it should share single network request when multiple subscribers collect same params`() =
    runTest {
      val params = TestParams("shared")
      var networkCallCount = 0
      val networkContent = TestDomain("network-shared")

      val dataStore = createDataStore(
        networkFetcher = {
          ++networkCallCount
          delay(100)
          networkContent
        }
      )

      val receivedContent = mutableListOf<TestDomain>()

      val job1 = launch {
        dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
          // First subscriber gets Loading, then Content
          awaitItem() // Loading
          val contentItem = awaitItem()
          assertIs<Async.Content<TestDomain>>(contentItem)
          receivedContent.add(contentItem.content)
          cancelAndIgnoreRemainingEvents()
        }
      }

      val job2 = launch {
        dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
          // Later subscribers may miss Loading due to replay=0, so just await content
          val item = awaitItem()
          val content = if (item is Async.Loading) awaitItem() else item
          assertIs<Async.Content<TestDomain>>(content)
          receivedContent.add(content.content)
          cancelAndIgnoreRemainingEvents()
        }
      }

      val job3 = launch {
        dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
          val item = awaitItem()
          val content = if (item is Async.Loading) awaitItem() else item
          assertIs<Async.Content<TestDomain>>(content)
          receivedContent.add(content.content)
          cancelAndIgnoreRemainingEvents()
        }
      }

      job1.join()
      job2.join()
      job3.join()

      assertEquals(1, networkCallCount)
      assertEquals(3, receivedContent.size)
      receivedContent.forEach { assertEquals(networkContent, it) }
    }

  @Test
  fun `it should make separate network calls for different params`() = runTest {
    val params1 = TestParams("test1")
    val params2 = TestParams("test2")
    var networkCallCount = 0

    val dataStore = createDataStore(
      networkFetcher = { params ->
        ++networkCallCount
        delay(50)
        TestDomain("network-${params.id}")
      }
    )

    val job1 = launch {
      dataStore.collect(params1, AsyncDataStore.LoadStrategy.CacheFirst).test {
        awaitItem() // Loading
        awaitItem() // Content
        cancelAndIgnoreRemainingEvents()
      }
    }

    val job2 = launch {
      dataStore.collect(params2, AsyncDataStore.LoadStrategy.CacheFirst).test {
        awaitItem() // Loading
        awaitItem() // Content
        cancelAndIgnoreRemainingEvents()
      }
    }

    job1.join()
    job2.join()

    assertEquals(2, networkCallCount)
  }

  @Test
  fun `it should share flow for same params regardless of strategy`() =
    runTest {
      val params = TestParams("test")
      var networkCallCount = 0
      val networkContent = TestDomain("network-test")

      val dataStore = createDataStore(
        networkFetcher = {
          ++networkCallCount
          delay(50)
          networkContent
        }
      )

      val receivedContent = mutableListOf<TestDomain>()

      val job1 = launch {
        dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
          awaitItem() // Loading
          val content = awaitItem()
          assertIs<Async.Content<TestDomain>>(content)
          receivedContent.add(content.content)
          cancelAndIgnoreRemainingEvents()
        }
      }

      val job2 = launch {
        dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheOnly).test {
          // Shares the same flow, so may get Loading or Content depending on timing
          val item = awaitItem()
          val content = if (item is Async.Loading) awaitItem() else item
          assertIs<Async.Content<TestDomain>>(content)
          receivedContent.add(content.content)
          cancelAndIgnoreRemainingEvents()
        }
      }

      job1.join()
      job2.join()

      // Only one network call since flows are shared by params (strategy is ignored for caching)
      assertEquals(1, networkCallCount)
      assertEquals(2, receivedContent.size)
      receivedContent.forEach { assertEquals(networkContent, it) }
    }

  @Test
  fun `it should make new network call after updateLocal is called`() = runTest {
    val params = TestParams("test")
    var networkCallCount = 0
    val updatedContent = TestDomain("updated")

    val dataStore = createDataStore(
      networkFetcher = {
        ++networkCallCount
        TestDomain("network-${it.id}")
      }
    )

    dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
      awaitItem() // Loading
      awaitItem() // Content
      cancelAndIgnoreRemainingEvents()
    }

    dataStore.updateLocal(params, updatedContent)

    dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
      awaitItem() // Loading
      awaitItem() // Content
      cancelAndIgnoreRemainingEvents()
    }

    assertEquals(2, networkCallCount)
  }

  @Test
  fun `it should make new network call after invalidateMemory is called`() = runTest {
    val params = TestParams("test")
    var networkCallCount = 0

    val dataStore = createDataStore(
      networkFetcher = {
        ++networkCallCount
        TestDomain("network-${it.id}")
      }
    )

    dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
      awaitItem() // Loading
      awaitItem() // Content
      cancelAndIgnoreRemainingEvents()
    }

    dataStore.invalidateMemory(params)

    dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
      awaitItem() // Loading
      awaitItem() // Content
      cancelAndIgnoreRemainingEvents()
    }

    assertEquals(2, networkCallCount)
  }

  @Test
  fun `TimeBased should fetch from network when no cache timestamp exists`() = runTest {
    val params = TestParams("timebased1")
    val networkContent = TestDomain("network-timebased1")
    var networkCallCount = 0
    val invalidator = TestCacheInvalidator()

    val dataStore = createDataStore(
      networkFetcher = {
        ++networkCallCount
        networkContent
      }
    )

    val strategy = TimeBased(
      duration = 5.minutes,
      invalidator = invalidator
    )

    dataStore.collect(params, strategy).test {
      assertIs<Async.Loading>(awaitItem())
      val contentItem = awaitItem()
      assertIs<Async.Content<TestDomain>>(contentItem)
      assertEquals(networkContent, contentItem.content)
      cancelAndIgnoreRemainingEvents()
    }

    assertEquals(1, networkCallCount)
    assertNotNull(invalidator.timestamps[params])
  }

  @Test
  fun `TimeBased should not fetch from network when cache is still valid`() = runTest {
    val params = TestParams("timebased2")
    val memoryContent = TestDomain("memory-timebased2")
    var networkCallCount = 0
    val invalidator = TestCacheInvalidator()

    // Set a recent cache timestamp (within the duration)
    invalidator.timestamps[params] = Clock.System.now()

    val dataStore = createDataStore(
      networkFetcher = {
        ++networkCallCount
        TestDomain("network-${it.id}")
      },
      fromMemory = { memoryContent }
    )

    val strategy = TimeBased(
      duration = 5.minutes,
      invalidator = invalidator
    )

    dataStore.collect(params, strategy).test {
      val contentItem = awaitItem()
      assertIs<Async.Content<TestDomain>>(contentItem)
      assertEquals(memoryContent, contentItem.content)
      cancelAndIgnoreRemainingEvents()
    }

    assertEquals(0, networkCallCount)
  }

  @Test
  fun `TimeBased should fetch from network when cache has expired`() = runTest {
    val params = TestParams("timebased3")
    val memoryContent = TestDomain("memory-timebased3")
    val networkContent = TestDomain("network-timebased3")
    var networkCallCount = 0
    val invalidator = TestCacheInvalidator()

    // Set an old cache timestamp (expired)
    invalidator.timestamps[params] = Clock.System.now() - 10.minutes

    val dataStore = createDataStore(
      networkFetcher = {
        ++networkCallCount
        networkContent
      },
      fromMemory = { memoryContent }
    )

    val strategy = TimeBased(
      duration = 5.minutes,
      invalidator = invalidator
    )

    dataStore.collect(params, strategy).test {
      val memoryItem = awaitItem()
      assertIs<Async.Content<TestDomain>>(memoryItem)
      assertEquals(memoryContent, memoryItem.content)

      val networkItem = awaitItem()
      assertIs<Async.Content<TestDomain>>(networkItem)
      assertEquals(networkContent, networkItem.content)

      cancelAndIgnoreRemainingEvents()
    }

    assertEquals(1, networkCallCount)
  }

  @Test
  fun `TimeBased should update cache timestamp after successful network fetch`() = runTest {
    val params = TestParams("timebased4")
    val networkContent = TestDomain("network-timebased4")
    val invalidator = TestCacheInvalidator()

    // Set an old cache timestamp (expired)
    val oldTimestamp = Clock.System.now() - 10.minutes
    invalidator.timestamps[params] = oldTimestamp

    val dataStore = createDataStore(
      networkFetcher = { networkContent }
    )

    val strategy = TimeBased(
      duration = 5.minutes,
      invalidator = invalidator
    )

    dataStore.collect(params, strategy).test {
      assertIs<Async.Loading>(awaitItem())
      assertIs<Async.Content<TestDomain>>(awaitItem())
      cancelAndIgnoreRemainingEvents()
    }

    val newTimestamp = invalidator.timestamps[params]
    assertNotNull(newTimestamp)
    assertTrue(newTimestamp > oldTimestamp, "Cache timestamp should be updated after network fetch")
  }

  @Test
  fun `TimeBased should not update cache timestamp when network fetch fails`() = runTest {
    val params = TestParams("timebased5")
    val networkError = RuntimeException("Network error")
    val invalidator = TestCacheInvalidator()

    // Set an old cache timestamp (expired)
    val oldTimestamp = Clock.System.now() - 10.minutes
    invalidator.timestamps[params] = oldTimestamp

    val dataStore = createDataStore(
      networkFetcher = { throw networkError }
    )

    val strategy = TimeBased(
      duration = 5.minutes,
      invalidator = invalidator
    )

    dataStore.collect(params, strategy).test {
      assertIs<Async.Loading>(awaitItem())
      assertIs<Async.Error>(awaitItem())
      cancelAndIgnoreRemainingEvents()
    }

    assertEquals(oldTimestamp, invalidator.timestamps[params])
  }

  @Test
  fun `TimeBased should use storage cache when network is not needed`() = runTest {
    val params = TestParams("timebased6")
    val storageContent = TestDomain("storage-timebased6")
    var networkCallCount = 0
    val invalidator = TestCacheInvalidator()

    // Set a recent cache timestamp (within the duration)
    invalidator.timestamps[params] = Clock.System.now()

    val dataStore = createDataStore(
      networkFetcher = {
        ++networkCallCount
        TestDomain("network-${it.id}")
      },
      fromStorage = { storageContent }
    )

    val strategy = TimeBased(
      duration = 5.minutes,
      invalidator = invalidator
    )

    dataStore.collect(params, strategy).test {
      val contentItem = awaitItem()
      assertIs<Async.Content<TestDomain>>(contentItem)
      assertEquals(storageContent, contentItem.content)
      cancelAndIgnoreRemainingEvents()
    }

    assertEquals(0, networkCallCount)
  }

  @Test
  fun `TimeBased should return cached content with suppressed error when network fails`() =
    runTest {
      val params = TestParams("timebased7")
      val memoryContent = TestDomain("memory-timebased7")
      val networkError = RuntimeException("Network error")
      val invalidator = TestCacheInvalidator()

      // Set an old cache timestamp (expired)
      invalidator.timestamps[params] = Clock.System.now() - 10.minutes

      val dataStore = createDataStore(
        networkFetcher = { throw networkError },
        fromMemory = { memoryContent }
      )

      val strategy = TimeBased(
        duration = 5.minutes,
        invalidator = invalidator
      )

      dataStore.collect(params, strategy).test {
        val memoryItem = awaitItem()
        assertIs<Async.Content<TestDomain>>(memoryItem)
        assertEquals(memoryContent, memoryItem.content)

        val contentWithError = awaitItem()
        assertIs<Async.Content<TestDomain>>(contentWithError)
        assertEquals(memoryContent, contentWithError.content)
        assertEquals(networkError, contentWithError.suppressedError)

        cancelAndIgnoreRemainingEvents()
      }
    }

  @Test
  fun `ForceReload should skip memory cache and fetch from network`() = runTest {
    val params = TestParams("forcereload1")
    val memoryContent = TestDomain("memory-forcereload1")
    val networkContent = TestDomain("network-forcereload1")
    var networkCallCount = 0
    var memoryReadCount = 0

    val dataStore = createDataStore(
      networkFetcher = {
        ++networkCallCount
        networkContent
      },
      fromMemory = {
        ++memoryReadCount
        memoryContent
      }
    )

    dataStore.collect(params, AsyncDataStore.LoadStrategy.ForceReload).test {
      assertIs<Async.Loading>(awaitItem())
      val contentItem = awaitItem()
      assertIs<Async.Content<TestDomain>>(contentItem)
      assertEquals(networkContent, contentItem.content)
      cancelAndIgnoreRemainingEvents()
    }

    assertEquals(1, networkCallCount)
    assertEquals(0, memoryReadCount)
  }

  @Test
  fun `ForceReload should skip storage cache and fetch from network`() = runTest {
    val params = TestParams("forcereload2")
    val storageContent = TestDomain("storage-forcereload2")
    val networkContent = TestDomain("network-forcereload2")
    var networkCallCount = 0
    var storageReadCount = 0

    val dataStore = createDataStore(
      networkFetcher = {
        ++networkCallCount
        networkContent
      },
      fromStorage = {
        ++storageReadCount
        storageContent
      }
    )

    dataStore.collect(params, AsyncDataStore.LoadStrategy.ForceReload).test {
      assertIs<Async.Loading>(awaitItem())
      val contentItem = awaitItem()
      assertIs<Async.Content<TestDomain>>(contentItem)
      assertEquals(networkContent, contentItem.content)
      cancelAndIgnoreRemainingEvents()
    }

    assertEquals(1, networkCallCount)
    assertEquals(0, storageReadCount)
  }

  @Test
  fun `ForceReload should skip both memory and storage cache`() = runTest {
    val params = TestParams("forcereload3")
    val memoryContent = TestDomain("memory-forcereload3")
    val storageContent = TestDomain("storage-forcereload3")
    val networkContent = TestDomain("network-forcereload3")
    var memoryReadCount = 0
    var storageReadCount = 0

    val dataStore = createDataStore(
      networkFetcher = { networkContent },
      fromMemory = {
        ++memoryReadCount
        memoryContent
      },
      fromStorage = {
        ++storageReadCount
        storageContent
      }
    )

    dataStore.collect(params, AsyncDataStore.LoadStrategy.ForceReload).test {
      assertIs<Async.Loading>(awaitItem())
      val contentItem = awaitItem()
      assertIs<Async.Content<TestDomain>>(contentItem)
      assertEquals(networkContent, contentItem.content)
      cancelAndIgnoreRemainingEvents()
    }

    assertEquals(0, memoryReadCount)
    assertEquals(0, storageReadCount)
  }

  @Test
  fun `ForceReload should update storage and memory after successful network fetch`() = runTest {
    val params = TestParams("forcereload4")
    val networkContent = TestDomain("network-forcereload4")
    val storageUpdates = mutableListOf<TestDomain>()
    val memoryUpdates = mutableListOf<TestDomain>()

    val dataStore = createDataStore(
      networkFetcher = { networkContent },
      intoStorage = { _, domain -> storageUpdates.add(domain) },
      intoMemory = { _, domain -> memoryUpdates.add(domain) }
    )

    dataStore.collect(params, AsyncDataStore.LoadStrategy.ForceReload).test {
      awaitItem() // Loading
      awaitItem() // Content
      cancelAndIgnoreRemainingEvents()
    }

    assertEquals(listOf(networkContent), storageUpdates)
    assertEquals(listOf(networkContent), memoryUpdates)
  }

  @Test
  fun `ForceReload should emit error when network fails`() = runTest {
    val params = TestParams("forcereload5")
    val memoryContent = TestDomain("memory-forcereload5")
    val networkError = RuntimeException("Network error")

    val dataStore = createDataStore(
      networkFetcher = { throw networkError },
      fromMemory = { memoryContent }
    )

    dataStore.collect(params, AsyncDataStore.LoadStrategy.ForceReload).test {
      assertIs<Async.Loading>(awaitItem())
      val errorItem = awaitItem()
      assertIs<Async.Error>(errorItem)
      assertEquals(networkError, errorItem.error)
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `ForceReload should always emit Loading state first`() = runTest {
    val params = TestParams("forcereload6")
    val networkContent = TestDomain("network-forcereload6")
    val emissions = mutableListOf<Async<TestDomain>>()

    val dataStore = createDataStore(
      networkFetcher = { networkContent }
    )

    dataStore.collect(params, AsyncDataStore.LoadStrategy.ForceReload).test {
      emissions.add(awaitItem())
      emissions.add(awaitItem())
      cancelAndIgnoreRemainingEvents()
    }

    assertEquals(2, emissions.size)
    assertIs<Async.Loading>(emissions[0])
    assertIs<Async.Content<TestDomain>>(emissions[1])
  }

  data class TestParams(val id: String)

  data class TestDomain(val value: String)

  private class TestCacheInvalidator : TimeBased.CacheInvalidator<TestParams> {
    val timestamps = mutableMapOf<TestParams, Instant>()

    override suspend fun getCacheTimestamp(params: TestParams): Result<Instant?> {
      return Result.success(timestamps[params])
    }

    override suspend fun setCacheTimestamp(params: TestParams, time: Instant): Result<Unit> {
      timestamps[params] = time
      return Result.success(Unit)
    }
  }

  private fun createDataStore(
    networkFetcher: suspend (TestParams) -> TestDomain = { TestDomain("network-${it.id}") },
    fromStorage: suspend (TestParams) -> TestDomain? = { null },
    intoStorage: suspend (TestParams, TestDomain) -> Unit = { _, _ -> },
    fromMemory: (TestParams) -> TestDomain? = { null },
    intoMemory: (TestParams, TestDomain) -> Unit = { _, _ -> },
    invalidateMemory: (TestParams) -> Unit = { },
  ) = AsyncDataStore(
    networkFetcher = networkFetcher,
    fromStorage = fromStorage,
    intoStorage = intoStorage,
    fromMemory = fromMemory,
    intoMemory = intoMemory,
    invalidateMemory = invalidateMemory,
  )
}
