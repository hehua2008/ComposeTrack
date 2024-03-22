package com.hym.composetrack

/**
 * @author hehua2008
 * @date 2022/1/7
 */
interface OnAttachOnDetachCallback {
    /**
     * This function will be called on the main thread, so don't do hard work.
     */
    fun onAttach(nodeTrackId: NodeTrackId)

    /**
     * This function will be called on the main thread, so don't do hard work.
     */
    fun onDetach(nodeTrackId: NodeTrackId)
}