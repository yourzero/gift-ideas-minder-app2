package myiconpack.src.`gift-idea-minder-android`.git.logs.refs

import androidx.compose.ui.graphics.vector.ImageVector
import myiconpack.src.`gift-idea-minder-android`.git.logs.RefsGroup
import myiconpack.src.`gift-idea-minder-android`.git.logs.refs.remotes.AllIcons
import myiconpack.src.`gift-idea-minder-android`.git.logs.refs.remotes.Origin
import kotlin.collections.List as ____KtList

public object RemotesGroup

public val RefsGroup.Remotes: RemotesGroup
  get() = RemotesGroup

private var __AllIcons: ____KtList<ImageVector>? = null

public val RemotesGroup.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= Origin.AllIcons + listOf()
    return __AllIcons!!
  }
