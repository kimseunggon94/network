package test.chat.client.win;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class ChatWindow {
	private Frame frame;
	private Panel pannel;
	private Button buttonSend;
	private TextField textField;
	private TextArea textArea;
	
	private Socket socket;
	
	private PrintWriter writer;
	private BufferedReader reader; 
	
	public ChatWindow(String nickname, Socket socket) {
		frame = new Frame(nickname);
		pannel = new Panel();
		buttonSend = new Button("Send");
		textField = new TextField();
		textArea = new TextArea(30, 80);
		
		this.socket = socket;
	}

	public void show() throws IOException{
		//
		// 1. UI 초기화
		//
		
		// Button
		buttonSend.setBackground(Color.GRAY);
		buttonSend.setForeground(Color.WHITE);
		buttonSend.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});

		// Textfield
		textField.setColumns(80);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				char keyCode = event.getKeyChar();
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
				finish();
			}
		});
		
		frame.setVisible(true);
		frame.pack();
		
		//
		// 2. IOStream 생성
		//
		writer = new PrintWriter( new OutputStreamWriter(socket.getOutputStream(), "UTF-8" ), true );
		reader = new BufferedReader( new InputStreamReader(socket.getInputStream(), "UTF-8" ));
	
		//
		// 3. Chat Client Thread 생성
		//
		new ChatClientThread().start();
	}
	
	private void finish() {
		try {
			// Socket 닫기
			if(socket!= null && socket.isClosed() == false) {
				socket.close();
			}
			
			// 애플리케이션 종료
			System.exit(0);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void updateTextArea(String message) {
		textArea.append(message);
		textArea.append("\n");
	}
	
	private void sendMessage() {
		String message = textField.getText();
		writer.println( "MESSAGE:" + message );

		textField.setText("");
		textField.requestFocus();
	}
	
	private class ChatClientThread extends Thread {
		@Override
		public void run() {
			try{
				while( true ) {
					String message = reader.readLine();
					if( message == null ) {
						break;
					}
					
					Thread.sleep(1);
					updateTextArea(message);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}catch(SocketException ex){
				ChatClientApp.consoleLog( "" + ex );	
			} catch(IOException ex){
				ChatClientApp.consoleLog( "" + ex );	
			} finally {
				finish();
			}
		}
	}
}