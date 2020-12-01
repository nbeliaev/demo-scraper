package dev.fr13.html;

import dev.fr13.domain.MenuItem;
import dev.fr13.domain.Product;
import dev.fr13.domain.WebSite;
import dev.fr13.html.pagination.QueryParams;
import dev.fr13.http.Downloader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class AbstractParser implements Parser {
    private static final Logger logger = LoggerFactory.getLogger(AbstractParser.class);

    private static final String FLOAT_NUMBER = "\\D*$";
    private static final String CUT_SPACES = "\\s";

    static final String NUMBERS_ONLY = "[^0-9]";
    static final String HREF = "href";

    final Downloader downloader;
    final List<MenuItem> menuItems = new ArrayList<>();

    private final WebSite webSite;
    private final String baseUrl;
    private final QueryParams queryParams;

    public AbstractParser(WebSite webSite, Downloader downloader, QueryParams queryParams) {
        this.webSite = webSite;
        this.baseUrl = webSite.getUrl();
        this.downloader = downloader;
        this.queryParams = queryParams;
    }

    @Override
    public List<MenuItem> parseMainMenu() {
        do {
            downloader.updateProxy();
            var html = downloader.getHtml(baseUrl);
            logger.info("Parsing main menu of {}", baseUrl);
            doParseMainMenu(html);
            logger.info("Total found {} menu items", menuItems.size());
        } while (menuItems.isEmpty());
        return Collections.unmodifiableList(menuItems);
    }

    @Override
    public List<Product> parseProductsOf(MenuItem menuItem) {
        var pagesNumber = getPagesNumber(menuItem);
        var products = parseMenuItemPages(menuItem, pagesNumber);
        logger.info("Total found {} unique products in menu item {}",
                products.size(), menuItem.getUrn());
        return new ArrayList<>(products);
    }

    @Override
    public WebSite getWebSite() {
        return webSite;
    }

    Set<Product> parseMenuItemPages(MenuItem menuItem, int pagesNumber) {
        var result = new HashSet<Product>();
        for (int i = 1; i <= pagesNumber; i++) {
            queryParams.setPaginationParam(i);
            var products = new HashSet<Product>();
            var maxAttempts = 10;
            do {
                var html = downloader.getHtml(getResourceUri(menuItem.getUrn()), queryParams.getParams());
                logger.info("Parsing products of {}", menuItem.getUrn());
                var pageProducts = doParsePageProducts(html, menuItem);
                logger.info("Found {} products", pageProducts.size());
                products.addAll(pageProducts);
                if (products.isEmpty()) {
                    downloader.updateProxy();
                }
                maxAttempts--;
            } while (products.isEmpty() && maxAttempts > 0);
            result.addAll(products);
        }
        return result;
    }

    float convertPrice(String s) {
        if (s.isEmpty()) {
            return 0;
        } else {
            var numbers = s.replaceAll(FLOAT_NUMBER, "");
            var trimmed = numbers.replaceAll(CUT_SPACES, "");
            var stringPrice = trimmed.replace(",", ".");
            return Float.parseFloat(stringPrice);
        }
    }

    @NotNull
    String getResourceUri(String url) {
        return baseUrl + url;
    }

    abstract void doParseMainMenu(String html);

    abstract int doParsePaginationPagesNumber(String html);

    abstract List<Product> doParsePageProducts(String html, MenuItem menuItem);

    @Override
    public String toString() {
        return "Parser with base url=" + baseUrl;
    }

    private int getPagesNumber(MenuItem menuItem) {
        var urn = menuItem.getUrn();
        var uri = getResourceUri(urn);
        queryParams.setPaginationParam(1);
        var menuItemHtml = downloader.getHtml(uri, queryParams.getParams());
        logger.info("Parsing pagination of {}", urn);
        var pagesNumber = doParsePaginationPagesNumber(menuItemHtml);
        logger.info("Found {} pages", pagesNumber);
        return pagesNumber;
    }
}
