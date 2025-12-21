package com.illiarb.peek.core.types

import com.illiarb.peek.core.arch.CommonParcelable
import com.illiarb.peek.core.arch.CommonParcelize

@CommonParcelize
public data class Url(val url: String) : CommonParcelable {
  init {
    require(url.isNotEmpty())
  }
}
