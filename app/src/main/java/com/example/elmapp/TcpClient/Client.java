package com.example.elmapp.TcpClient;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;

public class Client extends Thread {

    private static String ServerIP;

    private static int Port;

    private static Socket client;

    private static Writer writer;

    private static BufferedReader br;

    /*
     *
     * */
    static boolean isclosed = true;

    private static byte RegisterFlag = 0;

    private static byte LoginFlag = 0;

    private static byte BannersJSONSFlag = 0;
    private static String ReceiveFileurl = null;

    private static ArrayList<String> bannerJSONS;

    private static String ID;

    private static String Password;

    private static Context mcontext;

    public Client(String serverIP, int port){
        this.ServerIP = serverIP;
        this.Port = port;
        try {
            client = new Socket(serverIP,port);
            client.setKeepAlive(true);

            //
            writer = new OutputStreamWriter(client.getOutputStream(),"GBK");
            br = new BufferedReader(
                    new InputStreamReader(client.getInputStream(), "GBK"));



        }catch (IOException ioException){
            System.out.println(ioException);
        }

    }


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String scnStr;

        String servername = "localhost";
        int port = 25601;
        boolean ReceiveFlag = false;//false

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
                        Log.e("File",message);
                        String s = message;
                        String[] strings = s.split(":");
                        //ReceiveFile("", strings[1],strings[2]);
                    }else if(message.contains("Registered")){
                        if(message.contains("Success")){
                            RegisterFlag = 1;
                        }else {
                            RegisterFlag = -1;
                        }
                    }else if(message.contains("BannerJSONS")){
                        String[] strings = message.split(":");
                        if(strings.length>0&&strings[1]!=null){
                            int len = Integer.parseInt(strings[1]);
                            ReceiveBannerJSONS(len);
                        }
                    } else if(message.contains("Login")){
                        if(message.contains("Success")){
                            LoginFlag = 1;
                        }else {
                            LoginFlag = -1;
                        }
                    }
                    if(message.length()<50) Log.d("TCP","receive "+message);
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
                Looper.prepare();
                sendStringToServer("Registered:"+id+"."+password);
                try {
                    //等待注册结果
                    int i = 0;
                    while (true){
                        if(RegisterFlag==0) {
                            i++;
                            //System.out.println("等待注册结果 "+i);
                            Thread.sleep(10);
                            //i>500表示已经过了5s 则超时退出
                            if(i>=500){
                                //System.out.println("超时 "+i);
                                Successcallable.call((byte) 0);
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
                Looper.loop();

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
                            //System.out.println("等待登录结果 "+i);
                            Thread.sleep(10);
                            //i>500表示已经过了5s 则超时退出
                            if(i>=500){
                                //System.out.println("超时 "+i);
                                Successcallable.call((byte)0);
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
            Log.d("TCP","Send success:"+s);
        }catch (IOException e)
        {
            System.out.println("Client send String Error:"+e);
        }

    }

    //url: 保存文件路径
    public static void ReceiveFile(String url,String filename,String fileLength){

        url = "res/FileBuffer/New/"+filename;
        int filelength = Integer.parseInt(fileLength);

        try {
            System.out.println("Receive File "+url);
            System.out.println("File length:"+fileLength);
            File file = new File("data/data/com.example.elmapp/files/New/"+filename);
            FileOutputStream fileOutputStream = new FileOutputStream(file);


            char[] chars = new char[512];
            int length = -1;
            long ReceiveLength = 0;//当前接收的文件长度

            while (true) {

                if(filelength-ReceiveLength>(chars.length)){
                    Log.e("EEEEEEEEEEEEE",String.valueOf((chars.length*2)));
                    if((length = br.read(chars, 0, (chars.length))) == -1) break;
                }else {

                    int a = (int) (filelength-ReceiveLength);
                    Log.e("DDDDDDDDDDDDDDD",String.valueOf(a));
                    //System.out.println("文件长度过短 此次接收"+a+"byte");
                    length = br.read(chars,0,a);
                }
                Log.e("EEEEEEEEEEEEE",String.valueOf(filelength));
                fileOutputStream.write(getBytes(chars), 0, length);
                fileOutputStream.flush();

                ReceiveLength+=length;
                System.out.println("ReceiveLength:"+ReceiveLength);
                if(ReceiveLength>=filelength) break;
            }
            Log.e("File",String.valueOf(ReceiveLength));
            System.out.println("file \"" + filename + "\" receive success");
            ReceiveFileurl = file.getPath();


            fileOutputStream.close();
            mcontext =null;

        }catch (IOException ioException){
            System.out.println(ioException);
        }
    }

    public static void GetallBanners(OneStringCallable cb){
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendStringToServer("Get:JSONS:Banners");
                int i = 0;
                try {
                    while (true){
                        if(BannersJSONSFlag==0) {
                            i++;
                            //System.out.println("等待登录结果 "+i);
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            //i>500表示已经过了5s 则超时退出
                            if(i>=500){
                                //System.out.println("超时 "+i);
                                cb.call(null);
                                break;
                            }
                        } else{//获得结果
                            //System.out.println("获得结果 "+RegisterFlag);
                            cb.call(bannerJSONS);
                            BannersJSONSFlag = 0;
                            break;
                        }
                    }
                }catch (Exception e){
                    Log.e("BannerJSONS","error ReceiveBannerJSONS:"+e);
                }
            }
        }).start();
    }

    public void ReceiveBannerJSONS(int len){
        int i = 0;
        String message;
        bannerJSONS = new ArrayList<>();
        try {
            while ((message = br.readLine())!=null && !client.isClosed()){
                bannerJSONS.add(message);
                if((i+1) >= len) break;
                i++;
            }
            Log.d("Banners","receive bannersJSONS success");
            BannersJSONSFlag = 1;//表示接收完成
        }catch (IOException ioException){
            System.out.println("ReceiveBannerJSONS error :"+ioException);
        }

    }

    /*public static File getFile(){

    }*/

    public static void GetFile(String url,OneStringCallable Cb){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Object lock = new Object();
                new TCPReceiveFile(ServerIP,Port,url,lock).start();
                synchronized (lock){
                    try {
                        lock.wait();
                    }catch (InterruptedException interruptedException){
                        System.out.println("ERROR GetFile:"+interruptedException);
                    }
                }
                System.out.println("Get file success");
                String[] strings = url.split("/");
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(strings[strings.length-1]);
                try {
                    Cb.call(arrayList);
                }catch (Exception e){
                    Log.e("File","Get File Cb "+e);
                }

            }
        }).start();

    }

    public static byte[] getBytes(char[] chars) {
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }

    public static boolean isConnected(){
        return (client!=null && client.isConnected());
    }

    public static boolean isNull(){return client==null;}

    public String getID(){return ID;}

    public String getPassword(){return Password;}



}
