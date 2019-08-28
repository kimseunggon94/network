package chat.client.win;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ChatClientApp {
	private static Socket socket = null;
	private static int PORT = 7000;
	public static void main(String[] args) {
		String name = null;
		Scanner scanner = new Scanner(System.in);

		while( true ) {

			System.out.println("대화명을 입력하세요.");
			System.out.print(">>> ");
			name = scanner.nextLine();

			if (name.isEmpty() == false ) {
				break;
			}

			System.out.println("대화명은 한글자 이상 입력해야 합니다.\n");
		}

		scanner.close();
		try {
			//1.create socket
			socket=new Socket();


			//2. connect to server
			InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), PORT);
			socket.connect(inetSocketAddress);


			//3. create iostream
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"),true);		
			

			//4. join 프로토콜 구현
			printWriter.println( "join:" + name );
			printWriter.flush();
			
			new ChatWindow(name, socket).show();
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	static void log(String log) {
		System.out.println("[Client#"+Thread.currentThread().getId()+"]"+log);	
	}

}
