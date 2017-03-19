package de.mh.snake.client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import de.mh.snake.Request;
import de.mh.snake.Response;
import de.mh.snake.server.Game;
import com.leapmotion.leap.*;

public class ClientHandler extends com.leapmotion.leap.Listener implements KeyListener{

	private SnakeClient snakeClient;
	Client client = new Client(8192, 8192);
	public int id = 0;
	public int score = 0;
	public int highscore = 0;
	public boolean ingame = false;
	public Timer reconnectionTimer;
	Controller controller = new Controller();


	public ClientHandler(SnakeClient snakeClient) {
		this.snakeClient = snakeClient;
		controller.addListener(this);
	}

	public void start() {

		client.start();
		try {

			Kryo kryo = client.getKryo();
			kryo.register(Request.class);
			kryo.register(Response.class);

			//InetAddress address = client.discoverHost(54001, 5000);
			InetAddress address = InetAddress.getByName("52.43.213.42");
			client.connect(5000, address, 54000, 54001);

			client.addListener(new Listener() {
				public void received(Connection connection, Object object) {
					if (object instanceof Response) {
						Response response = (Response) object;
						handleResponse(response.content, connection);
					}
				}

				@Override
				public void disconnected(Connection connection) {
					System.out.println("Connection lost!");

					// try to reconnect
					snakeClient.board.text = "Connection lost! Trying to reconnect...";
					snakeClient.board.repaint();
					ingame = false;
					reconnectionTimer = new Timer(3000, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								client.reconnect(5000);
								reconnectionTimer.stop();

								snakeClient.board.id = 9999;
								request("getHighscore");
								request("getColors");

							} catch (IOException e1) {
								System.out.println(e1.toString());
							}
						}
					});
					reconnectionTimer.setRepeats(true);
					reconnectionTimer.start();

				}
			});

			snakeClient.addKeyListener(this);

			request("getHighscore");
			request("getColors");

		} catch (Exception e) {
			System.out.println(e.toString());

			if (e.toString().contains("host cannot be null.")) {

				int result = JOptionPane.showOptionDialog(null, "No server found! Start server first, then click 'Retry'.", "Error",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null,
						new String[]{"Retry", "Cancel"}, "Retry");

				if (result == 0) {
					start();
				} else {
					System.exit(1);
				}

			}

		}

	}

	public void stop() {

		client.close();
		client.stop();

	}

	private void handleResponse(String content, Connection connection) {

		if (!content.startsWith("update")) System.out.println("Response: " + content);


		if (content.startsWith("setID")) {

			id = Integer.valueOf(content.substring(6));
			snakeClient.board.id = id;
			score = 0;
			snakeClient.board.text = "Score: " + score + " | Highscore: " + highscore;
			ingame = true;

		} else if (content.startsWith("update")) {

			String raw = content.substring(7);
			String tempY[] = raw.split(";");
			int field[][] = new int[Game.WIDTH][Game.HEIGHT];

			for (int y = 0; y < Game.HEIGHT; y++) {
				String tempX[] = tempY[y].split(":");
				for (int x = 0; x < Game.WIDTH; x++) {
					field[x][y] = Integer.valueOf(tempX[x]);
				}
			}

			// display start point
			if (!ingame) {
				field[0][0] = -9999;
			}

			snakeClient.board.field = field;
			snakeClient.board.repaint();

		} else if (content.startsWith("score")) {

			String temp[] = content.split(";");
			if (Integer.valueOf(temp[1]) == id) {
				score = Integer.valueOf(temp[2]);
				snakeClient.board.text = "Score: " + score + " | Highscore: " + highscore;
				snakeClient.board.repaint();
			}

		} else if (content.startsWith("highscore")) {

			highscore = Integer.valueOf(content.substring(10));
			if (ingame) {
				snakeClient.board.text = "Score: " + score + " | Highscore: " + highscore;
			} else {
				snakeClient.board.text = "Press SPACE to play. | Highscore: " + highscore;
			}
			snakeClient.board.repaint();

		} else if (content.startsWith("dead")) {

			String temp[] = content.split(";");
			if (Integer.valueOf(temp[1]) == id) {
				snakeClient.board.text = "GAME OVER! Press SPACE to replay. | Highscore: " + highscore;
				snakeClient.board.id = 9999;
				snakeClient.board.repaint();
				ingame = false;
			}

		} else if (content.startsWith("ban")) {

			snakeClient.board.text = content.substring(4);
			snakeClient.board.repaint();
			ingame = false;

		} else if (content.startsWith("colors")) {

			for (String s : content.substring(7).split(";")) {
				String temp[] = s.split(":");
				snakeClient.board.otherColor.put(Integer.valueOf(temp[0]), Color.decode(temp[1]));
			}

		}

	}

	private void request(String content) {
		Request request = new Request();
		request.content = content;
		client.sendTCP(request);
	}


	@Override
	public void keyPressed(KeyEvent e) {

		int key = e.getKeyCode();

		if (key == KeyEvent.VK_SPACE && !ingame) {

			// wanna play
			request("getID;#" + Integer.toHexString(snakeClient.board.myColor.getRGB()).substring(2));

		}

	}
	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}


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

		for (Hand hand : frame.hands()) {
			String handType = hand.isLeft() ? "Left hand" : "Right hand";
			double x = hand.palmNormal().getX();
			double y = hand.palmNormal().getY();
			String a = "";
			int direction = -1;


			//System.out.println(x + " " + y);
			if (java.lang.Math.abs(x) - 0.4 > java.lang.Math.abs(y)) {
				if (x < 0) {
					a = "Left";
					direction = 2;
				} else if (x > 0) {
					a = "Right";
					direction = 0;
				}
			} else if (java.lang.Math.abs(y) - 0.4 > java.lang.Math.abs(x)) {
				if (y < 0) {
					a = "Down";
					direction = 3;
				} else if (y > 0) {
					a = "Up";
					direction = 1;
				}
			}
			if (ingame){
			request("direction;" + String.valueOf(id) + ";" + String.valueOf(direction));
			System.out.println(a);
			try {Thread.sleep(50);}
			catch (InterruptedException e){}
			}
		}



	}
}
