package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class ChatClient {
	
	private static int PORT = 7000;
	private static Scanner scanner = null;
	private static Socket socket = null;
	public static void main(String [] args) {
		try {
			//2. socket 생성
			socket= new Socket();
			scanner = new Scanner(System.in);

			//3. 연결
			InetSocketAddress inetSocketAddress = new InetSocketAddress(ChatServer.SERVER_IP, PORT);
			socket.connect(inetSocketAddress);


			//4. reader/writer 생성
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"),true);		


			//5. join 프로토콜
			System.out.print("닉네임>>" );
			String nickname = scanner.nextLine();
			printWriter.println( "join:" + nickname );
			printWriter.flush();


			//6. ChatClientReceiveThread 시작
			new ChatClientThread(socket, bufferedReader).start();

			//7. 키보드 입력 처리
			while( true ) {

				String input = scanner.nextLine();

				if( "quit".equals( input )) {
					printWriter.println("quit");
					printWriter.flush();
					break;
				}
				if("".equals(input)) {
					input =" ";
				}
				printWriter.println("message:"+input);
				printWriter.flush();
				
			}

		} catch( SocketException e ) {
			e.printStackTrace();
		} catch( IOException e ) {
			e.printStackTrace();
		} finally {
			if(scanner!=null) {
				scanner.close();
			}
		}
	}

	static void log(String log) {
		System.out.println("[Client#"+Thread.currentThread().getId()+"]"+log);	
	}
}
