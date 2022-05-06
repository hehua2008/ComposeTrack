package com.hym.composetrack

/**
 * @author ganmin.he
 * @date 2022/1/7
 */
interface OnAttachOnDetachCallback {
    /**
     * This function will be called on the main thread, so don't do hard work.
     */
    fun onAttach(nodeIdentity: NodeIdentity)

    /**
     * This function will be called on the main thread, so don't do hard work.
     */
    fun onDetach(nodeIdentity: NodeIdentity)
}