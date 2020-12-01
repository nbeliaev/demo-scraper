package dev.fr13.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "product", indexes = @Index(columnList = "sku", name = "sku_indx"))
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "sku", nullable = false)
    private String sku;

    @Column(name = "urn", nullable = false)
    private String urn;

    @OneToMany(mappedBy = "product")
    private List<ProductPrice> prices = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    public Product() {
    }

    public Product(MenuItem menuItem, String name, String sku, String urn) {
        this(menuItem, name, urn);
        this.sku = sku;
    }

    public Product(MenuItem menuItem, String name, String urn) {
        this.menuItem = menuItem;
        this.name = name;
        this.urn = urn;
    }

    public String getName() {
        return name;
    }

    public String getSku() {
        return sku;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
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

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public List<ProductPrice> getPrices() {
        return Collections.unmodifiableList(prices);
    }

    public void setPrices(List<ProductPrice> prices) {
        this.prices = prices;
    }

    public void addPrice(ProductPrice productPrice) {
        prices.add(productPrice);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (!Objects.equals(name, product.name)) return false;
        if (!Objects.equals(sku, product.sku)) return false;
        return Objects.equals(urn, product.urn);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (sku != null ? sku.hashCode() : 0);
        result = 31 * result + (urn != null ? urn.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", urn='" + urn + '\'' +
                '}';
    }
}
