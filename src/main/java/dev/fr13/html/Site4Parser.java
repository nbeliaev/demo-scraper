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
@ConditionalOnProperty(value = "parser.source-site4", havingValue = "enabled")
public class Site4Parser extends AbstractParser {
    private static final Logger logger = LoggerFactory.getLogger(Site4Parser.class);

    private static final String MENU_LINKS_SELECTOR = "li.navbar-catalog > ul.dropdown-menu > li.dropdown-submenu" +
            " > div.dropdown-menu > div.multiColumnMenu__col > a.multiColumnMenu__heading[href]";

    private static final String PAGINATION_LI_SELECTOR = "ul.pagination > li > a:not(.inline-link)";

    private static final String PRODUCT_DIV_SELECTOR = "div.productsWrapper > ul.products > li.products__item";
    private static final String PRODUCT_NAME_URN_SKU_SELECTOR = "div.products__text > div.products__name > a[href]";
    private static final String PRODUCT_PRICE_SELECTOR = "div.products__buy > div.products__prices > span.products__price";

    public Site4Parser(Downloader downloader, @Qualifier("site4") QueryParams queryParams) {
        super(WebSite.SITE4, downloader, queryParams);
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
            var urn = link.attr(HREF);
            var elName = link.selectFirst("span");
            var name = elName.text();
            var item = new MenuItem(name, urn, WebSite.SITE4);
            menuItems.add(item);
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
        } else {
            var product = new Product(menuItem, name, sku, urn);
            var productPrice = new ProductPrice(new Date(), product, price);
            product.addPrice(productPrice);
            logger.debug("Found product: {}", product);
            return Optional.of(product);
        }
    }

    private String getProductName(Element el) {
        var elName = el.selectFirst(PRODUCT_NAME_URN_SKU_SELECTOR);
        return elName == null ? "" : elName.text();
    }

    private String getProductUrn(Element el) {
        var elUrn = el.selectFirst(PRODUCT_NAME_URN_SKU_SELECTOR);
        return elUrn == null ? "" : elUrn.attr(HREF);
    }

    private float getProductPrice(Element el) {
        var elPrice = el.select(PRODUCT_PRICE_SELECTOR);
        return elPrice.isEmpty() ? 0 : convertPrice(elPrice.text());
    }

    private String getProductSku(Element el) {
        var elSku = el.select(PRODUCT_NAME_URN_SKU_SELECTOR);
        return elSku.isEmpty() ? "" : elSku.attr(HREF).replaceAll(NUMBERS_ONLY, "");
    }
}
