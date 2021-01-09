package mini.chat;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Color;
import java.awt.SystemColor;
import javax.swing.JLabel;

//클라이언트가 채팅을 치면 서버가 먼저 읽고 클라이언트에게 보내주면 클라이언트가 읽고 채팅창에 출력

public class Client extends JFrame {

   private JPanel contentPane;
   private JTextField userName;
   private JTextField portText;
   private JTextField textField;
   private String userId;
   private String text;
   private static final Runnable Runnable = null;
   private Socket socket;
   private String host;
   private int port;
   private Scanner sc = new Scanner(System.in);
   private JTextField ipNum;
   private JTextField portNum;
   private JTextArea textArea;
   private JScrollPane scrollPane_1;

   public void connect(String IP, int port, String userName) {
            try {

               socket = new Socket(IP, port);
               PrintWriter writer = new PrintWriter(socket.getOutputStream());
               writer.println(userName);
               writer.println("[채팅시작]");
               writer.flush();
               read();
            } catch (IOException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
   }

   public void chatStart(String text) {

      Thread thread = new Thread(new Runnable() {

         @Override
         public void run() {

            PrintWriter writer = null;

            try {
               // 클라이언트가 서버에 보낸 대화내용을 출력.
               writer = new PrintWriter(socket.getOutputStream());
               writer.println(text);
               writer.flush();
            } catch (IOException e) {
               try {
                  writer.close();
                  socket.close();
               } catch (IOException e1) {
                  e1.printStackTrace();
               }
            }
         }
      });
      thread.start();
   }

   public void read() {

      Thread thread = new Thread(new Runnable() {

         @Override
         public void run() {

            BufferedReader br = null;
            while (true) {
               try {
                  // 클라이언트가 작성한 내용을 읽어서 버퍼에 담음.
                  br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                  String data = br.readLine();
                  if (data != null) {
                     textArea.append(data + "\n");
                     textArea.setCaretPosition(textArea.getDocument().getLength());
                  }
               } catch (IOException e) {
                  try {
                     br.close();
                     socket.close();
                     break;
                  } catch (IOException e1) {
                     e1.printStackTrace();
                  }
               }
            }
         }
      });
      thread.start();
   }

   /**
    * Launch the application.
    */
   public static void main(String[] args) {

      // 읽는 게 먼저 이뤄져야 채팅이 시작됨.
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            try {
               Client frame = new Client();
               frame.setVisible(true);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      });
   }

   public Client() {
      getContentPane().setLayout(null);

      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(100, 100, 405, 518);
      contentPane = new JPanel();
      contentPane.setBackground(SystemColor.inactiveCaption);
      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      setContentPane(contentPane);
      contentPane.setLayout(null);

      userName = new JTextField();
      userName.setHorizontalAlignment(SwingConstants.CENTER);
      userName.setBackground(SystemColor.activeCaption);
      userName.setText("닉네임");
      userName.setFont(new Font("배달의민족 주아", Font.PLAIN, 12));
        
      JToggleButton connection = new JToggleButton("접속하기");
      connection.setForeground(SystemColor.windowText);
      connection.setBackground(SystemColor.activeCaption);
      connection.setFont(new Font("배달의민족 주아", Font.PLAIN, 16));
      connection.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            if(connection.getText().contains("접속하기")) {
            connect(ipNum.getText(), Integer.parseInt(portNum.getText()), userName.getText());
            textArea.append("[채팅시작]\n");
            connection.setText("종료하기");
            }else if(connection.getText().contains("종료하기")){
               try {
                  socket.close();
                  textArea.append("[채팅종료]\n");
                  connection.setText("접속하기");
               } catch (IOException e1) {
                  e1.printStackTrace();
               }
            }
         }
      });
      connection.setBounds(246, 87, 133, 52);
      contentPane.add(connection);
      userName.setBounds(15, 118, 219, 21);
      contentPane.add(userName);
      userName.setColumns(10);

      scrollPane_1 = new JScrollPane();
      scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
      scrollPane_1.setBounds(15, 146, 364, 285);
      contentPane.add(scrollPane_1);

      textArea = new JTextArea();
      textArea.setBackground(SystemColor.inactiveCaptionBorder);
      scrollPane_1.setViewportView(textArea);

      textArea.setFont(new Font("배달의민족 주아", Font.PLAIN, 13));
      textArea.setTabSize(100);

      textField = new JTextField();
      textField.setBackground(SystemColor.inactiveCaptionBorder);
      textField.setFont(new Font("배달의민족 주아", Font.PLAIN, 11));
      textField.addKeyListener(new KeyAdapter() {
         @Override
         public void keyTyped(KeyEvent e) {
            text = textField.getText();
         }
      });
      textField.setBounds(15, 441, 286, 29);
      contentPane.add(textField);
      textField.setColumns(10);

      JToggleButton send = new JToggleButton("보내기");
      send.setForeground(SystemColor.desktop);
      send.setBackground(SystemColor.activeCaption);
      send.setFont(new Font("배달의민족 주아", Font.PLAIN, 12));
      send.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            String text = textField.getText();
            chatStart(text);
            textField.setText("");
         }
      });
      send.setBounds(308, 440, 71, 29);
      contentPane.add(send);

      ipNum = new JTextField();
      ipNum.setBackground(SystemColor.activeCaption);
      ipNum.setText("127.0.0.1");
      ipNum.setHorizontalAlignment(SwingConstants.LEFT);
      ipNum.setBounds(15, 87, 106, 21);
      contentPane.add(ipNum);
      ipNum.setColumns(10);

      portNum = new JTextField();
      portNum.setBackground(SystemColor.activeCaption);
      portNum.setText("8989");
      portNum.setBounds(128, 87, 106, 21);
      contentPane.add(portNum);
      portNum.setColumns(10);
      
      JLabel lblNewLabel = new JLabel("Chatting");
      lblNewLabel.setFont(new Font("tvN 즐거운이야기 Medium", Font.BOLD, 35));
      lblNewLabel.setBounds(136, 27, 121, 39);
      contentPane.add(lblNewLabel);
   }
}