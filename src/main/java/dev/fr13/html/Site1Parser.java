package dev.fr13.html;

import dev.fr13.domain.MenuItem;
import dev.fr13.domain.Product;
import dev.fr13.domain.ProductPrice;
import dev.fr13.domain.WebSite;
import dev.fr13.html.pagination.QueryParams;
import dev.fr13.http.Downloader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnProperty(value = "parser.source-site1", havingValue = "enabled")
public class Site1Parser extends AbstractParser {
    private static final Logger logger = LoggerFactory.getLogger(Site1Parser.class);

    private static final String MENU_LINKS_SELECTOR = "a.a_lvl2";

    private static final String PAGINATION_LINKS_SELECTOR = "div.modern-page-navigation > a.number";

    private static final String PRODUCT_DIV_SELECTOR = "div.catalog-item_inner";
    private static final String PRODUCT_LINK_SELECTOR = "a.qb_corner";
    private static final String PRODUCT_NAME_SELECTOR = "div.catalog-item-name > a.text_fader";
    private static final String PRODUCT_SKU_SELECTOR = "div.catalog-article";
    private static final String PRODUCT_PRICE_SELECTOR = "div.price";

    public Site1Parser(Downloader downloader, @Qualifier("site1") QueryParams queryParams) {
        super(WebSite.SITE1, downloader, queryParams);
    }

    @Override
    void doParseMainMenu(String html) {
        var document = Jsoup.parse(html, StandardCharsets.UTF_8.name());
        var links = document.select(MENU_LINKS_SELECTOR);
        parseMenuItems(links);
    }

    @Override
    int doParsePaginationPagesNumber(String html) {
        var document = Jsoup.parse(html, StandardCharsets.UTF_8.name());
        var links = document.select(PAGINATION_LINKS_SELECTOR);
        var result = 1;
        if (!links.isEmpty()) {
            var lastLink = links.last();
            if (lastLink == null) {
                logger.error("Couldn't parse pagination");
            } else {
                result = Integer.parseInt(lastLink.text());
            }
        }
        return result;
    }

    @Override
    List<Product> doParsePageProducts(String html, MenuItem menuItem) {
        var document = Jsoup.parse(html);
        var items = document.select(PRODUCT_DIV_SELECTOR);
        var result = new ArrayList<Product>();
        items.forEach(i->getProduct(i, menuItem).ifPresent(result::add));
        return result;
    }

    private Optional<Product> getProduct(Element el, MenuItem menuItem) {
        var name = getItemText(el, PRODUCT_NAME_SELECTOR);
        var sku = getItemText(el, PRODUCT_SKU_SELECTOR);
        var stringPrice = getItemText(el, PRODUCT_PRICE_SELECTOR);
        var urn = el.select(PRODUCT_LINK_SELECTOR).attr(HREF);
        if (name.isEmpty() || sku.isEmpty()
                || stringPrice.isEmpty() || urn.isEmpty()) {
            return Optional.empty();
        }
        var numberSku = sku.replaceAll(NUMBERS_ONLY, "");
        var price = convertPrice(stringPrice);
        var product = new Product(menuItem, name, numberSku, urn);
        var productPrice = new ProductPrice(new Date(), product, price);
        product.addPrice(productPrice);
        logger.debug("Found product: {}", product);
        return Optional.of(product);
    }

    private String getItemText(Element el, String selector) {
        var elements = el.select(selector);
        if (elements.isEmpty()) {
            logger.warn("Couldn't parse {}", selector);
            return "";
        } else {
            return elements.text();
        }
    }

    private void parseMenuItems(Elements links) {
        if (links.isEmpty()) {
            logger.warn("Container of main menu is empty");
        }
        for (Element link : links) {
            var name = link.text();
            var urn = link.attr(HREF);
            logger.debug("Found menu item name: {}, urn: {}", name, urn);
            var item = new MenuItem(name, urn, WebSite.SITE1);
            menuItems.add(item);
        }
    }
}