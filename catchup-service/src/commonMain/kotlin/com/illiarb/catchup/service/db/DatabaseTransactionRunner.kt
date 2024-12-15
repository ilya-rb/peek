package com.illiarb.catchup.service.db

import com.illiarb.catchup.service.Database
import me.tatarka.inject.annotations.Inject

@Inject
public class DatabaseTransactionRunner(
  private val db: Database,
) {

  public operator fun <T> invoke(block: () -> T): T {
    return db.transactionWithResult {
      block()
    }
  }
}