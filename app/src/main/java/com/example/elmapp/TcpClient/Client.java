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
     *  isclosed : true ���ӹر�, false ����δ�ر�
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

            //�����������ʼ��
            writer = new OutputStreamWriter(client.getOutputStream(),"GBK");
            br = new BufferedReader(
                    new InputStreamReader(client.getInputStream(), "GBK"));

            //���Ϳͻ��˳�ʼ����Ϣ
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
        boolean ReceiveFlag = false;//false��ʾδ�������߳�

        System.out.println("Connect to:" + servername + " ,port:" + port);
        System.out.println("wating.........");

        Client client1 = new Client(servername,port);
        if(!client1.isNull() && client1.isConnected()) client1.start();


        //���������ӳɹ���ִ�еĴ���
        //ʵ�ֽ����̵����뷢����������
        try {
            if(!(client==null))
                while (scanner.hasNextLine()) {
                    scnStr = scanner.nextLine();
                    System.out.println("���룺 " + scnStr);

                    if (scnStr.equals("close")) {

                        /* ��ʾ�رո�����
                          �� if �����ʵ�ֹرղ��� ������������͹ر���䣬�����߷��������ѹر�
                          */

                        writer.write("$close$\n");
                        writer.flush();

                        System.out.println("���ڹر�����......");
                        client.shutdownInput();
                        client.shutdownOutput();
                        //ȷ�����ر�����ִ�к�������
                        while (!(client.isOutputShutdown() && client.isInputShutdown())) ;
                        writer.close();
                        client.close();
                        System.out.println("�����ѶϿ�!");

                        break;//�˳�ѭ��
                    }else if(scnStr.equals("Registered")){
                        client1.registerUser("10086", "123456",new OneParametersCallable(){
                            @Override
                            public Object call(byte flag){
                                //System.out.println(flag);
                                if(flag == 1){
                                    System.out.println("�ɹ�");
                                }else if(flag == -1){
                                    System.out.println("ʧ��");
                                }else {
                                    System.out.println("��ʱ��ʧ�ܣ�");
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
     * ����һ���߳����ڴ����������֮���ͨѶ
     * */
    @Override
    public void run() {
        //����Ƿ�����
        if(!client.isConnected())
        {
            System.out.println("δ����");
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
            System.out.println("��������Ͽ�����");
            Close();
            //throw new RuntimeException(socketException);
        } catch (Exception e)
        {
            System.out.println(e);
        }


    }

    public static void registerUser(String id, String password, OneParametersCallable Successcallable){

        System.out.println("ע���û�");
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendStringToServer("Command:Registered."+id+"."+password);
                try {
                    //�ȴ�ע����
                    int i = 0;
                    while (true){
                        if(RegisterFlag==0) {
                            i++;
                            System.out.println("�ȴ�ע���� "+i);
                            Thread.sleep(10);
                            //i>500��ʾ�Ѿ�����5s ��ʱ�˳�
                            if(i>=500){
                                System.out.println("��ʱ "+i);
                                break;
                            }
                        } else{//��ý��
                            //System.out.println("��ý�� "+RegisterFlag);
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

        System.out.println("��¼");
        ID = id;
        Password = password;
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendStringToServer("connectInit ID:"+id);
                sendStringToServer("connectInit Password:"+password);
                try {
                    //�ȴ�ע����
                    int i = 0;
                    while (true){
                        if(LoginFlag==0) {
                            i++;
                            System.out.println("�ȴ�ע���� "+i);
                            Thread.sleep(10);
                            //i>500��ʾ�Ѿ�����5s ��ʱ�˳�
                            if(i>=500){
                                System.out.println("��ʱ "+i);
                                break;
                            }
                        } else{//��ý��
                            //System.out.println("��ý�� "+RegisterFlag);
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

            System.out.println("�ر�");
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

    //url: �����ļ�·��
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
                long ReceiveLength = 0;//��ǰ���յ��ļ�����
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

                System.out.println("�ļ� " + filename + " ���ճɹ�");

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
