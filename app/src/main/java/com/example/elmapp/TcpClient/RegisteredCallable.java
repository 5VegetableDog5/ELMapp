package com.example.elmapp.TcpClient;

public interface RegisteredCallable<V> {
    //����@flag 0:��ʱ 1:�ɹ� -1��ʧ��
    V call(byte flag) throws Exception;
}
