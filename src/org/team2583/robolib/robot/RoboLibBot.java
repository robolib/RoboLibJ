/*
 * Copyright (c) 2015 Westwood Robotics <code.westwoodrobotics@gmail.com>.
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

package org.team2583.robolib.robot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.team2583.robolib.communication.FRCNetworkCommunicationsLibrary;
import org.team2583.robolib.communication.FRCNetworkCommunicationsLibrary.tInstances;
import org.team2583.robolib.communication.FRCNetworkCommunicationsLibrary.tResourceType;
import org.team2583.robolib.communication.UsageReporting;
import org.team2583.robolib.control.DriverStation;
import org.team2583.robolib.util.log.ILogger;
import org.team2583.robolib.util.log.Logger;

import edu.wpi.first.wpilibj.HLUsageReporting;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.internal.HardwareHLUsageReporting;
import edu.wpi.first.wpilibj.internal.HardwareTimer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

/**
 * A better version of the WPILib IterativeRobot class.
 * It replaces RobotBase and handles much of the robots functions.
 * It also handles any compressor the robot may have, Loading the
 * robot and sending NetworkTable values about the current state.
 * 
 * This Class handles switching of Game Modes better than the default
 * IterativeRobot. It has a more complex program flow, however it runs faster
 * and uses less memory.
 * 
 * You must make your own class and have it extend this class to use it.
 * The constructor must call the Super class with either no arguments, 
 * or two strings representing the Name of the robot and the version of
 * the code.
 *
 * @author Austin Reuland <amreuland@gmail.com>
 * @since 0.1.0
 */
public class RoboLibBot {

    /** The Constant MAJOR_VERSION. */
    public static final int MAJOR_VERSION = 2;
    
    /** The Constant MINOR_VERSION. */
    public static final int MINOR_VERSION = 0;
    
    /** The Constant PATCH_VERSION. */
    public static final int PATCH_VERSION = 1;

    /** The m_name. */
    private static String m_name;
    
    /** The m_version. */
    private static String m_version;
    
    /** The m_log. */
    private static ILogger m_log;
    
    /** The m_run. */
    private static boolean m_run = true;
    
    /** The m_table. */
    private static ITable m_table;
    
    /** The Constant m_ds. */
    private static final DriverStation m_ds = DriverStation.getInstance();
    
    /** The Constant m_modeSwitcher. */
    private static final ModeSwitcher m_modeSwitcher = ModeSwitcher.getInstance();
    
    /** The m_instance. */
    private static RoboLibBot m_instance;

    /**
     * Robot Class Method.
     */
    protected RoboLibBot(){
        this("RoboLibBot", "1.0.0");
    }

    /**
     * Robot Class Method.
     *
     * @param name Name of the Robot
     */
    protected RoboLibBot(String name){
        this(name, "1.0.0");
    }
    
    /**
     * Robot Class Method.
     *
     * @param name Name of the Robot
     * @param version Version number of the robot code
     */
    protected RoboLibBot(String name, String version){
        m_name = name;
        m_version = version;

        NetworkTable.setServerMode();
        NetworkTable.getTable("");
        NetworkTable.getTable("LiveWindow").getSubTable("~STATUS~").putBoolean("LW Enabled", false);

        m_table = NetworkTable.getTable("Robot");
        m_log = Logger.get(this, "Robot");
        m_instance = this;
    }

    /**
     * Send a message to the Console, and to the Dashboard.
     * @param msg The message to be sent.
     */
    protected void msg(String msg){
        m_log.info(msg);
    }

    /**
     * Send a message to the Console, and to the Dashboard.
     * @param msg The message to be sent.
     */
    protected void debug(String msg){
        m_log.debug(msg);
    }
    
    /**
     * Enable Debug Messages.
     */
    public static void enableDebug(){
        m_log.enableDebug();
    }

    /**
     * Enable or Disable Debug Messages.
     *
     * @param debug Enable or Disable
     */
    public static void enableDebug(boolean debug){
        m_log.enableDebug(debug);
    }

