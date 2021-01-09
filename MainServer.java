package mini.chat;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainServer frame = new MainServer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// threadPool 다양접속자 효과적관리
	// 스레드가 한정적이기때문에 클라이언트가많아지면 저하됨.
	// 서버를 안정적으로 관리하기위해 쓰레드풀기법을 운영한다.
	public static ExecutorService threadPool;

	// Vector를 이용해 클라이언트 관리할 것
	//// 쉽게사용하는 배열같다. 왜 벡터를 써야하는가?
	public static Vector<Server> clients = new Vector<Server>();

	ServerSocket serverSocket;

	// GUI부분
	// 서버를 구동시켜 클라이언트의 연결을 기다리는 메서드
	public void startServer(String IP, int port) {
		try {
			serverSocket = new ServerSocket();
			// bind 함수는 기다려주는 자신의 ip,port 접속을 기다린다.
			serverSocket.bind(new InetSocketAddress(IP, port));
		} catch (Exception e) {
			e.printStackTrace();
			if (!serverSocket.isClosed()) {
				stopServer();
			}
			return;
		}

		// 클라이언트가 접속할때 까지 기다리는 쓰레드입니다.
		Runnable thread = new Runnable() {

			@Override
			public void run() {
				while (true) {
					// 접속했다면 클라이언트배열에 추가.
					// 로그 출력
					try {
						Socket socket = serverSocket.accept();
						clients.add(new Server(socket));
						System.out.println(
								"[클라이언트 접속] " + socket.getReuseAddress() + ": " + Thread.currentThread().getName());
					} catch (Exception e) {

						if (serverSocket.isClosed()) {
							stopServer();
						}
						break;
					}
				}

			}
		};
		// 쓰레디풀 초기화 해주고 첫번째쓰레드에 클라이언드 기다리는 쓰레드를 추가한다.
		threadPool = Executors.newCachedThreadPool();
		threadPool.submit(thread);
	}

	// 서버의 작동을 중지시키는 메서드
	// 서버 작동종료 이후에 자원 반납하는 메서드
	public void stopServer() {
		try {
			// 현재작중인 모든소켓 닫기
			Iterator<Server> iterator = clients.iterator();
			while (iterator.hasNext()) {
				Server client = iterator.next();
				client.socket.close();
				iterator.remove();
			}

			// 서버소켓객체 닫기
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}

			// 쓰레드풀 종료하기
			if (threadPool != null && !threadPool.isShutdown()) {
				threadPool.isShutdown();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * Create the frame.
	 */
	public MainServer() {
		setTitle("[ 채팅하기 ]");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(33, 19, 383, 197);
		contentPane.add(textArea);
		

		String IP = "127.0.0.1";
		int port = 9876;
		
		JToggleButton toggleButton = new JToggleButton("시작하기");
		toggleButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(toggleButton.getText().equals("시작하기")) {
					startServer(IP, port);
					textArea.append("[서버 시작]\n");
					toggleButton.setText("종료하기");
				} else {
					stopServer();
					textArea.append("[서버 종료]\n");
					toggleButton.setText("시작하기");
					
				}
			
			}
		});
		toggleButton.setBounds(137, 228, 161, 29);
		contentPane.add(toggleButton);
		
		
	}
}