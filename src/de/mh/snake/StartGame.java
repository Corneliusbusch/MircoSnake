package de.mh.snake;

import de.mh.snake.client.SnakeClient;
import de.mh.snake.server.ServerHandler;
import de.mh.snake.server.SnakeServer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by nicole on 18.03.17.
 */
public class StartGame extends JFrame {

    private JPanel contentPane;
    public JButton buttonStart;
    public JButton buttonStop;
    private SnakeClient player1;
    private SnakeClient player2;
    private SnakeClient player3;
    private SnakeClient player4;
    private SnakeClient[] players = {player1, player2, player3, player4};
    private int noOfPlayers = 0;


    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    StartGame frame = new StartGame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public StartGame() {
        setTitle("Start Multiplayer Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.NORTH);
        setAlwaysOnTop (true);

        buttonStart = new JButton("Add Player");
        buttonStart.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (noOfPlayers < 4) {

                    //Run new instance of client
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            try {
                                if (noOfPlayers == 0) {
                                    player1 = new SnakeClient();
                                    player1.setVisible(true);
                                } else if (noOfPlayers == 1) {
                                    player2 = new SnakeClient();
                                    //player1.setVisible(false);
                                    player2.setVisible(true);
                                } else if (noOfPlayers == 2) {
                                    player3 = new SnakeClient();
                                    //player2.setVisible(false);
                                    player3.setVisible(true);
                                } else {
                                    player4 = new SnakeClient();
                                    //player3.setVisible(false);
                                    player4.setVisible(true);
                                    setAlwaysOnTop(false);
                                }
                                noOfPlayers++;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }
        });
        panel.add(buttonStart);

        buttonStop = new JButton("Stop All");
        buttonStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (player1 != null) {
                    player1.stopClient();
                } else if (player2 != null) {
                    player2.stopClient();
                } else if (player3 != null) {
                    player3.stopClient();
                } else if (player4 != null) {
                    player4.stopClient();
                }
            }
        });
        panel.add(buttonStop);

        JLabel label1 = new JLabel("This is an interactive SNAKE game for a maximum of 4 players.");
        contentPane.add(label1);
    }
}
