package eu.northpoint.echo.gui;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class GuiItem {

    private final Consumer<InventoryClickEvent> event;
    @Setter
    private Consumer<ItemStack> onDraw = null;
    @Getter
    private final ItemStack item;
    @Getter
    private int x;
    @Getter
    private int y;

    public GuiItem(ItemStack item, int x, int y) {
        this.item = item;
        this.event = null;
        this.x = x;
        this.y = y;
    }

    public GuiItem(ItemStack item, Consumer<InventoryClickEvent> event, int x, int y) {
        this.item = item;
        this.event = event;
        this.x = x;
        this.y = y;
    }

    public GuiItem(ItemStack item, Consumer<InventoryClickEvent> event, Consumer<ItemStack> onDraw, int x, int y) {
        this.item = item;
        this.event = event;
        this.onDraw = onDraw;
        this.x = x;
        this.y = y;
    }

    //Used for PaginatedGui
    public GuiItem(ItemStack item, Consumer<InventoryClickEvent> event) {
        this.item = item;
        this.event = event;
        this.x = -1;
        this.y = -1;
    }

    void onClick(InventoryClickEvent event) {
        if (this.event != null) {
            this.event.accept(event);
        }
    }

    void draw() {
        if (onDraw != null) {
            onDraw.accept(item);
        }
    }

    boolean shouldReDraw() {
        return onDraw != null;
    }
}
