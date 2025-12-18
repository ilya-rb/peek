package com.illiarb.peek.core.data

import app.cash.turbine.test
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

internal class AsyncDataStoreTest {

  @Test
  fun `GIVEN empty cache WHEN collect with CacheFirst THEN emits loading and content from network`() =
    runTest {
      // GIVEN
      val params = TestParams("test1")
      val expectedContent = TestDomain("network-test1")
      val dataStore = createDataStore()

      // WHEN & THEN
      dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
        assertIs<Async.Loading>(awaitItem())
        val contentItem = awaitItem()
        assertIs<Async.Content<TestDomain>>(contentItem)
        assertEquals(expectedContent, contentItem.content)
        awaitComplete()
      }
    }

  @Test
  fun `GIVEN memory cache hit WHEN collect with CacheFirst THEN emits content from memory only`() =
    runTest {
      // GIVEN
      val params = TestParams("test2")
      val memoryContent = TestDomain("memory-test2")
      var networkCallCount = 0

      val dataStore = createDataStore(
        networkFetcher = {
          ++networkCallCount
          TestDomain("network-${it.id}")
        },
        fromMemory = { memoryContent }
      )

      // WHEN & THEN
      dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
        val contentItem = awaitItem()
        assertIs<Async.Content<TestDomain>>(contentItem)
        assertEquals(memoryContent, contentItem.content)
        awaitComplete()
      }

      assertEquals(0, networkCallCount)
    }

  @Test
  fun `GIVEN storage cache hit WHEN collect with CacheFirst THEN emits loading, content from storage, and warms memory`() =
    runTest {
      // GIVEN
      val params = TestParams("test3")
      val storageContent = TestDomain("storage-test3")
      val memoryWarmupCalls = mutableListOf<TestDomain>()

      val dataStore = createDataStore(
        fromStorage = { storageContent },
        intoMemory = { _, domain -> memoryWarmupCalls.add(domain) }
      )

      // WHEN & THEN
      dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
        assertIs<Async.Loading>(awaitItem())
        val contentItem = awaitItem()
        assertIs<Async.Content<TestDomain>>(contentItem)
        assertEquals(storageContent, contentItem.content)
        awaitComplete()
      }

      assertEquals(listOf(storageContent), memoryWarmupCalls)
    }

  @Test
  fun `GIVEN successful network fetch WHEN collect THEN updates both storage and memory`() =
    runTest {
      // GIVEN
      val params = TestParams("test8")
      val networkContent = TestDomain("network-test8")
      val storageUpdates = mutableListOf<TestDomain>()
      val memoryUpdates = mutableListOf<TestDomain>()

      val dataStore = createDataStore(
        networkFetcher = { networkContent },
        intoStorage = { _, domain -> storageUpdates.add(domain) },
        intoMemory = { _, domain -> memoryUpdates.add(domain) }
      )

      // WHEN
      dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
        awaitItem() // Loading
        awaitItem() // Content
        awaitComplete()
      }

      // THEN
      assertEquals(listOf(networkContent), storageUpdates)
      assertEquals(listOf(networkContent), memoryUpdates)
    }

  @Test
  fun `GIVEN network error and no cache WHEN collect THEN emits loading and error`() =
    runTest {
      // GIVEN
      val params = TestParams("test4")
      val networkError = RuntimeException("Network error")

      val dataStore = createDataStore(
        networkFetcher = { throw networkError }
      )

      // WHEN & THEN
      dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
        assertIs<Async.Loading>(awaitItem())
        val errorItem = awaitItem()
        assertIs<Async.Error>(errorItem)
        assertEquals(networkError, errorItem.error)
        awaitComplete()
      }
    }

  @Test
  fun `GIVEN network error with cache WHEN collect THEN emits cache content and ignores error`() =
    runTest {
      // GIVEN
      val params = TestParams("test5")
      val memoryContent = TestDomain("memory-test5")
      val networkError = RuntimeException("Network error")

      val dataStore = createDataStore(
        networkFetcher = { throw networkError },
        fromMemory = { memoryContent }
      )

      // WHEN & THEN
      dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
        val contentItem = awaitItem()
        assertIs<Async.Content<TestDomain>>(contentItem)
        assertEquals(memoryContent, contentItem.content)
        awaitComplete()
      }
    }

  @Test
  fun `GIVEN CacheOnly strategy and no cache WHEN collect THEN fetches from network`() =
    runTest {
      // GIVEN
      val params = TestParams("test6")
      val networkContent = TestDomain("network-test6")

      val dataStore = createDataStore(
        networkFetcher = { networkContent }
      )

      // WHEN & THEN
      dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheOnly).test {
        assertIs<Async.Loading>(awaitItem())
        val contentItem = awaitItem()
        assertIs<Async.Content<TestDomain>>(contentItem)
        assertEquals(networkContent, contentItem.content)
        awaitComplete()
      }
    }

  @Test
  fun `GIVEN CacheOnly strategy with cache WHEN collect THEN returns cache without network call`() =
    runTest {
      // GIVEN
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

      // WHEN & THEN
      dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheOnly).test {
        val contentItem = awaitItem()
        assertIs<Async.Content<TestDomain>>(contentItem)
        assertEquals(memoryContent, contentItem.content)
        awaitComplete()
      }

      assertEquals(0, networkCallCount)
    }

  @Test
  fun `GIVEN multiple concurrent collectors with same params WHEN collect THEN shares single flow and makes one network call`() =
    runTest {
      // GIVEN
      val params = TestParams("shared")
      var networkCallCount = 0
      val networkContent = TestDomain("network-shared")

      val dataStore = createDataStore(
        networkFetcher = {
          ++networkCallCount
          delay(100) // Simulate network delay
          networkContent
        }
      )

      // WHEN
      val job1 = launch {
        dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
          assertIs<Async.Loading>(awaitItem())
          val contentItem = awaitItem()
          assertIs<Async.Content<TestDomain>>(contentItem)
          assertEquals(networkContent, contentItem.content)
          awaitComplete()
        }
      }

      val job2 = launch {
        dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
          assertIs<Async.Loading>(awaitItem())
          val contentItem = awaitItem()
          assertIs<Async.Content<TestDomain>>(contentItem)
          assertEquals(networkContent, contentItem.content)
          awaitComplete()
        }
      }

      val job3 = launch {
        dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
          assertIs<Async.Loading>(awaitItem())
          val contentItem = awaitItem()
          assertIs<Async.Content<TestDomain>>(contentItem)
          assertEquals(networkContent, contentItem.content)
          awaitComplete()
        }
      }

      // THEN
      job1.join()
      job2.join()
      job3.join()

      assertEquals(1, networkCallCount) // Only one network call despite 3 collectors
    }

  @Test
  fun `GIVEN different params WHEN collect concurrently THEN makes separate network calls`() =
    runTest {
      // GIVEN
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

      // WHEN
      val results = mutableListOf<List<Async<TestDomain>>>()

      val job1 = launch {
        results.add(dataStore.collect(params1, AsyncDataStore.LoadStrategy.CacheFirst).toList())
      }

      val job2 = launch {
        results.add(dataStore.collect(params2, AsyncDataStore.LoadStrategy.CacheFirst).toList())
      }

      job1.join()
      job2.join()

      // THEN
      assertEquals(2, networkCallCount) // Two network calls for different params
      assertEquals(2, results.size)
    }

  @Test
  fun `GIVEN same params but different strategies WHEN collect concurrently THEN makes separate network calls`() =
    runTest {
      // GIVEN
      val params = TestParams("test")
      var networkCallCount = 0

      val dataStore = createDataStore(
        networkFetcher = {
          ++networkCallCount
          delay(50)
          TestDomain("network-${it.id}")
        }
      )

      // WHEN
      val job1 = launch {
        dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).toList()
      }

      val job2 = launch {
        dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheOnly).toList()
      }

      job1.join()
      job2.join()

      // THEN
      assertEquals(2, networkCallCount) // Different strategies = different flows
    }

  @Test
  fun `GIVEN updateLocal called WHEN collect with same params THEN invalidates shared flows`() =
    runTest {
      // GIVEN
      val params = TestParams("test")
      var networkCallCount = 0
      val updatedContent = TestDomain("updated")

      val dataStore = createDataStore(
        networkFetcher = {
          ++networkCallCount
          TestDomain("network-${it.id}")
        }
      )

      // WHEN - First collection
      dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
        awaitItem() // Loading
        awaitItem() // Content
        awaitComplete()
      }

      // Update local data
      dataStore.updateLocal(params, updatedContent)

      // Second collection after update
      dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
        awaitItem() // Loading
        awaitItem() // Content
        awaitComplete()
      }

      // THEN
      assertEquals(2, networkCallCount) // Should make new network call after invalidation
    }

  @Test
  fun `GIVEN invalidateMemory called WHEN collect with same params THEN invalidates shared flows`() =
    runTest {
      // GIVEN
      val params = TestParams("test")
      var networkCallCount = 0

      val dataStore = createDataStore(
        networkFetcher = {
          ++networkCallCount
          TestDomain("network-${it.id}")
        }
      )

      // WHEN - First collection
      dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
        awaitItem() // Loading
        awaitItem() // Content
        awaitComplete()
      }

      // Invalidate memory
      dataStore.invalidateMemory(params)

      // Second collection after invalidation
      dataStore.collect(params, AsyncDataStore.LoadStrategy.CacheFirst).test {
        awaitItem() // Loading
        awaitItem() // Content
        awaitComplete()
      }

      // THEN
      assertEquals(2, networkCallCount) // Should make new network call after invalidation
    }

  data class TestParams(val id: String)

  data class TestDomain(val value: String)

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
