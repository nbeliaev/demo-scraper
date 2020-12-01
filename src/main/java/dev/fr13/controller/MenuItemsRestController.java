package dev.fr13.controller;

import dev.fr13.domain.WebSite;
import dev.fr13.dto.MenuItemDto;
import dev.fr13.dto.ProductsMenuItemDto;
import dev.fr13.dto.TotalMenuItemStatisticDto;
import dev.fr13.exception.NoSuchResourceException;
import dev.fr13.services.MenuItemService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MenuItemsRestController {
    private static final Logger logger = LoggerFactory.getLogger(MenuItemsRestController.class);

    private final MenuItemService menuItemService;
    private final ModelMapper mapper;

    public MenuItemsRestController(MenuItemService menuItemService, ModelMapper mapper) {
        this.menuItemService = menuItemService;
        this.mapper = mapper;
    }

    @GetMapping(path = "/api/v1")
    public ResponseEntity<List<TotalMenuItemStatisticDto>> getSources() {
        logger.info("Getting source list");
        var statistic = menuItemService.getTotalStatistic();
        return new ResponseEntity<>(statistic, HttpStatus.OK);
    }

    @GetMapping(path = "/api/v1/{source}")
    public ResponseEntity<List<MenuItemDto>> getMenuItems(@PathVariable String source) {
        logger.info("Getting list of menu items by {} ", source);
        var menuItems = menuItemService.findAllActiveBySource(WebSite.valueByName(source));
        var menuItemsDto = menuItems.stream()
                .map(menuItem -> mapper.map(menuItem, MenuItemDto.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(menuItemsDto, HttpStatus.OK);
    }

    @GetMapping("/api/v1/{source}/{catalogName}/{menuItemName}")
    public ResponseEntity<ProductsMenuItemDto> getMenuItem(
            @PathVariable String source,
            @PathVariable String catalogName,
            @PathVariable String menuItemName) {

        var urn = String.format("/%s/%s/", catalogName, menuItemName);
        logger.info("Getting {} menu item {}", source, urn);
        var menuItemOpt = menuItemService.findBySourceAndUrnWithLastPrices(WebSite.valueByName(source), urn);
        var menuItem = menuItemOpt.orElseThrow(
                () -> new NoSuchResourceException(String.format("No such menu item %s", urn)));

        var menuItemDto = mapper.map(menuItem, ProductsMenuItemDto.class);
        return new ResponseEntity<>(menuItemDto, HttpStatus.OK);
    }
}
