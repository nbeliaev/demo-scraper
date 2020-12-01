package dev.fr13.repository;

import dev.fr13.domain.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {

    Optional<Product> findByUrnAndName(String urn, String name);
}
