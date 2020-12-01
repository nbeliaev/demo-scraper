package dev.fr13.domain;

public enum WebSite {
    SITE1("http://site1.ru", "site1"),
    SITE2("http://site2.ru", "site2"),
    SITE3("https://site3.ru", "site3"),
    SITE4("https://site4.ru", "site4");

    private final String url;
    private final String name;

    WebSite(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public static WebSite valueByName(String s) {
        switch (s) {
            case "site1":
                return SITE1;
            case "site2":
                return SITE2;
            case "site3":
                return SITE3;
            case "site4":
                return SITE4;
            default:
                throw new IllegalArgumentException(String.format("No such site %s", s));
        }
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }
}
