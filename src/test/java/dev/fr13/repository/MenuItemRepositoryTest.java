package dev.fr13.repository;

import dev.fr13.domain.MenuItem;
import dev.fr13.domain.Product;
import dev.fr13.domain.ProductPrice;
import dev.fr13.domain.WebSite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@DisplayName("Тест репозитория разделов меню")
class MenuItemRepositoryTest {
    private static final MenuItem menuItem1 = new MenuItem("item1", "urn1", WebSite.SITE1);
    private static final Product product1 = new Product(menuItem1, "product1", "sku1", "urn1");
    private static final ProductPrice productPrice1 = new ProductPrice(new Date(), product1, 100);

    private static final MenuItem menuItem2 = new MenuItem("item2", "urn2", WebSite.SITE1);
    private static final Product product2_1 = new Product(menuItem2, "product2_1", "sku2_1", "urn2_1");
    private static final Product product2_2 = new Product(menuItem2, "product2", "sku2_2", "urn2_2");

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private MenuItemRepository repository;

    @BeforeEach
    public void populateDB() {
        entityManager.persist(menuItem1);
        entityManager.persist(product1);
        entityManager.persist(productPrice1);

        entityManager.persist(menuItem2);
        entityManager.persist(product2_1);
        entityManager.persist(product2_2);

        entityManager.flush();
    }

    @Test
    @DisplayName("статистика разделов меню")
    void shouldGetCorrectMenuItemsStatistic() {
        var totalStatistic = repository.getTotalStatistic();

        assertThat(totalStatistic).hasSize(1);
        var menuItemsNumber = 2;
        var productsNumber = 3;
        var item = totalStatistic.get(0);
        assertThat(item.getActiveMenuItemsNumber()).isZero();
        assertThat(item.getTotalMenuItemsNumber()).isEqualTo(menuItemsNumber);
        assertThat(item.getProductsNumber()).isEqualTo(productsNumber);
    }
}