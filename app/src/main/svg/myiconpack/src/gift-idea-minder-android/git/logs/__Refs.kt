package myiconpack.src.`gift-idea-minder-android`.git.logs

import androidx.compose.ui.graphics.vector.ImageVector
import myiconpack.src.`gift-idea-minder-android`.git.LogsGroup
import myiconpack.src.`gift-idea-minder-android`.git.logs.refs.AllIcons
import myiconpack.src.`gift-idea-minder-android`.git.logs.refs.Heads
import myiconpack.src.`gift-idea-minder-android`.git.logs.refs.Remotes
import kotlin.collections.List as ____KtList

public object RefsGroup

public val LogsGroup.Refs: RefsGroup
  get() = RefsGroup

private var __AllIcons: ____KtList<ImageVector>? = null

public val RefsGroup.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= Heads.AllIcons + Remotes.AllIcons + listOf()
    return __AllIcons!!
  }
