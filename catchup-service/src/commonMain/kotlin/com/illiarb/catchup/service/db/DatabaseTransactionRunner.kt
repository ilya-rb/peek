package com.illiarb.catchup.service.db

import com.illiarb.catchup.service.Database
import me.tatarka.inject.annotations.Inject

@Inject
class DatabaseTransactionRunner(
  private val db: Database,
) {

  operator fun <T> invoke(block: () -> T): T {
    return db.transactionWithResult {
      block()
    }
  }
}