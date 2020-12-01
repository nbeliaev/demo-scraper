package dev.fr13.controller;

import dev.fr13.domain.WebSite;
import dev.fr13.dto.MenuItemsDtoContainer;
import dev.fr13.services.MenuItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    private final MenuItemService menuItemService;

    public IndexController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping({"/", "index"})
    public String menuItems(Model model) {
        logger.info("Getting index");
        model.addAttribute("statistics", menuItemService.getTotalStatistic());
        return "index";
    }

    @PostMapping({"/", "index"})
    public String saveMenuItems(
            @ModelAttribute MenuItemsDtoContainer container,
            @RequestParam String sourceName) {
        logger.info("Save menu items of {}", sourceName);
        var menuItems = container.getMenuItems();
        var source = WebSite.valueByName(sourceName);
        menuItems.forEach(i -> menuItemService.update(i, source));
        return "redirect:/";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setAutoGrowCollectionLimit(512);
    }
}
