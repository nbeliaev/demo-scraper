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
import java.util.*;

@Service
@ConditionalOnProperty(value = "parser.source-site3", havingValue = "enabled")
public class Site3Parser extends AbstractParser {
    private static final Logger logger = LoggerFactory.getLogger(Site3Parser.class);

    private static final String ROOT_MENU_LI_SELECTOR = "div.catalogmenucolumn > ul.catalogmenu > li.parent > ul.first > li > a[href]";
    private static final String MENU_LI_SELECTOR = "div.sidebar > ul.menu-sidebar > li > a";
    private static final String MENU_SPAN_SELECTOR = "span";

    private static final String PAGINATION_LI_SELECTOR = "div.navigation > a:not(.right)";

    private static final String PRODUCT_DIV_SELECTOR = "div.offers > div.inner";
    private static final String PRODUCT_NAME_AND_URN_SELECTOR = "div.padd > div.name > a";
    private static final String PRODUCT_PRICE_SELECTOR = "div.prod-teaser__prices > div.soloprice > span";
    private static final String PRODUCT_SKU_SELECTOR = "div.popup > div.description > div";

    public Site3Parser(Downloader downloader, @Qualifier("site3") QueryParams queryParams) {
        super(WebSite.SITE3, downloader, queryParams);
    }

    @Override
    void doParseMainMenu(String html) {
        var rootDocument = Jsoup.parse(html, StandardCharsets.UTF_8.name());
        var rootLinks = rootDocument.select(ROOT_MENU_LI_SELECTOR);
        for (Element rootLink : rootLinks) {
            var itemUrn = rootLink.attr(HREF);
            var menuItemHtml = downloader.getHtml(getResourceUri(itemUrn));
            var document = Jsoup.parse(menuItemHtml, StandardCharsets.UTF_8.name());
            var links = document.select(MENU_LI_SELECTOR);
            parseMenuItems(links);
        }
    }

    @Override
    int doParsePaginationPagesNumber(String html) {
        var document = Jsoup.parse(html, StandardCharsets.UTF_8.name());
        var items = document.select(PAGINATION_LI_SELECTOR);
        return items.isEmpty() ? 1 : Integer.parseInt(items.last().text());
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
            var elName = link.select(MENU_SPAN_SELECTOR);
            if (!elName.isEmpty()) {
                var name = elName.text();
                var urn = link.attr(HREF);
                var item = new MenuItem(name, urn, WebSite.SITE3);
                logger.debug("Found menu item name: {}, urn: {}", name, urn);
                menuItems.add(item);
            }
        }
    }

    private Optional<Product> getProduct(Element el, MenuItem menuItem) {
        var name = getProductName(el);
        var urn = getProductUrn(el);
        var price = getProductPrice(el);
        var sku = getProductSku(el);
        if (name.isEmpty() || urn.isEmpty() ||
                sku.isEmpty() || price == 0) {
            return Optional.empty();
        }

        var product = new Product(menuItem, name, sku, urn);
        var productPrice = new ProductPrice(new Date(), product, price);
        product.addPrice(productPrice);
        logger.debug("Found product: {}", product);
        return Optional.of(product);
    }

    private String getProductName(Element el) {
        var elName = el.selectFirst(PRODUCT_NAME_AND_URN_SELECTOR);
        if (elName == null) {
            return "";
        } else {
            return elName.text();
        }
    }

    private String getProductUrn(Element el) {
        var elUrn = el.selectFirst(PRODUCT_NAME_AND_URN_SELECTOR);
        if (elUrn == null) {
            return "";
        } else {
            return elUrn.attr(HREF);
        }
    }

    private float getProductPrice(Element el) {
        var elPrice = el.select(PRODUCT_PRICE_SELECTOR);
        if (elPrice.isEmpty()) {
            return 0;
        } else {
            return convertPrice(elPrice.text());
        }
    }

    private String getProductSku(Element el) {
        var elSku = el.select(PRODUCT_SKU_SELECTOR);
        if (elSku.isEmpty()) {
            return "";
        } else {
            return elSku.last().text().replaceAll(NUMBERS_ONLY, "");
        }
    }
}
