package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ChatClientThread extends Thread{
	private BufferedReader bufferedReader;
	private Socket socket;
	public ChatClientThread(Socket socket, BufferedReader bufferedReader) {
		this.bufferedReader=bufferedReader;
		this.socket=socket;
	}

	@Override
	public void run() {
		try {
			while(true) {
				String data = bufferedReader.readLine();
				if(data==null) {
					break;
				}
				
				System.out.println(data);
				
			}
		} catch(IOException e){
			e.printStackTrace();
		}finally {
			try {
				if(socket!=null&&socket.isClosed()==false) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
