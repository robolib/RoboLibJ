/*
 * Copyright (c) 2014 noriah vix@noriah.dev.
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

package org.warriors2583.robolib.robot;

import edu.wpi.first.wpilibj.communication.FRCControl;
import org.warriors2583.robolib.Robot;
import org.warriors2583.robolib.robot.ModeSwitcher.GameMode;

/**
 *
 * @author noriah Reuland
 */
public abstract class RobotMode {
    
    protected RobotMode(){
    }
    
    protected RobotMode(GameMode mode){
        ModeSwitcher.getInstance().add(mode, this);
    }
    
    public void _init(){
        Robot.stopCompressor();
        init();
    }
    
    public abstract void init();
    
    public void _run(){
        FRCControl.observeUserProgramDisabled();
        run();
    }
    
    public abstract void run();
    
    public void _end(){
        end();
    }
    
    public abstract void end();
    
}