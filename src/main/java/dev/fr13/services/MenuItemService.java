package dev.fr13.services;

import dev.fr13.domain.MenuItem;
import dev.fr13.domain.WebSite;
import dev.fr13.dto.MenuItemDto;
import dev.fr13.dto.TotalMenuItemStatisticDto;

import java.util.List;
import java.util.Optional;

public interface MenuItemService {

    MenuItem save(MenuItem menuItem);

    void update(MenuItemDto menuItem, WebSite webSite);

    Optional<MenuItem> findBySourceAndUrnWithLastPrices(WebSite source, String urn);

    List<TotalMenuItemStatisticDto> getTotalStatistic();

    List<MenuItemDto> findAllBySource(WebSite source);

    List<MenuItem> findAllActiveBySource(WebSite source);
}
