package dev.fr13.http;

import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Supplier;

@Service
public class DownloaderImpl implements Downloader {
    private static final Logger logger = LoggerFactory.getLogger(DownloaderImpl.class);

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36";

    private final ProxySupplier proxySupplier;

    public DownloaderImpl(ProxySupplier proxySupplier) {
        this.proxySupplier = proxySupplier;
    }

    @Override
    public String getHtml(String urn) {
        logger.info("Download the resource {}", urn);
        checkUsedProxy();
        return doGetHtml(() -> Unirest.get(urn).asString().getBody());
    }

    @Override
    public String getHtml(String urn, Map<String, Object> params) {
        logger.info("Download the resource {} with query params {}", urn, params);
        checkUsedProxy();
        return doGetHtml(() -> Unirest.get(urn)
                .queryString(params)
                .asString()
                .getBody());
    }

    @Override
    public void updateProxy() {
        doConfig();
    }

    private String doGetHtml(Supplier<String> action) {
        String result = "";
        do {
            try {
                result = action.get();
            } catch (Exception e) {
                logger.info("Failed to get resource, possible the proxy is invalid");
            } finally {
                if (isInvalidHtml(result)) {
                    doConfig();
                }
            }
        } while (isInvalidHtml(result));
        return result;
    }

    private boolean isInvalidHtml(String s) {
        return s == null || s.isEmpty();
    }

    private void checkUsedProxy() {
        var proxy = Unirest.config().getProxy();
        if (proxy == null) {
            doConfig();
        }
    }

    private void doConfig() {
        Unirest.config().clearDefaultHeaders();
        Unirest.config().reset();
        var proxy = proxySupplier.getProxy();
        Unirest.config()
                .reset()
                .verifySsl(false)
                .proxy(proxy)
                .addDefaultHeader("user-agent", USER_AGENT);
        logger.info("Use proxy {}:{}", proxy.getHost(), proxy.getPort());
    }
}
