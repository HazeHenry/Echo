package eu.northpoint.echo.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.function.Consumer;

/**
 * Represents a GUI holder.
 */
public interface GuiHolder extends InventoryHolder {

    Consumer<InventoryClickEvent> getOnGlobalDrag();

    Consumer<InventoryClickEvent> getOnTopDrag();

    Consumer<InventoryClickEvent> getOnBottomDrag();

    Consumer<InventoryClickEvent> getOnGlobalClick();

    Consumer<InventoryClickEvent> getOnTopClick();

    Consumer<InventoryClickEvent> getOnBottomClick();

    Consumer<InventoryCloseEvent> getOnClose();

    GuiHolder setOnGlobalDrag(Consumer<InventoryClickEvent> onGlobalDrag);

    GuiHolder setOnTopDrag(Consumer<InventoryClickEvent> onTopDrag);

    GuiHolder setOnBottomDrag(Consumer<InventoryClickEvent> onBottomDrag);

    GuiHolder setOnGlobalClick(Consumer<InventoryClickEvent> onGlobalClick);

    GuiHolder setOnTopClick(Consumer<InventoryClickEvent> onTopClick);

    GuiHolder setOnBottomClick(Consumer<InventoryClickEvent> onBottomClick);

    GuiHolder setOnClose(Consumer<InventoryCloseEvent> onClose);

    boolean cancelClick();

    GuiItem getItem(int slot);

    void onClose(Player player);
}
