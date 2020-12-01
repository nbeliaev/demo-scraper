package dev.fr13.services;

import dev.fr13.domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    void saveAll(List<Product> products);

    Optional<Product> findByUrnAndName(String urn, String name);
}
