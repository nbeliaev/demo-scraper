package dev.fr13.dto;

import java.util.List;

public class ProductsMenuItemDto {
    private String name;
    private String urn;
    private List<ProductDto> products;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public List<ProductDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDto> products) {
        this.products = products;
    }
}
