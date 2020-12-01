package dev.fr13.task;

import dev.fr13.domain.MenuItem;
import dev.fr13.domain.Product;
import dev.fr13.html.Parser;
import dev.fr13.services.MenuItemService;
import dev.fr13.services.ProductPriceService;
import dev.fr13.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

public class PricesUpdater {
    private static final Logger logger = LoggerFactory.getLogger(PricesUpdater.class);

    private final List<Parser> parsers;
    private final MenuItemService menuItemService;
    private final ProductService productService;
    private final ProductPriceService productPriceService;
    private final Runnable jobAfterUpdating;
    private final boolean doInitialUpdating;

    public PricesUpdater(List<Parser> parsers,
                         MenuItemService menuItemService,
                         ProductService productService,
                         ProductPriceService productPriceService,
                         boolean doInitialUpdating,
                         Runnable jobAfterUpdating) {
        this.parsers = parsers;
        this.menuItemService = menuItemService;
        this.productService = productService;
        this.productPriceService = productPriceService;
        this.doInitialUpdating = doInitialUpdating;
        this.jobAfterUpdating = jobAfterUpdating;
    }

    @Scheduled(cron = "${parser.price-updating-schedule}")
    public void update() {
        logger.info("Scheduled products updating is starting");
        doUpdate();
        logger.info("Scheduled products updating is shutting down");
    }

    @PostConstruct
    @SuppressWarnings("unused")
    private void init() {
        if (doInitialUpdating) {
            logger.info("Initial products updating is starting");
            doUpdate();
            logger.info("Initial products updating is shutting down");
        }
    }

    private void doUpdate() {
        imitateRandomOrderOfParsing(parsers);
        for (Parser parser : parsers) {
            logger.info("Start parsing {}", parser);
            processSiteData(parser);
            logger.info("Finish parsing {}", parser);
        }
        jobAfterUpdating.run();
    }

    private void processSiteData(Parser parser) {
        try {
            var menuItems = menuItemService.findAllActiveBySource(parser.getWebSite());
            imitateRandomOrderOfParsing(menuItems);
            logger.info("Processing {} menu items of {}", menuItems.size(), parser);
            for (MenuItem menuItem : menuItems) {
                var products = parser.parseProductsOf(menuItem);
                saveProducts(products);
                logger.info("{} items remain", menuItems.size() - (menuItems.indexOf(menuItem) + 1));
            }
        } catch (Exception e) {
            logger.error("Couldn't process {}", parser);
            logger.error(e.getMessage(), e);
        }
    }

    private void imitateRandomOrderOfParsing(List<?> list) {
        Collections.shuffle(list);
    }

    private void saveProducts(List<Product> products) {
        productService.saveAll(products);
        for (Product product : products) {
            productPriceService.saveAll(product.getPrices());
        }
    }
}
