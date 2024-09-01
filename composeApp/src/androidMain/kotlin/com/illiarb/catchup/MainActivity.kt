package com.illiarb.catchup

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.illiarb.catchup.di.AndroidUiComponent
import com.illiarb.catchup.di.create

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge(SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT))

    super.onCreate(savedInstanceState)

    val appComponent = applicationContext.appComponent()
    val uiComponent = AndroidUiComponent::class.create(activity = this, appComponent = appComponent)

    setContent {
      App(uiComponent, appComponent.imageLoader)
    }
  }
}
