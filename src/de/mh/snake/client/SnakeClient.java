package de.mh.snake.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SnakeClient extends JFrame {

	public ClientHandler handler;
	private JPanel contentPane;
	public Board board;
	
	
	/** Snake Multiplayer
	 * - LAN based Snake multiplayer game
	 * @author Micha Hanselmann
	 * Copyright (c) 2016 Micha Hanselmann
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SnakeClient frame = new SnakeClient();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SnakeClient() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				handler.stop();
			}
		});
		setTitle("Snake Multiplayer (© 2016 Micha Hanselmann)");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		board = new Board();
		contentPane.add(board, BorderLayout.CENTER);
		pack();
		
		handler = new ClientHandler(this);
		handler.start();
		
	}

}
