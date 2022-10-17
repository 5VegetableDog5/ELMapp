package com.example.elmapp.TcpClient;

import android.util.Log;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPReceiveFile extends Thread{
    private Object object;

    private Socket clientSocket;

    private Writer writer;

    private DataInputStream dataInputStream;

    private String fileurl;

    public TCPReceiveFile(String serverIP, int port, String fileurl, Object lock){
        try {
            this.fileurl = fileurl;
            this.object = lock;
            clientSocket = new Socket(serverIP,port);
            writer = new OutputStreamWriter(clientSocket.getOutputStream(),"GBK");
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            writer.write("Get:File:"+fileurl+"\n");
            writer.flush();//向服务器发起文件请求
            System.out.println("TCP Receive File Init ok");
        }catch (UnknownHostException unknownHostException){
            System.out.println("TCPReceiveFile:"+unknownHostException);
        }catch (IOException ioException){
            System.out.println("TCPReceiveFile:"+ioException);
        }


    }

    @Override
    public void run(){
        receive();//进入接收函数
    }

    public void receive(){
        try {

            //文件接收准备阶段 获取文件信息
            String filename = dataInputStream.readUTF();
            if(filename.equals("Null")){
                Log.e("File","File not existed "+fileurl);
            }
            long filelen = dataInputStream.readLong();
            System.out.println("File len:"+filelen);


            String url = "data/data/com.example.elmapp/files/New/"+filename;//保存的本地路径
            System.out.println("文件保存路径为url:"+url);

            File file = new File(url);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            int length = -1;
            long receivelen = 0;
            byte[] receiveBuff = new byte[1024];
            while (true){
                if(filelen-receivelen>receiveBuff.length){
                    if((length = dataInputStream.read(receiveBuff,0,receiveBuff.length))==-1){
                        break;
                    }
                }else {
                    int residualLen = (int)(filelen - receivelen);
                    if((length = dataInputStream.read(receiveBuff,0,residualLen))==-1) break;
                }
                fileOutputStream.write(receiveBuff,0,length);
                fileOutputStream.flush();
                receivelen+=length;
                if(receivelen>=filelen) break;
            }
            System.out.println("文件接收完成 "+filename);
            fileOutputStream.close();
            synchronized (object){
                object.notifyAll();
            }
            clientSocket.close();

        }catch (IOException ioException){
            System.out.println("ERROR TCPReceiveFile receive:"+ioException);
        }

    }
}
