package dev.fr13.domain;

import dev.fr13.util.WebSiteConverter;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "menu_item")
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "urn", nullable = false)
    private String urn;

    @Convert(converter = WebSiteConverter.class)
    @Column(name = "source", nullable = false)
    private WebSite source;

    @Column(name = "active")
    private boolean active;

    @OneToMany(mappedBy = "menuItem")
    private Set<Product> products = new HashSet<>();

    public MenuItem() {
    }

    public MenuItem(String name, String urn, WebSite source) {
        this.name = name;
        this.urn = urn;
        this.source = source;
    }

    public MenuItem(String name, String urn, WebSite source, boolean active) {
        this.name = name;
        this.urn = urn;
        this.source = source;
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public String getUrn() {
        return urn;
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

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public WebSite getSource() {
        return source;
    }

    public void setSource(WebSite source) {
        this.source = source;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void addProducts(List<Product> products) {
        this.products.addAll(products);
    }

    public Set<Product> getProducts() {
        return Collections.unmodifiableSet(products);
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MenuItem menuItem = (MenuItem) o;

        if (!name.equals(menuItem.name)) return false;
        if (!urn.equals(menuItem.urn)) return false;
        return source.equals(menuItem.source);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + urn.hashCode();
        result = 31 * result + source.hashCode();
        result = 31 * result + (active ? 1 : 0);
        return result;
    }
}
