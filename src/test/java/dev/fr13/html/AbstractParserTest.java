package dev.fr13.html;

import dev.fr13.domain.MenuItem;
import dev.fr13.domain.WebSite;
import dev.fr13.http.Downloader;
import org.mockito.Mock;

public abstract class AbstractParserTest {
    static final String PAGE_NO_PRODUCTS = "./src/test/java/resources/no-products.html";
    static final MenuItem dummyMenuItem = new MenuItem("dummy", "/dummy", WebSite.SITE2);

    @Mock
    Downloader downloader;
}
