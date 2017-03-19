package de.mh.snake.client;

import com.leapmotion.leap.*;
/**
 * Created by zhangzhaoyu on 19/03/2017.
 */


import java.io.IOException;
import java.lang.Math;
import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.State;

class SampleListener extends Listener {
    public void onInit(Controller controller) {
        System.out.println("Initialized");
    }

    public void onConnect(Controller controller) {
        System.out.println("Connected");
    }

    public void onDisconnect(Controller controller) {
        //Note: not dispatched when running in a debugger.
        System.out.println("Disconnected");
    }

    public void onExit(Controller controller) {
        System.out.println("Exited");
    }

    public void onFrame(Controller controller) {
        // Get the most recent frame and report some basic information
        Frame frame = controller.frame();
//        System.out.println("Frame id: " + frame.id()
//                + ", timestamp: " + frame.timestamp()
//                + ", hands: " + frame.hands().count()
//                + ", fingers: " + frame.fingers().count()
//                + ", tools: " + frame.tools().count()
//                + ", gestures " + frame.gestures().count());

        for(Hand hand : frame.hands()) {
            String handType = hand.isLeft() ? "Left hand" : "Right hand";
            //System.out.println("  " + handType + ", id: " + hand.id()
                    //+ ", palm position: " + hand.palmPosition() + ", palm vec: " + hand.palmVelocity() + ", norm:" + hand.palmNormal().roll());
//            float pitch = hand.direction().pitch();
//            float yaw = hand.direction().yaw();
//            System.out.println("pitch:" + pitch + ", yaw:" + yaw);
            double x = hand.palmNormal().getX();
            double y = hand.palmNormal().getY();
            String a = "";
            //System.out.println(x + " " + y);
            if (java.lang.Math.abs(x) - 0.4 > java.lang.Math.abs(y)) {
                if (x < 0){ a = "Left";}
                else if (x > 0) {a = "Right";}
            } else if (java.lang.Math.abs(y) - 0.4 > java.lang.Math.abs(x)) {
                if (y < 0){ a = "Down";}
                else if (y > 0) {a = "Up";}
            }
            System.out.println(a);
        }

//        if (!frame.hands().isEmpty() || !gestures.isEmpty()) {
//            System.out.println("Nothing!");
//        }
    }
}

class TestLeapMotion {
    public static void main(String[] args) {
        // Create a sample listener and controller
        SampleListener listener = new SampleListener();
        Controller controller = new Controller();

        // Have the sample listener receive events from the controller
        controller.addListener(listener);

        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        try {
            System.in.read();


        } catch (IOException e) {
            e.printStackTrace();
        }

        // Remove the sample listener when done
        controller.removeListener(listener);
    }
}