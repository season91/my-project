package mini.chat;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;

import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;

public class ChatClient extends JFrame {
   private Socket socket;
   private JTextArea jta = new JTextArea();;
   private JTextField jtf;
   private JTextField nname;
   private JTextField iip;
   private JTextField pport;
   private JButton start;
   private JTextField pf;
   private JScrollPane scrollPane;
   private JTextArea a;

   public static void main(String[] args) {

      ChatClient frame = new ChatClient();
      frame.setVisible(true);

   }

   public ChatClient() {

      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(100, 100, 557, 527);
      getContentPane().setLayout(null);

      scrollPane = new JScrollPane();
      scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
      scrollPane.setBounds(17, 58, 394, 355);
      getContentPane().add(scrollPane);
      scrollPane.setViewportView(jta);

      jtf = new JTextField();
      jtf.setBounds(17, 428, 394, 28);
      getContentPane().add(jtf);
      jtf.setColumns(10);

      nname = new JTextField();
      nname.setHorizontalAlignment(SwingConstants.CENTER);
      nname.setBounds(17, 14, 86, 28);
      getContentPane().add(nname);
      nname.setColumns(10);

      iip = new JTextField();
      iip.setHorizontalAlignment(SwingConstants.CENTER);
      iip.setColumns(10);
      iip.setBounds(109, 14, 134, 28);
      getContentPane().add(iip);
      iip.setText("192.168.0.106");

      pport = new JTextField();
      pport.setHorizontalAlignment(SwingConstants.CENTER);
      pport.setColumns(10);
      pport.setBounds(248, 14, 86, 28);
      getContentPane().add(pport);
      pport.setText("7979");
      start = new JButton("참여");

      start.setBounds(337, 13, 69, 31);
      getContentPane().add(start);

      pf = new JTextField();
      pf.setText("접속자");
      pf.setHorizontalAlignment(SwingConstants.CENTER);
      pf.setBounds(428, 55, 90, 28);
      getContentPane().add(pf);
      pf.setColumns(10);

      a = new JTextArea();
      a.setBounds(428, 98, 90, 315);
      getContentPane().add(a);

      start.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            if (start.getText().equals("참여")) {
               connect();
               start.setText("퇴장");
               jta.append("서버알림이 : 귓속말은 /w 닉네임:메시지 입니다.\n");
            } else {
               start.setText("퇴장");
               jta.append("[채팅 퇴장]");
               jta.append("\n");
               stop();
               start.setText("참여");

            }

         }
      });

      jtf.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            String msg = jtf.getText();
            chatStart(msg);
            jtf.setText("");

         }
      });

   }

   // 버튼을 눌렀을때 종료하는 메서드
   public void stop() {
      try {
         socket.close();
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   // 서버에 연결하는 메서드
   public void connect() {
      PrintWriter writer = null;
      try {
         // 연결할 서버의 IP 입력
         socket = new Socket(iip.getText(), Integer.parseInt(pport.getText()));
         // 자신이 정한 닉네임 입력
         String name = nname.getText();

         writer = new PrintWriter(socket.getOutputStream());
         writer.println(name);
         writer.flush();

         // 서버에 보내고 읽기 메서드를 부름
         read();

      } catch (UnknownHostException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();

      }

   }

   // 읽어주는 메서드
   public void read() {
      Thread thread = new Thread(new Runnable() {
         @Override
         public void run() {

            BufferedReader br = null;
            // 서버로 들어오는 요청을 계속 읽어줌
            while (true) {
               try {
                  br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                  String data = br.readLine();
                  if (data != null) {

                     System.out.println("\n" + data);
                     jta.append(data + "\n");

                     // 스크롤 따라 내려가기
                     jta.setCaretPosition(jta.getDocument().getLength());
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

   // 메세지를 서버로 보내는 메서드
   public void chatStart(String msg) {

      Thread thread = new Thread(new Runnable() {
         @Override
         public void run() {
            PrintWriter writer = null;

            try {
               writer = new PrintWriter(socket.getOutputStream());
               writer.println(msg);
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

      });
      thread.start();
   }
}