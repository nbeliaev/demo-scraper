package dev.fr13.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "product_price")
public class ProductPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "price")
    private float price;

    public ProductPrice() {
    }

    public ProductPrice(Date created, Product product, float price) {
        this.created = created;
        this.product = product;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductPrice that = (ProductPrice) o;

        if (Float.compare(that.price, price) != 0) return false;
        if (!created.equals(that.created)) return false;
        return product.equals(that.product);
    }

    @Override
    public int hashCode() {
        int result = created.hashCode();
        result = 31 * result + product.hashCode();
        result = 31 * result + (price != +0.0f ? Float.floatToIntBits(price) : 0);
        return result;
    }
}
