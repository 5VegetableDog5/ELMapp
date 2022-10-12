package com.example.elmapp.TcpClient;

public interface RegisteredCallable<V> {
    //参数@flag 0:超时 1:成功 -1：失败
    V call(byte flag) throws Exception;
}
