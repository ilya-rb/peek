package com.illiarb.peek.features.tasks.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentLate
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.illiarb.peek.features.tasks.domain.Task
import com.illiarb.peek.uikit.core.atom.BoxListItemContainer
import com.illiarb.peek.uikit.core.components.cell.RowCell
import com.illiarb.peek.uikit.core.components.cell.RowCellContract
import com.illiarb.peek.uikit.core.components.cell.RowCellContract.StartContent
import com.illiarb.peek.uikit.core.components.date.DateFormats
import com.illiarb.peek.uikit.core.components.swipe.SwipeToDeleteContainer
import com.illiarb.peek.uikit.core.image.VectorIcon
import com.illiarb.peek.uikit.core.model.TextModel
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_icon_task_overdue
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.days

@Composable
internal fun LazyItemScope.TaskCell(
  currentDate: LocalDate,
  enabled: Boolean,
  index: Int,
  itemsCount: Int,
  onTaskDeleted: (Task) -> Unit,
  onTaskToggled: (Task) -> Unit,
  selectedDate: LocalDate,
  showOverdue: Boolean,
  task: Task,
  modifier: Modifier = Modifier,
) {
  SwipeToDeleteContainer(
    onDelete = { onTaskDeleted(task) },
    enabled = enabled && !task.completed,
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .animateItem(fadeInSpec = null)
  ) {
    BoxListItemContainer(
      index = index,
      itemsCount = itemsCount,
    ) {
      RowCell(
        modifier = Modifier.fillMaxWidth().clickable { onTaskToggled(task) },
        title = TextModel(
          text = task.title,
          decoration = if (task.completed) {
            TextDecoration.LineThrough
          } else {
            TextDecoration.None
          }
        ),
        subtitle = if (showOverdue && selectedDate != task.createdForDate) {
          val overdueDate = currentDate.minus(task.createdForDate).days.days

          TextModel(
            text = DateFormats.formatTimestamp(overdueDate),
            color = MaterialTheme.colorScheme.error,
          )
        } else {
          null
        },
        startContent = StartContent.Checkbox(
          checked = task.completed,
          enabled = enabled,
        ),
        endContent = if (showOverdue && task.createdForDate < selectedDate) {
          RowCellContract.EndContent.Icon(
            VectorIcon(
              Icons.Filled.AssignmentLate,
              contentDescription = stringResource(Res.string.acsb_icon_task_overdue),
              tint = MaterialTheme.colorScheme.error,
            )
          )
        } else {
          null
        }
      )
    }
  }
}
