package com.steelkiwi.library.listener;

/**
 * Created by yaroslav on 6/29/17.
 */

public interface OnStateListener {
    /**
     * On product count change
     * */
    void onCountChange(int count);

    /**
     * On some count of product was confirmed
     * */
    void onConfirm(int count);

    /**
     * User close view
     * */
    void onClose();
}
