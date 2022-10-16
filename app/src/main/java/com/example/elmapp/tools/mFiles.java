package com.example.elmapp.tools;

import android.util.Log;

import java.io.File;

public class mFiles {
    public static boolean careteDir(String url){
        File file = new File(url);
        if(!file.exists()) {
            file.mkdir();
            Log.d("File","create new File success");
            return true;
        }
        Log.e("File","create new File error:Maybe new file is exists");
        return false;
    }

    public static void showLocalFiles(){
        File file = new File("data/data/com.example.elmapp/files");
        showFilesByUrl(file.getPath(),0);
    }

    //deep 文件深度
    public static void showFilesByUrl(String url,int deep){
        File file = new File(url);
        if(file.isDirectory()){
            for(int i = 0;i<deep;i++) System.out.print("-");
            System.out.println(file.getName());
            File[] files = file.listFiles();
            for(int i = 0;i < files.length; i++){
                showFilesByUrl(files[i].getPath(),deep+1);
            }
        }else {
            for(int i = 0;i<deep;i++) System.out.print("-");
            System.out.println(file.getName());
        }
    }

    public static void rename(String fileurl,String newName){
        File oldfile = new File(fileurl);
        if(!oldfile.exists()){
            Log.e("File","rename error:old file not exists");
            return;
        }

        String newfileurl = oldfile.getParentFile().getPath()+"/"+newName;
        File newfile = new File(newfileurl);
        //Log.d("File",newfileurl);

        if(!newfile.exists()){
            if(oldfile.renameTo(newfile)){
                Log.d("File","rename success");
            }
        }else Log.e("File", "rename error:newfile existsed "+newfileurl);
    }

    /*
    * @deleteself:true删除自身 false不删除自身
    * */
    public static void deleteAllfiles(String url,boolean deleteself){
        File file = new File(url);
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(int i = 0; i < files.length;i++){
                deleteAllfiles(files[i].getPath(),true);
            }
            if(deleteself) file.delete();
        }else {
            file.delete();
        }
    }
}
