package dev.fr13.controller;

import dev.fr13.domain.WebSite;
import dev.fr13.dto.MenuItemsDtoContainer;
import dev.fr13.services.MenuItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MenuItemsController {
    private static final Logger logger = LoggerFactory.getLogger(MenuItemsController.class);

    private final MenuItemService menuItemService;

    public MenuItemsController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping("/{source}")
    public String getMenuItem(Model model, @PathVariable String source) {
        logger.info("Getting menu item {}", source);
        var menuItemDtos = menuItemService.findAllBySource(WebSite.valueByName(source));
        var container = new MenuItemsDtoContainer(menuItemDtos);
        model.addAttribute("container", container);
        model.addAttribute("name", source);
        return "menu-item";
    }
}
