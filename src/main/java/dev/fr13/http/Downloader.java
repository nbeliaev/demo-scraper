package dev.fr13.http;

import java.util.Map;

public interface Downloader {

    String getHtml(String uri);

    String getHtml(String uri, Map<String, Object> params);

    void updateProxy();
}
