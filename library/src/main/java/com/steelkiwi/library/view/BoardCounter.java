package com.steelkiwi.library.view;

/**
 * Created by yaroslav on 6/29/17.
 */

public interface BoardCounter {
    void reset();
    void increment();
    void decrement();
    int getCount();
    void setCount(int count);
}
