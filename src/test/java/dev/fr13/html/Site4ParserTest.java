package dev.fr13.html;

import dev.fr13.domain.MenuItem;
import dev.fr13.domain.Product;
import dev.fr13.domain.ProductPrice;
import dev.fr13.domain.WebSite;
import dev.fr13.html.pagination.Site4QueryParams;
import dev.fr13.html.pagination.QueryParams;
import dev.fr13.services.MenuItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("Парсинг сайта site4")
class Site4ParserTest extends AbstractParserTest {
    private static final String PAGE = "./src/test/java/resources/site4/page.html";
    private static final String PAGE_NO_PAGINATION = "./src/test/java/resources/site4/no-pagination.html";

    private static final QueryParams queryParams = new Site4QueryParams();

    @Mock
    private MenuItemService menuItemService;
    private Site4Parser parser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        doNothing().when(downloader).updateProxy();
        when(menuItemService.findAllActiveBySource(WebSite.SITE4)).thenReturn(List.of(dummyMenuItem));
        parser = new Site4Parser(downloader, queryParams);
    }

    @Test
    @DisplayName("должен найти все разделы главного меню")
    void shouldFindAllMenuSections() {
        var html = FileConvector.getFileContentAsString(PAGE);
        when(downloader.getHtml(anyString())).thenReturn(html);
        var menuItems = parser.parseMainMenu();
        var itemsNumber = menuItems.size();
        var menuItemsCount = 155;
        assertThat(itemsNumber).isEqualTo(menuItemsCount);
    }

    @Test
    @DisplayName("должен обработать все страницы товаров по всему меню")
    void shouldProcessAllProductPages() {
        var html = FileConvector.getFileContentAsString(PAGE);
        when(downloader.getHtml(anyString())).thenReturn(html);
        when(downloader.getHtml(anyString(), anyMap())).thenReturn(html);
        var menuItems = menuItemService.findAllActiveBySource(WebSite.SITE4);
        var initialMenuItemNumber = menuItems.size();
        for (MenuItem menuItem : menuItems) {
            parser.parseProductsOf(menuItem);
        }
        var invkTimesToGetPagination = 1;
        var pagePerMenuItem = 18;
        var invkTimesToGetProducts = initialMenuItemNumber * pagePerMenuItem + invkTimesToGetPagination;
        verify(downloader, times(0)).getHtml(anyString());
        verify(downloader, times(invkTimesToGetProducts)).getHtml(anyString(), anyMap());
    }

    @Test
    @DisplayName("должен найти все ссылки на страницы в пагинаторе")
    void shouldFindAllPaginationLinksOnPage() {
        var html = FileConvector.getFileContentAsString(PAGE);
        var pages = parser.doParsePaginationPagesNumber(html);
        var pagesCount = 18;
        assertThat(pages).isEqualTo(pagesCount);
    }

    @Test
    @DisplayName("должен правильно обработать отсутствие пагинатора")
    void shouldProcessEmptyPagination() {
        var html = FileConvector.getFileContentAsString(PAGE_NO_PAGINATION);
        var pages = parser.doParsePaginationPagesNumber(html);
        var pagesCount = 1;
        assertThat(pages).isEqualTo(pagesCount);
    }

    @Test
    @DisplayName("должен найти все товары на странице")
    void shouldFindAllProducts() {
        var html = FileConvector.getFileContentAsString(PAGE);
        when(downloader.getHtml(anyString())).thenReturn(html);

        var products = parser.doParsePageProducts(html, dummyMenuItem);
        var productsCount = 20;

        var date = new Date();
        var product1 = new Product(
                dummyMenuItem,
                "товар1",
                "10511227",
                "/product/10511227/");
        var productPrice1 = new ProductPrice(date, product1, 21);
        product1.addPrice(productPrice1);

        var product2 = new Product(
                dummyMenuItem,
                "товар2",
                "10431809",
                "/product/10431809/");
        var productPrice2 = new ProductPrice(date, product2, 10090);
        product2.addPrice(productPrice2);

        var product3 = new Product(
                dummyMenuItem,
                "товар3",
                "10259580",
                "/product/10259580/");
        var productPrice3 = new ProductPrice(date, product3, 4178);
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