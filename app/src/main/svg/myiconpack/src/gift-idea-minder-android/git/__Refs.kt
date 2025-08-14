package myiconpack.src.`gift-idea-minder-android`.git

import androidx.compose.ui.graphics.vector.ImageVector
import myiconpack.src.`gift-idea-minder-android`.git.refs.AllIcons
import myiconpack.src.`gift-idea-minder-android`.git.refs.Heads
import myiconpack.src.`gift-idea-minder-android`.git.refs.Remotes
import myiconpack.src.`gift-idea-minder-android`.git.refs.Tags
import myiconpack.src.`gift-idea-minder-android`.gitGroup
import kotlin.collections.List as ____KtList

public object RefsGroup

public val gitGroup.Refs: RefsGroup
  get() = RefsGroup

private var __AllIcons: ____KtList<ImageVector>? = null

public val RefsGroup.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= Heads.AllIcons + Remotes.AllIcons + Tags.AllIcons + listOf()
    return __AllIcons!!
  }
