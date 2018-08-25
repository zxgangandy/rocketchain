package com.rocketchain.utils.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetUtil {
    public static List<String> getLocalAddresses() {
        List<String> addressList = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
            while (nifs.hasMoreElements()) {
                NetworkInterface iface = nifs.nextElement();
                // 获得与该网络接口绑定的 IP 地址，一般只有一个
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    addressList.add(address.getHostAddress());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return addressList;
    }
}
