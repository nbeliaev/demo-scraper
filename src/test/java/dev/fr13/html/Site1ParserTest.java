package dev.fr13.html;

import dev.fr13.domain.MenuItem;
import dev.fr13.domain.Product;
import dev.fr13.domain.ProductPrice;
import dev.fr13.domain.WebSite;
import dev.fr13.html.pagination.Site1QueryParams;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("Парсинг сайта Site1")
class Site1ParserTest extends AbstractParserTest {
    private static final String PAGE = "./src/test/java/resources/site1/page.html";
    private static final String PAGE_NO_PAGINATION = "./src/test/java/resources/site1/no-pagination.html";

    @Mock
    private MenuItemService menuItemService;
    private Site1Parser parser;
    private static final QueryParams queryParams = new Site1QueryParams();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        doNothing().when(downloader).updateProxy();
        when(menuItemService.findAllActiveBySource(any())).thenReturn(List.of(dummyMenuItem));
        parser = new Site1Parser(downloader, queryParams);
    }

    @Test
    @DisplayName("должен найти все разделы главного меню")
    void shouldFindAllMenuSections() {
        var html = FileConvector.getFileContentAsString(PAGE);
        when(downloader.getHtml(anyString())).thenReturn(html);
        var menuItems = parser.parseMainMenu();
        var menuItemsCount = 155;
        assertThat(menuItems.size()).isEqualTo(menuItemsCount);
    }

    @Test
    @DisplayName("должен обработать все страницы товаров по всему меню")
    void shouldProcessAllProductPages() {
        var html = FileConvector.getFileContentAsString(PAGE_NO_PAGINATION);
        when(downloader.getHtml(anyString())).thenReturn(html);
        when(downloader.getHtml(anyString(), anyMap())).thenReturn(html);
        var menuItems = menuItemService.findAllActiveBySource(WebSite.SITE1);
        for (MenuItem menuItem : menuItems) {
            parser.parseProductsOf(menuItem);
        }
        var invkTimesToGetPagination = 1;
        var paginationPages = 1;
        var invkTimesToGetProducts = 1;
        var totalInvkTimes  = invkTimesToGetPagination + paginationPages * invkTimesToGetProducts;
        verify(downloader, times(totalInvkTimes)).getHtml(anyString(), anyMap());
    }

    @Test
    @DisplayName("должен найти все ссылки на страницы в пагинаторе")
    void shouldFindAllPaginationLinksOnPage() {
        var html = FileConvector.getFileContentAsString(PAGE);
        var pages = parser.doParsePaginationPagesNumber(html);
        var pagesCount = 11;
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
        var products = parser.doParsePageProducts(html, dummyMenuItem);
        var productsCount = 80;

        var date = new Date();
        var product1 = new Product(
                dummyMenuItem,
                "товар1",
                "00049935404",
                "/catalog/6131/65466132/");
        var productPrice1 = new ProductPrice(date, product1, 22_248);
        product1.addPrice(productPrice1);

        var product2 = new Product(
                dummyMenuItem,
                "товар2",
                "00882048070",
                "/catalog/6131/646113/");
        var productPrice2 = new ProductPrice(date, product2, 1_313);
        product2.addPrice(productPrice2);

        var product3 = new Product(
                dummyMenuItem,
                "товар3",
                "000498534534",
                "/catalog/6131/87961321/");
        var productPrice3 = new ProductPrice(date, product3, 100);
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