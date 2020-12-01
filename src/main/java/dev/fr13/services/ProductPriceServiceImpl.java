package dev.fr13.services;

import dev.fr13.domain.ProductPrice;
import dev.fr13.repository.ProductPriceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ProductPriceServiceImpl implements ProductPriceService {
    private static final Logger logger = LoggerFactory.getLogger(ProductPriceServiceImpl.class);

    private final ProductPriceRepository repository;

    public ProductPriceServiceImpl(ProductPriceRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(ProductPrice productPrice) {
        logger.debug("Save {} product price", productPrice);
        repository.save(productPrice);
    }

    @Override
    public void saveAll(List<ProductPrice> productPrices) {
        logger.debug("Save {} product prices", productPrices.size());
        repository.saveAll(productPrices);
    }

    @Override
    @Transactional
    public void deleteOutdatedPrices(Date timeStamp) {
        logger.debug("Delete prices with date less than {}", timeStamp);
        repository.deleteOutdatedPrices(timeStamp);
    }
}
