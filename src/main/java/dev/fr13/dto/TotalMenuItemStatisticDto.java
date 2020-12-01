package dev.fr13.dto;

import dev.fr13.domain.WebSite;

public class TotalMenuItemStatisticDto {
    private String name;
    private long activeMenuItemsNumber;
    private long totalMenuItemsNumber;
    private long productsNumber;

    public TotalMenuItemStatisticDto(WebSite webSite, long activeMenuItemsNumber, long totalMenuItemsNumber, long productsNumber) {
        this.name = webSite.getName();
        this.activeMenuItemsNumber = activeMenuItemsNumber;
        this.totalMenuItemsNumber = totalMenuItemsNumber;
        this.productsNumber = productsNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getActiveMenuItemsNumber() {
        return activeMenuItemsNumber;
    }

    public void setActiveMenuItemsNumber(long activeMenuItemsNumber) {
        this.activeMenuItemsNumber = activeMenuItemsNumber;
    }

    public long getTotalMenuItemsNumber() {
        return totalMenuItemsNumber;
    }

    public void setTotalMenuItemsNumber(long totalMenuItemsNumber) {
        this.totalMenuItemsNumber = totalMenuItemsNumber;
    }

    public long getProductsNumber() {
        return productsNumber;
    }

    public void setProductsNumber(long productsNumber) {
        this.productsNumber = productsNumber;
    }
}
