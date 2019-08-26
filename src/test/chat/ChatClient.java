package test.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
	private static final String SERVER_IP = "192.168.1.16";
	private static final int SERVER_PORT = 9999;

	public static void main(String[] args) {
		Socket socket = null;
		Scanner scanner = null;
		
		try {
			// 1.create scanner to keyboard
			scanner = new Scanner( System.in );
			
			// 2.create socket
			socket = new Socket();

			// 3.connect to server
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
			
			// 4.create stream
			PrintWriter pw = new PrintWriter( new OutputStreamWriter(socket.getOutputStream(), "UTF-8" ), true );
			BufferedReader br = new BufferedReader( new InputStreamReader(socket.getInputStream(), "UTF-8" ) );
			
			// 5.join
			System.out.print( "닉네임>>" );
			String nickname = scanner.nextLine();
			pw.println( "JOIN:" + nickname );
			String ack = br.readLine();
			if( "JOIN:OK".equals( ack ) ) {
				System.out.println( "입장하였습니다. 즐거운 채팅 되세요" );
			}
			
			//6.create and start thread
			new ChatClientThread( socket ).start();
			
			//7.input message
			while( true ) {
				if( scanner.hasNextLine() == false ) {
					continue;
				}
				
				String message = scanner.nextLine();
				
				if( "quit".equals( message ) ) {
					pw.println( "QUIT" );
					//System.exit(0);
					break;
				}
				
				if( "".equals( message ) == false ){
					pw.println( "MESSAGE:" + message );
				}
			}
		} catch (ConnectException ex) {
			consoleLog( "서버[" + SERVER_IP + ":" + SERVER_PORT + "]에 연결할 수 없습니다." );	
		} catch (Exception ex) {
			consoleLog( "다음 이유로 프로그램을 종료 합니다 :" + ex );	
		} finally {
			try {
				if( scanner != null ) {
					scanner.close();
				}
				if( socket != null && socket.isClosed() == false ){
					socket.close();
				}
			}catch( IOException ex ) {
				consoleLog( "다음 이유로 프로그램을 종료 합니다 :" + ex );	
			}
		}
	}
	
	public static void consoleLog( String message ) {
		System.out.println( "\n[chat client]" + message );
	}
}