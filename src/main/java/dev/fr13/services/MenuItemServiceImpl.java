package dev.fr13.services;

import dev.fr13.domain.MenuItem;
import dev.fr13.domain.Product;
import dev.fr13.domain.ProductPrice;
import dev.fr13.domain.WebSite;
import dev.fr13.dto.MenuItemDto;
import dev.fr13.dto.TotalMenuItemStatisticDto;
import dev.fr13.repository.MenuItemRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MenuItemServiceImpl implements MenuItemService {
    private static final Logger logger = LoggerFactory.getLogger(MenuItemServiceImpl.class);

    private final MenuItemRepository repository;
    private final ModelMapper mapper;

    public MenuItemServiceImpl(MenuItemRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public MenuItem save(MenuItem menuItem) {
        logger.debug("Save {}", menuItem);
        var optMenuItem = repository.findBySourceAndUrn(menuItem.getSource(), menuItem.getUrn());
        if (optMenuItem.isEmpty()) {
            return repository.save(menuItem);
        } else {
            return menuItem;
        }
    }

    @Override
    public void update(MenuItemDto menuItemDto, WebSite source) {
        var menuItem = mapper.map(menuItemDto, MenuItem.class);
        logger.debug("Looking for menu item with source {} and urn {}", source, menuItemDto.getUrn());
        repository.findBySourceAndUrn(source, menuItemDto.getUrn())
                .ifPresent(item -> {
                    menuItem.setId(item.getId());
                    menuItem.setSource(source);
                    menuItem.setActive(menuItemDto.isActive());
                    logger.debug("Menu item with urn {} was found with id {}", menuItem.getUrn(), menuItem.getId());
                    repository.save(menuItem);
                });
    }

    @Override
    public Optional<MenuItem> findBySourceAndUrnWithLastPrices(WebSite source, String urn) {
        logger.debug("Find by urn {}", urn);

        var menuItemOpt = repository.findBySourceAndUrn(source, urn);
        if (menuItemOpt.isEmpty()) {
            return Optional.empty();
        } else {
            var menuItem = menuItemOpt.get();
            var products = menuItem.getProducts();
            for (Product product : products) {
                var prices = new ArrayList<ProductPrice>();
                product.getPrices().stream()
                        .max(Comparator.comparing(ProductPrice::getCreated))
                        .ifPresent(prices::add);
                product.setPrices(prices);
            }
            return Optional.of(menuItem);
        }
    }

    @Override
    public List<TotalMenuItemStatisticDto> getTotalStatistic() {
        logger.debug("Get total statistic");
        return repository.getTotalStatistic();
    }

    @Override
    public List<MenuItemDto> findAllBySource(WebSite source) {
        logger.debug("Get all menu items");
        var menuItems = repository.findAllBySourceOrderByNameAsc(source);
        return menuItems.stream()
                .map(menuItem -> mapper.map(menuItem, MenuItemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItem> findAllActiveBySource(WebSite source) {
        logger.debug("Get active menu items");
        return repository.findAllBySourceAndActiveTrue(source);
    }
}