    /**
     * Send out an error to the console. Crash the Robot.
     * Lets hope we don't get too many of these
     * @param msg The message to be sent with the RuntimeException
     */
    protected void error(String msg){
        error(msg, new RuntimeException());
    }
    
    /**
     * Send out an error to the console. Crash the Robot.
     * Lets hope we don't get too many of these
     * @param msg The message to be sent with the RuntimeException
     * @param e The throwable object to send with this message
     */
    protected void error(String msg, Throwable e){
        m_run = false;
        m_log.error(msg, e);
    }
    
    /**
     * Send out fatal message to the console. Crash the Robot.
     * Lets hope we don't get too many of these
     * @param msg The message to be sent with the RuntimeException
     */
    protected void fatal(String msg){
        fatal(msg, new RuntimeException());
    }
    
    /**
     * Send out a fatal message to the console. Crash the Robot.
     *  Lets hope we don't get too many of these
     * @param msg The message to be sent with the RuntimeException
     * @param e The throwable object to send with this message
     */
    protected void fatal(String msg, Throwable e){
        m_run = false;
        m_log.fatal(msg, e);
    }
    
    /**
     * Kill the robot.
     */
    public static void die(){
        m_run = false;
    }
    
    /**
     * Get the main NetworkTable table for the robot.
     * @return a networktable ITable
     */
    public static ITable getRobotTable(){
        return m_table;
    }

    /**
     * User Initialization code.
     * 
     * This is run before the robot runs through its modes.
     */
    public void robotInit(){
        debug("Default Robot.robotInit() method... Overload me!");
    }

    /**
     * Get the current driver station indicated mode.
     * @return the driver station mode as a {@link GameMode}
     */
    public static GameMode getDSMode(){
        if(isDisabled())
            return GameMode.DISABLED;
        else if(isTest())
            return GameMode.TEST;
        else if(isAutonomous())
            return GameMode.AUTON;
        else
            return GameMode.TELEOP;
    }

    /**
     * The Main method for the robot.
     * 
     * Starting point for the applications. Starts the OtaServer and then runs
     * the robot.
     * 
     * This is called to start the robot. It should never exit.
     * If it does exit, it will first throw several exceptions to kill the robot.
     * We don't want an out of control robot.
     *
     * @param args the arguments
     */
    public static void main(String args[]) {
        
        m_instance.msg("RoboLibJ v" + MAJOR_VERSION + "." + MINOR_VERSION + "." + PATCH_VERSION);
        m_instance.msg("Starting " + m_name);
        
        FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationReserve();
        Timer.SetImplementation(new HardwareTimer());
        HLUsageReporting.SetImplementation(new HardwareHLUsageReporting());
//        RobotState.SetImplementation(DriverStation.getInstance());

        UsageReporting.report(tResourceType.kResourceType_Language, tInstances.kLanguage_Java);
        
            
        m_instance.msg("Initializing Robot Network Table and Data");
        try{
            m_table.putString("name", m_name);
            m_table.putString("version", m_version);
        }catch(Throwable t){
            m_instance.fatal("Could not set Robot Name and Version in the Network Table. Did Something Screw Up?", t);
        }
        

        //Run User initialization
        m_instance.msg("Running User Initialization code");
        try{
            m_instance.robotInit();
        }catch(Throwable t){
            DriverStation.reportError("ERROR Unhandled exception instantiating robot " + m_name + " " + t.toString() + " at " + Arrays.toString(t.getStackTrace()), false);
            m_instance.fatal("Error running User Init Code", t);
        }

        checkVersionFile(new File("/tmp/frc_versions/FRC_Lib_Version.ini"));
        UsageReporting.report(tResourceType.kResourceType_Framework, tInstances.kFramework_Iterative);
        LiveWindow.setEnabled(false);

        m_instance.msg("Initializing Robot Modes");
        m_modeSwitcher.init();

        m_instance.msg(m_name + ", Version " + m_version + " Running");
        FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramStarting();
        
        m_instance.msg("Starting Main Loop");
        try{
            while(m_run){
                GameMode mode = getDSMode();
                if(m_modeSwitcher.inNewMode(mode)){
                    m_modeSwitcher.switchMode(mode);
                }
                
                if(m_ds.isNewControlData()){
                    m_modeSwitcher.run();
                }
                m_ds.waitForData();
            }
            
        }catch(Throwable t){
            DriverStation.reportError("ERROR Unhandled exception: " + t.toString() + " at " + Arrays.toString(t.getStackTrace()), false);
            m_instance.fatal("Error in Main Loop. Something should have caught this!!!", t);
        }finally{
            m_log.fatal("ROBOTS DON'T QUIT!!!", "Exited Main Loop");
        }
        System.exit(1);
    }
    
