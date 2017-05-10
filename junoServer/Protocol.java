package junoServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.swing.JOptionPane;

import org.json.JSONObject;

public class Protocol {
	private Receivable client;
	private PrintWriter output;
	private String username;
	private BufferedReader input;
	
	public Protocol(Receivable client) throws IOException {
		this.client = client;
		Socket socket = new Socket();
		String address = "ec2-52-41-213-54.us-west-2.compute.amazonaws.com";
		

		int port = 8989;
		try {
			socket.connect(new InetSocketAddress(address, port), 3000);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Socket Connection Failed");
		}

		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		output = new PrintWriter(socket.getOutputStream());
		login();
		new Thread(new Reader(socket, input, client)).start();

	}
	private void login() {
		boolean ack = false;
		JSONObject message;
		while (!ack) {
            username = JOptionPane.showInputDialog("Enter a Username");
            if (username == null) {
                System.exit(0);
            }
            JSONObject login = new JSONObject();
            JSONObject msg = new JSONObject();
            msg.put("username",username);
            login.put("type","login");
            login.put("message",msg);
			output.println(login);
			output.flush();
			try {
				message = new JSONObject(input.readLine());
				if (message.getString("type").equals("acknowledge")) {
                    ack = true;
                    System.out.println("connected");
                    System.out.println(message.toString());
                    client.setUsername(username);
                } else if(message.getString("type").equals("error")){
                    JOptionPane.showMessageDialog(null,message.getString("message"),"Error",JOptionPane.ERROR_MESSAGE);
				} else {
					client.giveMessage(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void sendMessage(JSONObject message) {
		output.println(message);
		output.flush();
		System.out.println("sent: " + message);
	}

	private class Reader implements Runnable {
		private BufferedReader input;
		private Receivable client;
		boolean running;

		private Reader(Socket s, BufferedReader in, Receivable cli) {
			input = in;
			running = true;
			client = cli;
		}

		@Override
		public void run() {
			JSONObject message;
			try {
				while (running) {
					message = new JSONObject(input.readLine());
                    System.out.println(message);
                    client.giveMessage(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		// Protocol protocol = new Protocol("Ethan", ));
	}

}
