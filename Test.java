package de.mh.snake.client;


import com.fazecast.jSerialComm.*;

import java.awt.*;

/**
 * Created by zhangzhaoyu on 19/03/2017.
 */
public class Test {
    public static void main(String agrs[]) {


        SerialPort comPort = SerialPort.getCommPort("/dev/cu.usbmodem1422");
        comPort.setComPortParameters(115200, 8,1,0);
        comPort.openPort();


        try {
            while (true)
            {
                while (comPort.bytesAvailable() == 0)
                    Thread.sleep(20);
                System.out.println(comPort.isOpen());
                System.out.println(comPort.bytesAvailable());
                byte[] readBuffer = new byte[comPort.bytesAvailable()];
                int numRead = comPort.readBytes(readBuffer, readBuffer.length);
                System.out.println("Read " + numRead + " bytes.");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

}
