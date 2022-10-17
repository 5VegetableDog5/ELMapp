package com.example.elmapp.TcpClient;

import java.util.ArrayList;

public interface OneStringCallable<V> {
    //参数@flag 0:超时 1:成功 -1：失败
    V call(ArrayList<String> BannerJSONS) throws Exception;
}
