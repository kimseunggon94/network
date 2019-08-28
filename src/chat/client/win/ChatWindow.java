package chat.client.win;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

import chat.ChatClient;
import chat.ChatClientThread;

public class ChatWindow {

	private Frame frame;
	private Panel pannel;
	private Button buttonSend;
	private TextField textField;
	private TextArea textArea;
	private Socket socket;
	
	
	public ChatWindow(String name, Socket socket) {
		frame = new Frame(name);
		pannel = new Panel();
		buttonSend = new Button("Send");
		textField = new TextField();
		textArea = new TextArea(30, 80);
		this.socket= socket;
		
	}

	public void show() {
		BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader( new InputStreamReader( socket.getInputStream(), StandardCharsets.UTF_8 ) );
			new ChatClientThread(bufferedReader).start();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// Button
		buttonSend.setBackground(Color.GRAY);
		buttonSend.setForeground(Color.WHITE);
		buttonSend.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent actionEvent ) {
				sendMessage();
			}
		});

		// Textfield
		textField.setColumns(80);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				char keyCode= event.getKeyChar();
				if(keyCode == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}
		});
		
		
		// Pannel
		pannel.setBackground(Color.LIGHT_GRAY);
		pannel.add(textField);
		pannel.add(buttonSend);
		frame.add(BorderLayout.SOUTH, pannel);

		// TextArea
		textArea.setEditable(false);
		frame.add(BorderLayout.CENTER, textArea);

		// Frame
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setVisible(true);
		frame.pack();
	}
	
	private void updateTextArea(String message) {
		textArea.append(message);
		textArea.append("\n");			//개행이 없어지므로
	}
	
	private void sendMessage() {
		String message = textField.getText();
		System.out.println("send : "+message);
		textField.setText("");
		textField.requestFocus();
		try {
			PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"),true);
			printwriter.println( "message:" + message );
			printwriter.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	private class ChatClientThread extends Thread {
		BufferedReader bufferedreader;
		public ChatClientThread(BufferedReader bufferedreader) {
			this.bufferedreader=bufferedreader;
		}
		
		public void run() {
			try {
				while(true) {
					String data = bufferedreader.readLine();
					if(data == null) {
						ChatClientApp.log("closed by client");
						break;
					}else {
						updateTextArea(data);
					}
					
				}
			}catch( SocketException e ) {
				ChatClientApp.log( "error:" + e );
			} catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}
