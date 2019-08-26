package test.chat.client.win;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ChatClientApp {
	private static final String SERVER_IP = "218.39.221.90";
	private static final int SERVER_PORT = 9999;

	public static void main(String[] args) {
		String nickname = null;
		Scanner scanner = null;
		Socket socket = null;

		try {
			// 1.create scanner to keyboard
			scanner = new Scanner(System.in);
			while (true) {
				System.out.println("대화명을 입력하세요.");
				System.out.print(">>> ");
				nickname = scanner.nextLine();

				if (nickname.isEmpty() == false) {
					break;
				}

				System.out.println("대화명은 한글자 이상 입력해야 합니다.\n");
			}
			
			//1. create socket
			socket = new Socket();
			
			//2. connect to server
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));

			//3. create iostream
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

			//4. join
			pw.println("JOIN:" + nickname);
			String ack = br.readLine();
			if ("JOIN:OK".equals(ack)) {
				new ChatWindow(nickname, socket).show();
			}
		} catch (ConnectException ex) {
			consoleLog("서버[" + SERVER_IP + ":" + SERVER_PORT + "]에 연결할 수 없습니다.");
		} catch (Exception ex) {
			consoleLog("다음 이유로 프로그램을 종료 합니다 :" + ex);
		} finally {
			if(scanner != null) {
				scanner.close();
			}
		}
	}

	public static void consoleLog( String message ) {
		System.out.println( "\n[chat client]" + message );
	}
	
}
