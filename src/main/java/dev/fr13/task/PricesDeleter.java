package dev.fr13.task;

import dev.fr13.services.ProductPriceService;
import dev.fr13.util.DateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "parser.delete-price-history", matchIfMissing = true)
public class PricesDeleter {
    private static final Logger logger = LoggerFactory.getLogger(PricesDeleter.class);

    private static final int EXPIRE_PRICE_DATE = 2;

    private final ProductPriceService productPriceService;
    private final DateConverter dateConverter;

    public PricesDeleter(ProductPriceService productPriceService, DateConverter dateConverter) {
        this.productPriceService = productPriceService;
        this.dateConverter = dateConverter;
    }

    @Scheduled(cron = "${parser.price-deleting-schedule}")
    public void deleteOutdatedPrices() {
        logger.info("Scheduled price deleting is starting");
        doDeletePrices();
        logger.info("Scheduled price deleting is shutting down");
    }

    private void doDeletePrices() {
        var date = dateConverter.minusDaysFromNow(EXPIRE_PRICE_DATE);
        productPriceService.deleteOutdatedPrices(date);
    }
}