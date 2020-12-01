package dev.fr13.html;

import dev.fr13.domain.MenuItem;
import dev.fr13.domain.Product;
import dev.fr13.domain.ProductPrice;
import dev.fr13.domain.WebSite;
import dev.fr13.html.pagination.Site2QueryParams;
import dev.fr13.html.pagination.QueryParams;
import dev.fr13.services.MenuItemService;
import dev.fr13.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@DisplayName("Парсинг сайта site2")
class Site2ParserTest extends AbstractParserTest {
    private static final String PAGE = "./src/test/java/resources/site2/page.html";
    private static final String PAGE_NO_PAGINATION = "./src/test/java/resources/site2/no-pagination.html";
    private static final String PAGE_ONE_PRODUCT = "./src/test/java/resources/site2/one-product.html";

    @Mock
    private ProductService productService;
    @Mock
    private MenuItemService menuItemService;
    private Site2Parser parser;
    private static final QueryParams queryParams = new Site2QueryParams();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        doNothing().when(downloader).updateProxy();
        when(menuItemService.findAllActiveBySource(WebSite.SITE2)).thenReturn(List.of(dummyMenuItem));
        parser = new Site2Parser(downloader, productService, queryParams);
    }

    @Test
    @DisplayName("должен найти все разделы главного меню")
    void shouldFindAllMenuSections() {
        var html = FileConvector.getFileContentAsString(PAGE);
        when(downloader.getHtml(anyString())).thenReturn(html);
        var menuItems = parser.parseMainMenu();
        var itemsNumber = menuItems.size();
        var menuItemsCount = 157;
        assertThat(itemsNumber).isEqualTo(menuItemsCount);
    }

    @Test
    @DisplayName("должен обработать все страницы товаров по всему меню")
    void shouldProcessAllProductPages() {
        var html = FileConvector.getFileContentAsString(PAGE);
        when(downloader.getHtml(anyString())).thenReturn(html);
        when(downloader.getHtml(anyString(), anyMap())).thenReturn(html);
        var menuItems = menuItemService.findAllActiveBySource(WebSite.SITE2);
        for (MenuItem menuItem : menuItems) {
            parser.parseProductsOf(menuItem);
        }
        var invkTimesToGetPagination = 1;
        var paginationPages = 16;
        var invkTimesToGetProducts = 1;
        var productsPerPage = 40;
        var totalInvkToGetSku = paginationPages * productsPerPage;
        var totalInvkTimes  = invkTimesToGetPagination + paginationPages * invkTimesToGetProducts;
        verify(downloader, times(totalInvkToGetSku)).getHtml(anyString());
        verify(downloader, times(totalInvkTimes)).getHtml(anyString(), anyMap());
    }

    @Test
    @DisplayName("должен найти все ссылки на страницы в пагинаторе")
    void shouldFindAllPaginationLinksOnPage() {
        var html = FileConvector.getFileContentAsString(PAGE);
        var pages = parser.doParsePaginationPagesNumber(html);
        var pagesCount = 16;
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
        var htmlOneProduct = FileConvector.getFileContentAsString(PAGE_ONE_PRODUCT);
        when(downloader.getHtml(anyString())).thenReturn(html);
        when(downloader.getHtml(contains("product"))).thenReturn(htmlOneProduct);
        when(productService.findByUrnAndName(anyString(), anyString())).thenReturn(Optional.empty());

        var products = parser.doParsePageProducts(html, dummyMenuItem);
        var productsCount = 40;

        var date = new Date();
        var product1 = new Product(
                dummyMenuItem,
                "товар1",
                "316056",
                "/product/8646131/");
        var productPrice1 = new ProductPrice(date, product1, 22_248);
        product1.addPrice(productPrice1);

        var product2 = new Product(
                dummyMenuItem,
                "товар2",
                "316056",
                "/product/843321321/");
        var productPrice2 = new ProductPrice(date, product2, 1_313);
        product2.addPrice(productPrice2);

        var product3 = new Product(
                dummyMenuItem,
                "товар3",
                "316056",
                "/product/94646131/");
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