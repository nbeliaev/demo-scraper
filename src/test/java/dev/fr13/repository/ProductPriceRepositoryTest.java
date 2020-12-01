package dev.fr13.repository;

import dev.fr13.domain.MenuItem;
import dev.fr13.domain.Product;
import dev.fr13.domain.ProductPrice;
import dev.fr13.domain.WebSite;
import dev.fr13.util.DateConverter;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@DisplayName("Тест репозитория цен")
public class ProductPriceRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ProductPriceRepository repository;
    private final DateConverter dateConverter = new DateConverter();

    @Test
    @DisplayName("должен удалить устаревшие цены")
    public void shouldDeleteOutdatedPrices() {
        var menuItem = new MenuItem("dummy", "dummy", WebSite.SITE1);
        entityManager.persist(menuItem);
        var product = new Product(menuItem, "dummy", "0001", "dummy");
        entityManager.persist(product);
        var productPriceOutdated = new ProductPrice(dateConverter.minusDaysFromNow(5), product, 100);
        entityManager.persist(productPriceOutdated);
        var productPrice = new ProductPrice(dateConverter.minusDaysFromNow(1), product, 100);
        entityManager.persist(productPrice);
        entityManager.flush();

        repository.deleteOutdatedPrices(dateConverter.minusDaysFromNow(2));

        var productPrices = repository.findAll();
        assertThat(productPrices).hasSize(1);
    }
}