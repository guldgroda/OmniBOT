package OmniBOT;


import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

/**
 * Testing the compass pilot.
 * @author Roger Glassey
 */
public class TestCompass
{
   public static void main(String[] args)
    {
      LCD.drawString("CompassPilot Test",0,0);
      
   // get a port instance
      Port port = LocalEV3.get().getPort("S1");

      // Get an instance of the Ultrasonic EV3 sensor
      HiTechnicCompass compass = new HiTechnicCompass(port);

      // get an instance of this sensor in measurement mode
      SampleProvider angle = compass.getMode("Compass");

      // initialize an array of floats for fetching samples. 
      // Ask the SampleProvider how long the array should be
      float[] sample = new float[angle.sampleSize()];

      LCD.clear();
      LCD.drawString("Calibration",0,0);
      LCD.drawString("Press any button",0,1);
      Button.waitForAnyPress();
      LCD.clear(1);
      compass.startCalibration();
      Motor.A.setSpeed(35);
      Motor.B.setSpeed(35);
      Motor.C.setSpeed(35);
      Motor.A.forward();
      Motor.B.forward();
      Motor.C.forward();
      Delay.msDelay(47000);
      Motor.A.stop();
      Motor.B.stop();
      Motor.C.stop();
      compass.stopCalibration();
      LCD.drawString("Calibration ended",0,0);
      LCD.drawString("Press any button",0,1);
      Button.waitForAnyPress();
      LCD.clear();
      LCD.drawString("ESC to Quit", 0, 0);
      LCD.drawString("Any other button update",0,1);
      while (Button.ESCAPE.isUp())
      {
         LCD.clear(2);
         LCD.drawString("Tst Compass", 0, 2);
         angle.fetchSample(sample, 0);
         LCD.drawString((int)Math.round(sample[0])+"",4,3);
         Button.waitForAnyPress();
      }
      compass.close();
    }
}