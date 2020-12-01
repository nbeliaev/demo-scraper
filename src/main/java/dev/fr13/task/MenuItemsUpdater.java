package dev.fr13.task;

import dev.fr13.html.Parser;
import dev.fr13.services.MenuItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuItemsUpdater {
    private static final Logger logger = LoggerFactory.getLogger(MenuItemsUpdater.class);

    private final List<Parser> parsers;
    private final MenuItemService menuItemService;

    public MenuItemsUpdater(List<Parser> parsers, MenuItemService menuItemService) {
        this.parsers = parsers;
        this.menuItemService = menuItemService;
    }

    @Scheduled(cron = "${parser.menu-items-schedule}")
    public void update() {
        for (Parser parser : parsers) {
            logger.info("Start parsing menu items of {}", parser);
            processSiteData(parser);
            logger.info("Finish parsing menu items of {}", parser);
        }
    }

    private void processSiteData(Parser parser) {
        try {
            var menuItems = parser.parseMainMenu();
            menuItems.forEach(menuItemService::save);
        } catch (Exception e) {
            logger.error("Couldn't process {}", parser);
            logger.error(e.getMessage(), e);
        }
    }
}
