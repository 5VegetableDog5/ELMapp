package com.example.elmapp.TcpClient;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Client extends Thread {

    private String ServerIP;

    private int Port;

    private static Socket client;

    private static Writer writer;

    private static BufferedReader br;

    /*
     *  isclosed : true 连接关闭, false 连接未关闭
     * */
    static boolean isclosed = true;

    private static byte RegisterFlag = 0;

    private static byte LoginFlag = 0;

    private static String ID;

    private static String Password;

    public Client(String serverIP, int port){
        this.ServerIP = serverIP;
        this.Port = port;
        try {
            client = new Socket(serverIP,port);
            client.setKeepAlive(true);

            //输入输出流初始化
            writer = new OutputStreamWriter(client.getOutputStream(),"GBK");
            br = new BufferedReader(
                    new InputStreamReader(client.getInputStream(), "GBK"));

            //发送客户端初始化信息
            writer.write("connectInit Type:esp8266\n" +
                    "connectInit ID:3\n");
            writer.flush();



        }catch (IOException ioException){
            System.out.println(ioException);
        }

    }


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String scnStr;

        String servername = "localhost";
        int port = 25601;
        boolean ReceiveFlag = false;//false表示未开启多线程

        System.out.println("Connect to:" + servername + " ,port:" + port);
        System.out.println("wating.........");

        Client client1 = new Client(servername,port);
        if(!client1.isNull() && client1.isConnected()) client1.start();


        //以下是连接成功后执行的代码
        //实现将键盘的输入发送至服务器
        try {
            if(!(client==null))
                while (scanner.hasNextLine()) {
                    scnStr = scanner.nextLine();
                    System.out.println("输入： " + scnStr);

                    if (scnStr.equals("close")) {

                        /* 表示关闭该连接
                          此 if 语句内实现关闭操纵 并向服务器发送关闭语句，并告诉服务器我已关闭
                          */

                        writer.write("$close$\n");
                        writer.flush();

                        System.out.println("正在关闭连接......");
                        client.shutdownInput();
                        client.shutdownOutput();
                        //确保都关闭了再执行后面的语句
                        while (!(client.isOutputShutdown() && client.isInputShutdown())) ;
                        writer.close();
                        client.close();
                        System.out.println("连接已断开!");

                        break;//退出循环
                    }else if(scnStr.equals("Registered")){
                        client1.registerUser("10086", "123456",new OneParametersCallable(){
                            @Override
                            public Object call(byte flag){
                                //System.out.println(flag);
                                if(flag == 1){
                                    System.out.println("成功");
                                }else if(flag == -1){
                                    System.out.println("失败");
                                }else {
                                    System.out.println("超时，失败！");
                                }
                                return null;
                            }
                        });
                    }
                    if(client.isClosed()) break;
                    //client1.sendStringToServer(scnStr);
                }

            scanner.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    /*
     * 开启一个线程用于处理与服务器之间的通讯
     * */
    @Override
    public void run() {
        //检查是否连接
        if(!client.isConnected())
        {
            System.out.println("未连接");
            return;
        }

        try {
            String message;
            //writer.write("Get\n");
            //writer.flush();
            while (!client.isClosed() && client.isConnected()) {

                if((message = br.readLine())!=null && !client.isClosed())
                {
                    if(message.equals("$close$"))
                    {
                        Close();
                        break;
                    }else if(message.contains("File name")) {
                        String s = message;
                        String[] strings = s.split(":");
                        ReceiveFile("", strings[1],strings[2]);
                    }else if(message.contains("Registered")){
                        if(message.contains("Success")){
                            RegisterFlag = 1;
                        }else {
                            RegisterFlag = -1;
                        }
                    }else if(message.contains("Login")){
                        if(message.contains("Success")){
                            LoginFlag = 1;
                        }else {
                            LoginFlag = -1;
                        }
                    }
                    System.out.println(message);
                }else{
                    break;
                }

            }
            if(!client.isClosed())
                Close();
        }catch (SocketException socketException){
            System.out.println("与服务器断开连接");
            Close();
            //throw new RuntimeException(socketException);
        } catch (Exception e)
        {
            System.out.println(e);
        }


    }

    public static void registerUser(String id, String password, OneParametersCallable Successcallable){

        System.out.println("注册用户");
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendStringToServer("Command:Registered."+id+"."+password);
                try {
                    //等待注册结果
                    int i = 0;
                    while (true){
                        if(RegisterFlag==0) {
                            i++;
                            System.out.println("等待注册结果 "+i);
                            Thread.sleep(10);
                            //i>500表示已经过了5s 则超时退出
                            if(i>=500){
                                System.out.println("超时 "+i);
                                break;
                            }
                        } else{//获得结果
                            //System.out.println("获得结果 "+RegisterFlag);
                            Successcallable.call(RegisterFlag);
                            RegisterFlag = 0;
                            break;
                        }
                    }

                }catch (InterruptedException interruptedException){
                    throw new RuntimeException(interruptedException);
                }catch (Exception e){
                    throw new RuntimeException(e);
                }

            }
        }).start();

    }

    public static void Login(String id, String password, OneParametersCallable Successcallable){

        System.out.println("登录");
        ID = id;
        Password = password;
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendStringToServer("connectInit ID:"+id);
                sendStringToServer("connectInit Password:"+password);
                try {
                    //等待注册结果
                    int i = 0;
                    while (true){
                        if(LoginFlag==0) {
                            i++;
                            System.out.println("等待注册结果 "+i);
                            Thread.sleep(10);
                            //i>500表示已经过了5s 则超时退出
                            if(i>=500){
                                System.out.println("超时 "+i);
                                break;
                            }
                        } else{//获得结果
                            //System.out.println("获得结果 "+RegisterFlag);
                            Successcallable.call(LoginFlag);
                            LoginFlag = 0;
                            break;
                        }
                    }

                }catch (InterruptedException interruptedException){
                    throw new RuntimeException(interruptedException);
                }catch (Exception e){
                    throw new RuntimeException(e);
                }

            }
        }).start();

    }




    private static void Close()
    {
        try {

            System.out.println("关闭");
            client.shutdownInput();
            client.shutdownOutput();
            while (!(client.isInputShutdown()&&client.isOutputShutdown()));
            writer.close();
            br.close();
            client.close();

        }catch (IOException e){
            System.out.println(e);
        }
    }

    public static void sendStringToServer(String s){
        try {
            writer.write(s+"\n");
            writer.flush();
        }catch (IOException e)
        {
            System.out.println("Client send String Error:"+e);
        }

    }

    //url: 保存文件路径
    public static void ReceiveFile(String url,String filename,String fileLength){

        url = "src/Resourse/client2"+filename;
        int filelength = Integer.parseInt(fileLength);

        try {
            System.out.println("Receive File "+url);
            File file = new File(url);
            DataInputStream dataInputStream = new DataInputStream(client.getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            if(file.exists()) {
                byte[] bytes = new byte[1024];
                int length = -1;
                long ReceiveLength = 0;//当前接收的文件长度
                while (true) {

                    if(filelength-ReceiveLength>bytes.length){
                        if((length = dataInputStream.read(bytes, 0, bytes.length)) == -1) break;
                    }else {
                        if((length = dataInputStream.read(bytes, 0,(int)(filelength-ReceiveLength))) == -1) break;
                    }

                    fileOutputStream.write(bytes, 0, length);
                    fileOutputStream.flush();

                    ReceiveLength+=length;
                    if(ReceiveLength>=filelength) break;
                }

                System.out.println("文件 " + filename + " 接收成功");

            }else {
                return;
            }
            fileOutputStream.close();

        }catch (IOException ioException){
            System.out.println(ioException);
        }
    }

    public static boolean isConnected(){
        return client.isConnected();
    }

    public static boolean isNull(){return client==null;}

    public String getID(){return ID;}

    public String getPassword(){return Password;}


}
