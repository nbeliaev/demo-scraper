package dev.fr13.html;

import dev.fr13.domain.MenuItem;
import dev.fr13.domain.Product;
import dev.fr13.domain.WebSite;

import java.util.List;

public interface Parser {

    List<MenuItem> parseMainMenu();

    List<Product> parseProductsOf(MenuItem menuItem);

    WebSite getWebSite();
}
