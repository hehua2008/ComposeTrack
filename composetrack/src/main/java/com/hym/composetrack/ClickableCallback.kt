package com.hym.composetrack

/**
 * @author ganmin.he
 * @date 2022/1/12
 */
interface ClickableCallback {
    fun onClick(layoutNodeInfo: LayoutNodeInfo)

    fun onLongClick(layoutNodeInfo: LayoutNodeInfo)

    fun onDoubleClick(layoutNodeInfo: LayoutNodeInfo)
}
