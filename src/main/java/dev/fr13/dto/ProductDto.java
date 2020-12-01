package dev.fr13.dto;

import java.util.List;

public class ProductDto {
    private String name;
    private String sku;
    private String urn;
    private List<ProductPriceDto> prices;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public List<ProductPriceDto> getPrices() {
        return prices;
    }

    public void setPrices(List<ProductPriceDto> prices) {
        this.prices = prices;
    }
}
