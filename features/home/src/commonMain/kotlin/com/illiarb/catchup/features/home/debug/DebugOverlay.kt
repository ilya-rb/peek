package com.illiarb.catchup.features.home.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.core.appinfo.AppDebugToggles
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuit.overlay.OverlayNavigator

expect suspend fun OverlayHost.showDebugOverlay()

@Composable
fun DebugOverlay() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(500.dp)
      .background(MaterialTheme.colorScheme.surface)
  ) {
    Column {
      DebugSettingsItem(
        text = "Enable network delay (3s)",
        checked = AppDebugToggles.networkDelayEnabled,
      ) { checked ->
        AppDebugToggles.networkDelayEnabled = checked
      }
    }
  }
}

@Composable
fun DebugSettingsItem(
  text: String,
  checked: Boolean,
  onCheckedChanged: (Boolean) -> Unit,
) {
  var checkedState by remember { mutableStateOf(checked) }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    Text(
      text = text,
      color = MaterialTheme.colorScheme.onSurface,
    )

    Spacer(Modifier.weight(1f))

    Switch(
      checked = checkedState,
      onCheckedChange = {
        checkedState = !checkedState
        onCheckedChanged(checkedState)
      }
    )
  }
}
