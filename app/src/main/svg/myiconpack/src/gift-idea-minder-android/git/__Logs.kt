package myiconpack.src.`gift-idea-minder-android`.git

import androidx.compose.ui.graphics.vector.ImageVector
import myiconpack.src.`gift-idea-minder-android`.git.logs.AllIcons
import myiconpack.src.`gift-idea-minder-android`.git.logs.Refs
import myiconpack.src.`gift-idea-minder-android`.gitGroup
import kotlin.collections.List as ____KtList

public object LogsGroup

public val gitGroup.Logs: LogsGroup
  get() = LogsGroup

private var __AllIcons: ____KtList<ImageVector>? = null

public val LogsGroup.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= Refs.AllIcons + listOf()
    return __AllIcons!!
  }
