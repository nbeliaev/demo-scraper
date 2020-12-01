package dev.fr13.html;

import dev.fr13.domain.MenuItem;
import dev.fr13.domain.Product;
import dev.fr13.domain.ProductPrice;
import dev.fr13.domain.WebSite;
import dev.fr13.html.pagination.QueryParams;
import dev.fr13.http.Downloader;
import dev.fr13.services.ProductService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@ConditionalOnProperty(value = "parser.source-site2", havingValue = "enabled")
public class Site2Parser extends AbstractParser {
    private static final Logger logger = LoggerFactory.getLogger(Site2Parser.class);

    private static final String MENU_LI_SELECTOR = "div.catlog_menu_block > ul > li > a + ul > li > a";

    private static final String PAGINATION_LI_SELECTOR = "div.pagination > ul > li";

    private static final String PRODUCT_DIV_SELECTOR = "div.cart > div";
    private static final String PRODUCT_CART_SELECTOR = "a.title_cart";
    private static final String PRODUCT_PRICES_SELECTOR = "span.price > span";
    private static final String PRODUCT_ORDINARY_PRICE_SELECTOR = "span.price_one";
    private static final String PRODUCT_MARKETING_PRICE_SELECTOR = "span.price_new";
    private static final String PRODUCT_SKU_SELECTOR = "div.product_title > div.post > p";

    private final ProductService productService;

    public Site2Parser(Downloader downloader, ProductService productService, @Qualifier("site2") QueryParams queryParams) {
        super(WebSite.SITE2, downloader, queryParams);
        this.productService = productService;
    }

    @Override
    void doParseMainMenu(String html) {
        var document = Jsoup.parse(html, StandardCharsets.UTF_8.name());
        var links = document.select(MENU_LI_SELECTOR);
        parseMenuItems(links);
    }

    @Override
    int doParsePaginationPagesNumber(String html) {
        var document = Jsoup.parse(html, StandardCharsets.UTF_8.name());
        var items = document.select(PAGINATION_LI_SELECTOR);
        return items.isEmpty() ? 1 : Integer.parseInt(items.get(items.size() - 1).text());
    }

    @Override
    List<Product> doParsePageProducts(String html, MenuItem menuItem) {
        var document = Jsoup.parse(html);
        var items = document.select(PRODUCT_DIV_SELECTOR);
        var result = new ArrayList<Product>();
        items.forEach(i -> getProduct(i, menuItem).ifPresent(result::add));
        return result;
    }

    private void parseMenuItems(Elements links) {
        if (links.isEmpty()) {
            logger.warn("Container of main menu is empty");
        }
        for (Element link : links) {
            var name = link.text();
            var urn = link.attr(HREF);
            var item = new MenuItem(name, urn, WebSite.SITE2);
            logger.debug("Found menu item name: {}, urn: {}", name, urn);
            menuItems.add(item);
        }
    }

    private Optional<Product> getProduct(Element el, MenuItem menuItem) {
        var elLink = el.selectFirst(PRODUCT_CART_SELECTOR);
        if (elLink == null) {
            return Optional.empty();
        } else {
            var name = getProductName(elLink);
            var urn = getProductUrn(elLink);
            var price = getProductPrice(el);
            if (name.isEmpty() || urn.isEmpty() ||
                    price == 0) {
                return Optional.empty();
            }

            var product = new Product(menuItem, name, urn);
            setProductSku(urn, name, product);
            var productPrice = new ProductPrice(new Date(), product, price);
            product.addPrice(productPrice);
            logger.debug("Found product: {}", product);
            return Optional.of(product);
        }
    }

    private void setProductSku(String urn, String name, Product product) {
        productService.findByUrnAndName(urn, name).ifPresentOrElse(
                i -> product.setSku(i.getSku()),
                () -> {
                    var sku = getProductSku(urn, name);
                    product.setSku(sku);
                });
    }

    private String getProductSku(String urn, String name) {
        logger.info("Get sku for {} by {}", name, urn);
        var uri = getResourceUri(urn);
        var html = downloader.getHtml(uri);
        var document = Jsoup.parse(html);
        var elSku = document.select(PRODUCT_SKU_SELECTOR);
        if (elSku.isEmpty()) {
            logger.warn("Couldn't get sku for {} by {}", name, uri);
            return "";
        } else {
            return elSku.text().replaceAll(NUMBERS_ONLY, "");
        }
    }

    private String getProductName(Element el) {
        return el.text();
    }

    private String getProductUrn(Element el) {
        return el.attr(HREF);
    }

    private float getProductPrice(Element el) {
        var elPrices = el.select(PRODUCT_PRICES_SELECTOR);
        var elPrice = elPrices.size() == 1 ?
                elPrices.select(PRODUCT_ORDINARY_PRICE_SELECTOR) : elPrices.select(PRODUCT_MARKETING_PRICE_SELECTOR);
        return convertPrice(elPrice.text());
    }
}
