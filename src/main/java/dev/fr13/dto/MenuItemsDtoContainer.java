package dev.fr13.dto;

import java.util.List;

public class MenuItemsDtoContainer {
    private List<MenuItemDto> menuItems;

    public MenuItemsDtoContainer() {
    }

    public MenuItemsDtoContainer(List<MenuItemDto> menuItems) {
        this.menuItems = menuItems;
    }

    public List<MenuItemDto> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItemDto> menuItems) {
        this.menuItems = menuItems;
    }

    public void addMenuItem(MenuItemDto menuItem) {
        menuItems.add(menuItem);
    }
}
