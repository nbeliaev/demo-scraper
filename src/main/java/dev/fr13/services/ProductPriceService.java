package dev.fr13.services;

import dev.fr13.domain.ProductPrice;

import java.util.Date;
import java.util.List;

public interface ProductPriceService {

    void save(ProductPrice productPrice);

    void saveAll(List<ProductPrice> productPrices);

    void deleteOutdatedPrices(Date timeStamp);
}
