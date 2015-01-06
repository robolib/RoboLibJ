/*
 * Copyright (c) 2015 noriah <vix@noriah.dev>.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 */

package org.team2583.robolib.input.limitswitch;

/**
 * The Limit Switch System Interface.
 *
 * @author noriah Reuland <vix@noriah.dev>
 */
public interface ILimitSwitchSystem {
    
    /**
     * Can up.
     *
     * @return Can we go Up
     */
    public boolean canUp();
    
    /**
     * Can down.
     *
     * @return Can we go Down
     */ 
    public boolean canDown();
    
}
