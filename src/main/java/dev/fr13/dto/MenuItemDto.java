package dev.fr13.dto;

public class MenuItemDto {
    private String name;
    private String urn;
    private boolean active;

    public MenuItemDto() {
    }

    public MenuItemDto(String name, String urn, boolean active) {
        this.name = name;
        this.urn = urn;
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
