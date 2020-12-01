package dev.fr13.http;


import kong.unirest.Proxy;

public interface ProxySupplier {

    Proxy getProxy();

    void clearProxyPool();
}
