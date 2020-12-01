package dev.fr13.repository;

import dev.fr13.domain.ProductPrice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface ProductPriceRepository extends CrudRepository<ProductPrice, Long> {

    @Modifying
    @Query("delete from ProductPrice where created < :timeStamp")
    void deleteOutdatedPrices(@Param("timeStamp") Date timeStamp);
}
