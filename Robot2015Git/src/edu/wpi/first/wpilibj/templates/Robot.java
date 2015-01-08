/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

//This is the version of the RoboRIO code for 2015 that is up-to-date with Github.
//Use this version, not the local version

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;

//import edu.wpi.first.wpilibj.buttons.JoystickButton; Not needed (using raw input)


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends SimpleRobot {
    /**
     * This function is called once each time the robot enters autonomous mode.
     */
	
		 Joystick driveStick = new Joystick(0);
		 Joystick liftStick = new Joystick(1);
		 
		 /*trigger is ground level each stage is a different set height for the containers to stack. Still needs calculations
		 JoystickButton button2 = new JoystickButton(liftStick, 2);
		 JoystickButton button3 = new JoystickButton(liftStick, 3);
		 JoystickButton button4 = new JoystickButton(liftStick, 2);
		 JoystickButton button5 = new JoystickButton(liftStick, 5); Not needed (using raw input) */
	
		 RobotDrive chassis = new RobotDrive(1, 2, 3, 4); //four motor drive config (we use mecanum drive)
		 
		 Talon frontLeft = new Talon(1);
		 Talon frontRight = new Talon(2);
		 Talon rearLeft = new Talon(3);
		 Talon rearRight = new Talon(4);
		 
		 Jaguar lift = new Jaguar(5);
		 Jaguar test = new Jaguar(11);
		 
		 
		 //sensor constants n' stuff
		 //constants are CAPITALIZED so that we know they are constants
		 private final double GROUND_SENSOR_DISTANCE = 0.1; //ground level | This value is to give a buffer from hitting the ground
		 private final double FIRST_SENSOR_DISTANCE = 12.2; //1st level 
		 private final double SECOND_SENSOR_DISTANCE = 24.3; //2nd level
		 private final double THIRD_SENSOR_DISTANCE = 36.4; //3rd level
		 private final double FOURTH_SENSOR_DISTANCE = 48.5; //4th level
		 private final double FIFTH_SENSOR_DISTANCE = 56; //5th level (container)
		 
		 
		 
		 Ultrasonic liftSensor = new Ultrasonic(6, 6);
		 double liftHeight; //in inches | totes = 12.1 in, containers = 29 in
		 double desiredHeight = GROUND_SENSOR_DISTANCE; //the height we want the liftHeight to be at (this variable simplifies stuff)
		 
		 private boolean[] buttonsPressed = new boolean[11]; //this array keeps track of buttons pressed, there are 11 in total
		 /* Button Raw IDs */
			//public final int FIRE_BUTTON = 1;
			public final int LIFT_TO_ONE = 2; //Right stick only
			public final int LIFT_TO_TWO = 3; //Right stick only
			public final int LIFT_TO_THREE = 4;
			public final int LIFT_TO_FOUR = 5;
			public final int LIFT_TO_FIVE = 10;
			//public final int CAMERA_TOGGLE = 3;
		
			//non-const buttons (these are not button IDs)
			public boolean triggerPressed = false;
	
    public void autonomous() {
				liftSensor.setAutomaticMode(true);
				liftSensor.setEnabled(true);
				
        chassis.setSafetyEnabled(false);
        chassis.mecanumDrive_Polar(0.5, 0.0, 0.0);
        Timer.delay(2.0);
        chassis.mecanumDrive_Polar(0.0, 0.0, 0.0);
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
			System.out.println("Robot now in driver mode!");
			liftSensor.setAutomaticMode(true);
			liftSensor.setEnabled(true);
			chassis.setSafetyEnabled(true);
			
			//operator control loop - robot accepts input as long as inside this loop
      while (isOperatorControl() && isEnabled()) 
			{
				/*
				UPDATE DA SENSOR INFO-------------------
				*/
				liftHeight = liftSensor.getRangeInches();
				//--------------------------------------
				
				/*
				GET DA INPUT WITH THIS LOOP-------------
				*/
				for(int i=0;i<buttonsPressed.length;i++)
				{
					buttonsPressed[i] = liftStick.getRawButton(i);
				}
				triggerPressed = liftStick.getTrigger();
				//---------------------------------------
				
				
				/*
				NOW DO INPUT ACTIONS--------------------
				*/
				chassis.mecanumDrive_Polar(driveStick.getMagnitude(), driveStick.getDirectionDegrees(), driveStick.getTwist());
				for(int i=0;i<buttonsPressed.length;i++)
				{
					//are any buttons pressed?
					if(buttonsPressed[i])
					{
						//if a button is pressed, let's look at the number of the button
						switch(i)
						{
							case LIFT_TO_ONE: //if the number corresponds with button for lift lvl 1
								desiredHeight = FIRST_SENSOR_DISTANCE;
								break;
							case LIFT_TO_TWO: //button is lift lvl 2
								desiredHeight = SECOND_SENSOR_DISTANCE;
								break;
							case LIFT_TO_THREE: //lift lvl 3
								desiredHeight = THIRD_SENSOR_DISTANCE;
								break;
							case LIFT_TO_FOUR: //lift lvl 4
								desiredHeight = FOURTH_SENSOR_DISTANCE;
								break;
							case LIFT_TO_FIVE: //lift lvl 5
								desiredHeight = FIFTH_SENSOR_DISTANCE;
								break;
							default:
								//do nothing
								break;
						}
					}
				}
				if(triggerPressed)
				{
					desiredHeight = GROUND_SENSOR_DISTANCE;
				}
				//now do the actual lifting
				if(liftHeight > desiredHeight)
				{
					lift.set(-0.5);
				}
				if(liftHeight < desiredHeight)
				{
					lift.set(0.5);
				}
				if(liftHeight == desiredHeight)
				{
					//will it ever actually EXACTLY EQUAL the number we want?
					//maybe we should use an 'approximate equals' method instead of ==
					System.out.println("Desired height for lift reached:" + desiredHeight);
					lift.stopMotor();
				}
				//---------------------------------------
		} //<--end operator control loop
	}
  /**
     * This function is called once each time the robot enters test mode.
    */
    public void test() {
				liftSensor.setAutomaticMode(true);
				liftSensor.setEnabled(true);
				double testHeight;
				while (isTest() && isEnabled()) {
					testHeight = liftSensor.getRangeInches();
					System.out.println(testHeight + " inches");
		}
	}
}