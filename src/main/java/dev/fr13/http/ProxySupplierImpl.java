package dev.fr13.http;

import kong.unirest.Proxy;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.LinkedList;

public class ProxySupplierImpl implements ProxySupplier {
    private static final Logger logger = LoggerFactory.getLogger(ProxySupplierImpl.class);

    private static final String PROXY_LIST_URL = "http://hidemy.name/ru/api/proxylist.php";
    private static final String PROXY_CHECKER_URL = "http://api.proxyipchecker.com/pchk.php";

    private static final byte RESPONSE_PARAMS_NUMBER = 4;
    private static final byte ANON_TYPES = 34;

    private final String apiKey;
    private final boolean checkProxy;
    private final LinkedList<Proxy> proxyList = new LinkedList<>();

    public ProxySupplierImpl(String apiKey, boolean checkProxy) {
        this.apiKey = apiKey;
        this.checkProxy = checkProxy;
    }

    @Override
    public Proxy getProxy() {
        boolean proxyFound = true;
        Proxy proxy;
        do {
            proxy = doGetProxy();
            if (checkProxy) {
                proxyFound = isValidProxy(proxy);
            }
        } while (!proxyFound);
        return proxy;
    }

    @Override
    public void clearProxyPool() {
        logger.debug("Proxy pool is cleaning");
        proxyList.clear();
    }

    private Proxy doGetProxy() {
        if (proxyList.isEmpty()) {
            updateProxyList();
        }
        return proxyList.pop();
    }

    private void updateProxyList() {
        var proxies = getRawProxies();
        if (proxies.isEmpty()) {
            throw new IllegalStateException("Couldn't update proxy list");
        }
        for (int i = 0; i < proxies.length(); i++) {
            var proxy = proxies.getJSONObject(i);
            var host = proxy.getString("ip");
            var port = Integer.valueOf(proxy.getString("port"));
            proxyList.add(new Proxy(host, port));
        }
        logger.info("Found {} new proxies", proxyList.size());
    }

    @NotNull
    private JSONArray getRawProxies() {
        resetProxy();
        logger.debug("Api key is {}", apiKey);
        var body = Unirest.get(PROXY_LIST_URL)
                .queryString("out", "js")
                .queryString("type", "h")
                .queryString("anon", ANON_TYPES)
                .queryString("code", apiKey)
                .asString()
                .getBody();
        try {
            return new JSONArray(body);
        } catch (JSONException e) {
            logger.error("Response body is: {}", body, e);
            throw new JSONException("Couldn't convert to json array: {}");
        }
    }

    private boolean isValidProxy(@NotNull Proxy proxy) {
        logger.info("Proxy checking {}:{}", proxy.getHost(), proxy.getPort());
        var result = false;
        try {
            resetProxy();
            var resp = Unirest.post(PROXY_CHECKER_URL)
                    .field("ip", proxy.getHost())
                    .field("port", String.valueOf(proxy.getPort()))
                    .asString();
            if (resp.isSuccess()
                    && resp.getStatus() == HttpStatus.OK.value()) {
                result = isProxyAvailable(resp.getBody());
            } else {
                logger.info("Failed to check proxy. The proxy checker response status is {}", resp.getStatus());
                if (resp.getStatus() == HttpStatus.NOT_FOUND.value()) {
                    throw new IllegalStateException("The proxy checker is unavailable now.");
                }
            }
        } catch (Exception e) {
            logger.info("Proxy {}:{} is down", proxy.getHost(), proxy.getPort());
        }
        return result;
    }

    private boolean isProxyAvailable(String proxyCheckerResponse) {
        if (proxyCheckerResponse.isEmpty()) {
            return false;
        } else {
            var proxyParams = proxyCheckerResponse.split(";");
            if (proxyParams.length != RESPONSE_PARAMS_NUMBER) {
                return false;
            } else {
                var responseTime = Float.parseFloat(proxyParams[0]);
                var speed = Float.parseFloat(proxyParams[1]);
                logger.info("Response time is {} and speed is {}", responseTime, speed);
                return responseTime != 0f && speed != 0f;
            }
        }
    }

    private void resetProxy() {
        Unirest.config().reset().verifySsl(false).proxy(null);
    }
}
