package mini.chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class Server {

	Socket socket;
	
	public Server(Socket socket) {
		this.socket = socket;
	}
	
	// 클라이언트로부터??? 메시지를 전달받는 메서드 
	public void receive() {
		//하나의 스레드이용할때 런어블 자주사용
		Runnable thread = new Runnable() {
			
			@Override
			public void run() {
				//어떠한 모듈로 작동할건디 런안에서 정의
				//각종 예외가 발생할 수 있으니 try-catch
				try {
					//메시지 전달받기 
					while(true) {
						//나중에 버퍼로바꿔보기.
						InputStream in = socket.getInputStream();
						byte[] buffer = new byte[512];
						int length = in.read(buffer);
						while(length == -1) {
							throw new IOException();
						}
						System.out.println("[메시지 수신 성공]"
								+ socket.getRemoteSocketAddress()
								+ ": " + Thread.currentThread().getName());
					String message = new String(buffer, 0, length, "UTF-8");
					
					//다른 클라이언트들에게 메시지  전달 
					for (Server client : MainServer.clients) {
						client.send(message);
					}
								}
 				} catch (Exception e) {
					try {
						//메시지를 보낸 클라이언트의 주소와 해당쓰레드 고유이름 출력 
						System.out.println("[메시지 수신 오류]"
						+socket.getRemoteSocketAddress()
						+": " + Thread.currentThread().getName());
					} catch (Exception e2) {
						
						e2.printStackTrace();
					}
				}
				
			}
		};
		MainServer.threadPool.submit(thread);
	}
	
	//클라이언트에게?? 메시지를  전송 메서드 
	public void send(String message) {
		Runnable thread = new Runnable() {
			
			@Override
			public void run() {
				try {
					OutputStream out = socket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
				} catch (Exception e) {
					try {
						System.out.println("[메시지 송신 오류]"
								+ socket.getRemoteSocketAddress()
								+": " + Thread.currentThread().getName());
						//클라이언트가 접속 끊겼으니 클라이언트배열에서 우리쪽에서도 지워준다.
						MainServer.clients.remove(Server.this);
						socket.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
				
			}
		};
		MainServer.threadPool.submit(thread);
	}
	
}