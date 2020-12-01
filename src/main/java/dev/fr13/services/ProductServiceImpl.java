package dev.fr13.services;

import dev.fr13.domain.Product;
import dev.fr13.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository repository;

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveAll(List<Product> products) {
        for (Product product : products) {
            logger.debug("Looking for product with urn {} and name {}", product.getUrn(), product.getName());
            var optProduct = repository.findByUrnAndName(product.getUrn(), product.getName());
            if (optProduct.isPresent()) {
                logger.debug("{} was found with id {}", product.getName(), product.getId());
                product.setId(optProduct.get().getId());
            } else {
                logger.debug("{} is new", product.getName());
                repository.save(product);
            }
        }
    }

    @Override
    public Optional<Product> findByUrnAndName(String urn, String name) {
        return repository.findByUrnAndName(urn, name);
    }
}
