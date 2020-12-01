package dev.fr13.config;

import dev.fr13.html.Parser;
import dev.fr13.http.ProxySupplier;
import dev.fr13.http.ProxySupplierImpl;
import dev.fr13.services.MenuItemService;
import dev.fr13.services.ProductPriceService;
import dev.fr13.services.ProductService;
import dev.fr13.task.PricesUpdater;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public ProxySupplier proxySupplier(@Value("${proxy.apikey}") String apiKey,
                                       @Value("${proxy.check-proxy}") boolean checkProxy) {
        return new ProxySupplierImpl(apiKey, checkProxy);
    }

    @Bean
    public PricesUpdater productsUpdater(List<Parser> parsers,
                                         MenuItemService menuItemService,
                                         ProductService productService,
                                         ProductPriceService productPriceService,
                                         @Value("${parser.parsing-on-start}") boolean doInitialUpdating,
                                         ProxySupplier proxySupplier) {
        return new PricesUpdater(parsers,
                menuItemService,
                productService,
                productPriceService,
                doInitialUpdating,
                proxySupplier::clearProxyPool);
    }

    @Bean
    public ModelMapper modelMapper() {
        var mapper = new ModelMapper();
        mapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        return mapper;
    }
}
