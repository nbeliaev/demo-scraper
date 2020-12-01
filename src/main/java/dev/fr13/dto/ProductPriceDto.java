package dev.fr13.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ProductPriceDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
    private Date created;
    private float price;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}