    /**
     * Check version file.
     *
     * @param file the file
     */
    @SuppressWarnings("null")
    private static void checkVersionFile(File file){
        
        if(!file.exists()){
            writeVersionFile(file);
        }else{
            byte[] data = null;
            try{
                FileInputStream input = new FileInputStream(file);
                input.read(data);
                input.close();
            }catch (IOException ex){
                ex.printStackTrace();
            }
            if(!data.equals("2015 Java 1.0.0".getBytes())){
                file.delete();
                writeVersionFile(file);
            }
        }
        
    }
    
    /**
     * Write version file.
     *
     * @param file the file
     */
    private static void writeVersionFile(File file){
        FileOutputStream output = null;
        try {
            file.createNewFile();
            output = new FileOutputStream(file);
            output.write("2015 Java 1.0.0".getBytes());

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                }
            }
        }
    }
    
    /**
     * Free the resources for a RoboLibBot class.
     */
    public void free() {
    }

    /**
     * Checks if is simulation.
     *
     * @return If the robot is running in simulation.
     */
    public static boolean isSimulation() {
        return false;
    }

    /**
     * Checks if is real.
     *
     * @return If the robot is running in the real world.
     */
    public static boolean isReal() {
        return true;
    }

    /**
     * Determine if the Robot is currently disabled.
     *
     * @return True if the Robot is currently disabled by the field controls.
     */
    public static boolean isDisabled() {
        return DriverStation.isDisabled();
    }

    /**
     * Determine if the Robot is currently enabled.
     *
     * @return True if the Robot is currently enabled by the field controls.
     */
    public static boolean isEnabled() {
        return DriverStation.isEnabled();
    }

    /**
     * Determine if the robot is currently in Autonomous mode.
     *
     * @return True if the robot is currently operating Autonomously as
     * determined by the field controls.
     */
    public static boolean isAutonomous() {
        return DriverStation.isAutonomous();
    }

    /**
     * Determine if the robot is currently in Test mode.
     *
     * @return True if the robot is currently operating in Test mode as
     * determined by the driver station.
     */
    public static boolean isTest() {
        return DriverStation.isTest();
    }
    
    /**
     * Determine if the robot is currently Emergency stopped.
     * 
     * @return True if the robot is currently emergency stopped.
     */
    public static boolean isEStopped(){
        return DriverStation.isEStopped();
    }

    /**
     * Determine if the robot is currently in Operator Control mode.
     *
     * @return True if the robot is currently operating in Tele-Op mode as
     * determined by the field controls.
     */
    public static boolean isOperatorControl() {
        return DriverStation.isOperatorControl();
    }
    
    /**
     * Check on the overall status of the system.
     *
     * @return Is the system active (i.e. PWM motor outputs, etc. enabled)?
     */
    public static boolean isSysActive() {
        return DriverStation.isSysActive();
    }
    
    /**
     * Check if the system is browned out.
     * 
     * @return True if the system is browned out
     */
    public static boolean isBrownedOut() {
        return DriverStation.isBrownedOut();
    }

}
