package dev.fr13.html;

import dev.fr13.domain.MenuItem;
import dev.fr13.domain.Product;
import dev.fr13.domain.ProductPrice;
import dev.fr13.domain.WebSite;
import dev.fr13.html.pagination.Site3QueryParams;
import dev.fr13.html.pagination.QueryParams;
import dev.fr13.services.MenuItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("Парсинг сайта Site3")
class Site3ParserTest extends AbstractParserTest {
    private static final String MAIN_MENU = "./src/test/java/resources/site3/main-menu.html";
    private static final String PAGE = "./src/test/java/resources/site3/page.html";

    private static final Charset charset = Charset.forName("windows-1251");
    private static final QueryParams queryParams = new Site3QueryParams();

    @Mock
    private MenuItemService menuItemService;
    private Site3Parser parser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        doNothing().when(downloader).updateProxy();
        when(menuItemService.findAllActiveBySource(WebSite.SITE3)).thenReturn(List.of(dummyMenuItem));
        parser = new Site3Parser(downloader, queryParams);
    }

    @Test
    @DisplayName("должен найти все разделы главного меню")
    void shouldFindAllMenuSections() {
        var indexHtml = FileConvector.getFileContentAsString(MAIN_MENU, charset);
        when(downloader.getHtml(anyString())).thenReturn(indexHtml);
        var menuItems = parser.parseMainMenu();
        var itemsNumber = menuItems.size();
        var menuItemsCount = 506;
        assertThat(itemsNumber).isEqualTo(menuItemsCount);
    }

    @Test
    @DisplayName("должен обработать все страницы товаров по всему меню")
    void shouldProcessAllProductPages() {
        var menuHtml = FileConvector.getFileContentAsString(MAIN_MENU, charset);
        var html = FileConvector.getFileContentAsString(PAGE, charset);
        when(downloader.getHtml(anyString())).thenReturn(menuHtml);
        when(downloader.getHtml(anyString(), anyMap())).thenReturn(html);
        var menuItems = menuItemService.findAllActiveBySource(WebSite.SITE3);
        var initialMenuItemNumber = menuItems.size();
        for (MenuItem menuItem : menuItems) {
            parser.parseProductsOf(menuItem);
        }
        var invkTimesToGetPagination = 1;
        var pagePerMenuItem = 3;
        var invkTimesToGetProducts = initialMenuItemNumber * pagePerMenuItem + invkTimesToGetPagination;
        verify(downloader, times(0)).getHtml(anyString());
        verify(downloader, times(invkTimesToGetProducts)).getHtml(anyString(), anyMap());
    }

    @Test
    @DisplayName("должен найти все ссылки на страницы в пагинаторе")
    void shouldFindAllPaginationLinksOnPage() {
        var html = FileConvector.getFileContentAsString(PAGE, charset);
        var pages = parser.doParsePaginationPagesNumber(html);
        var pagesCount = 3;
        assertThat(pages).isEqualTo(pagesCount);
    }

    @Test
    @DisplayName("должен найти все товары на странице")
    void shouldFindAllProducts() {
        var html = FileConvector.getFileContentAsString(PAGE, charset);
        when(downloader.getHtml(anyString())).thenReturn(html);

        var products = parser.doParsePageProducts(html, dummyMenuItem);
        var productsCount = 15;

        var date = new Date();
        var product1 = new Product(
                dummyMenuItem,
                "товар1",
                "2004134654",
                "/catalog/1198/00005/");
        var productPrice1 = new ProductPrice(date, product1, 89);
        product1.addPrice(productPrice1);

        var product2 = new Product(
                dummyMenuItem,
                "товар2",
                "2100346541",
                "/catalog/1198/64684631/");
        var productPrice2 = new ProductPrice(date, product2, 485);
        product2.addPrice(productPrice2);

        var product3 = new Product(
                dummyMenuItem,
                "товар3",
                "3124230338",
                "/catalog/1198/89743321/");
        var productPrice3 = new ProductPrice(date, product3, 160);
        product3.addPrice(productPrice3);

        assertThat(products).isNotEmpty().hasSize(productsCount).contains(product1, product2, product3);
    }

    @Test
    @DisplayName("должен правильно обработать страницу без товаров")
    void shouldCorrectProcessPageWithNoProducts() {
        var html = FileConvector.getFileContentAsString(PAGE_NO_PRODUCTS);
        var products = parser.doParsePageProducts(html, dummyMenuItem);
        assertThat(products).isEmpty();
    }
}