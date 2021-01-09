package mini.chat;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainClient extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	

	Socket socket;
	private JTextField userName;
	private JTextField IPText;
	private JTextField portText;
	private JTextArea textArea;
	private JTextField input;
	private JButton sendBtn;
	private JButton connBtn;
	//여기선 쓰레드풀 없어도됨. 기본 쓰레드만 사용 
	//클라이언트 프로그램 동작하는 메서드
	public void startClient(String IP, int port) {
		//server에 있는 정보 가져오는 것 임.
		Thread thread = new Thread() {
			public void run() {
				try {
					socket = new Socket(IP, port);
					receive();
				} catch (Exception e) {
					if(!socket.isClosed()) {
						stopClient();
						System.out.println("[서버 접속 실패] ");
						//플랫폼 exit 사용 못함.
					}
				}
			}
		}; thread.start();
		
		
	}
	
	//클라이언트 종료 메서드 종료후 자원반납 
	public void stopClient() {
		try {
			if(socket != null && !socket.isClosed()) {
				socket.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}
	
	//서버로부터 메시지를 전달받는 메서드
	public void receive() {
		while(true) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String message = new String(br.readLine());
				System.out.println(message);
				textArea.append(message);
				
 			} catch (Exception e) {
				stopClient();
				break;
			}
		}
		
		
	}
	//여기서 쓰레드는 전달용 전송용 2개쓰레드임 
	//서버로 메시지를 전송하는 메서드
	public void send(String message) {
		Thread thread = new Thread() {
			public void run() {
				PrintWriter writer = null;
				try {
					writer = new PrintWriter(socket.getOutputStream());
					writer.println(message);
					writer.flush();
				} catch (IOException e) {
					try {
						writer.close();
						socket.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
				
			}
		}; thread.start();
		
	}
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainClient frame = new MainClient();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainClient() {
		setTitle("[ 채팅 클라이언트 ]");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		userName = new JTextField();
		userName.setText("닉네임을 입력하세요.");
		userName.setBounds(28, 19, 130, 26);
		contentPane.add(userName);
		userName.setColumns(10);
		
		IPText = new JTextField("127.0.0.1");
		IPText.setBounds(170, 19, 83, 26);
		contentPane.add(IPText);
		IPText.setColumns(10);
		
		portText = new JTextField("9876");
		portText.setBounds(265, 19, 58, 26);
		contentPane.add(portText);
		portText.setColumns(10);
		
		textArea = new JTextArea();
		textArea.setBounds(28, 46, 402, 176);
		contentPane.add(textArea);
		
		input = new JTextField();
		input.setBounds(28, 234, 235, 26);
		contentPane.add(input);
		input.setColumns(10);
		
		sendBtn = new JButton("보내기");
		sendBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String message = userName.getText() + ": " + input.getText() +"\n";
				send(message);
				input.setText("");
				input.requestFocus();
			}
		});
		sendBtn.setBounds(297, 234, 117, 29);
		contentPane.add(sendBtn);
		
		connBtn = new JButton("접속하기");
		connBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(connBtn.getText().equals("접속하기")) {
				int port = 9876;
				try {
					port = Integer.parseInt(portText.getText());
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				startClient(IPText.getText(), port);
				textArea.append("[ 채팅 접속] \n");
				connBtn.setText("종료하기");
				input.requestFocus();
				}else {
					stopClient();
					textArea.append("[채팅방 퇴장]\n");
					connBtn.setText("접속하기");
					
				}
			}
		});
		connBtn.setBounds(327, 19, 117, 29);
		contentPane.add(connBtn);
		
		
	}
}