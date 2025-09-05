package eu.northpoint.echo.gui;

import eu.northpoint.echo.Echo;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class Gui implements GuiHolder {

    private static final UUID UID = UUID.randomUUID();
    private final int rows;

    private final Inventory inventory;
    private final List<GuiItem> items = new ArrayList<>();

    private @Getter Consumer<InventoryClickEvent> onGlobalClick;
    private @Getter Consumer<InventoryClickEvent> onTopClick;
    private @Getter Consumer<InventoryClickEvent> onBottomClick;

    private @Getter Consumer<InventoryClickEvent> onGlobalDrag;
    private @Getter Consumer<InventoryClickEvent> onTopDrag;
    private @Getter Consumer<InventoryClickEvent> onBottomDrag;

    private @Getter Consumer<InventoryCloseEvent> onClose;

    private final List<Player> openers = new ArrayList<>();

    private boolean cancelClick = true;

    private Gui previousGui = null;

    public Gui(String name, int rows) {
        this.rows = rows;
        this.inventory = Bukkit.createInventory(this, rows * 9, name);
    }

    public Gui setPreviousGui(Gui gui) {
        this.previousGui = gui;
        //Add a back button to the bottom left corner
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName("§cVissza");
        back.setItemMeta(meta);
        addItem(new GuiItem(back, e -> {
            if (previousGui != null) {
                //Check if any of the items needs to be re-drawn
                previousGui.reDrawIfNeeded();
                previousGui.show((Player) e.getWhoClicked());
            }
        }, 0, rows - 1));
        return this;
    }

    public Gui setPreviousGui(String command) {
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName("§cVissza");
        back.setItemMeta(meta);
        addItem(new GuiItem(back, e -> new ArrayList<>(this.openers).forEach(player -> player.performCommand(command)), 0, rows - 1));
        return this;
    }

    public void addClose() {
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta meta = close.getItemMeta();
        meta.setDisplayName("§c§l✘");
        close.setItemMeta(meta);
        addItem(new GuiItem(close, e -> this.close((Player) e.getWhoClicked()), 4, this.rows - 1));
    }

    private void reDrawIfNeeded() {
        boolean needsRedraw = false;
        for (GuiItem item : items) {
            if (item.shouldReDraw()) {
                needsRedraw = true;
                item.draw();
            }
        }
        if (needsRedraw) {
            refresh();
        }
    }

    public int nextFreeSlot() {
        for (int i = 0; i < this.getInventory().getSize(); i++) {
            if (this.getInventory().getItem(i) == null) {
                return i;
            }
        }
        throw new IllegalStateException("No free slot found.");
    }

    public int nextFreeSlot(int[] bounds) {
        for (int i : bounds) {
            if (this.getInventory().getItem(i) == null) {
                return i;
            }
        }
        throw new IllegalStateException("No free slot found.");
    }

    public Gui addItem(GuiItem item) {
        //Check if x or y is -1, if so we ignore the item and throw an error
        if (item.getX() == -1 || item.getY() == -1) {
            Echo.getInstance().getLogger().warning("Tried to add an item with x or y set to -1, ignoring it. These values are reserved for PaginatedGui");
            return this;
        }

        //Check if we have an item in the same slot, if so override it
        items.removeIf(i -> i.getX() == item.getX() && i.getY() == item.getY());

        items.add(item);
        inventory.setItem(item.getX() + item.getY() * 9, item.getItem());
        return this;
    }

    public Gui addItem(ItemStack item, Consumer<InventoryClickEvent> event, int x, int y) {
        return addItem(new GuiItem(item, event, x, y));
    }

    public Gui addItem(ItemStack item, Consumer<InventoryClickEvent> event, int slot) {
        int x = slot % 9;
        int y = slot / 9;

        return addItem(new GuiItem(item, event, x, y));
    }

    public Gui addItem(ItemStack item, Consumer<InventoryClickEvent> event, Consumer<ItemStack> onDraw, int x, int y) {
        return addItem(new GuiItem(item, event, onDraw, x, y));
    }

    public void refresh() {
        inventory.clear();
        for (GuiItem item : items) {
            inventory.setItem(item.getX() + item.getY() * 9, item.getItem());
        }
    }

    public void reopen(Player player) {
        show(player);
    }

    public void reopenAll() {
        for (Player player : openers) {
            show(player);
        }
    }

    public Gui show(Player player) {
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(Echo.getInstance(), () -> show(player));
            return this;
        }
        player.openInventory(inventory);
        if (!openers.contains(player)) {
            openers.add(player);
        }
        GuiManager.markAsOpen(this);
        return this;
    }

    public Gui close(Player player) {
        player.closeInventory();
        openers.remove(player);
        if (openers.isEmpty()) {
            GuiManager.markAsClosed(this);
        }
        return this;
    }

    public Gui closeAll() {
        for (Player player : openers) {
            player.closeInventory();
        }
        openers.clear();
        GuiManager.markAsClosed(this);
        return this;
    }

    public List<Player> getOpeners() {
        return Collections.unmodifiableList(openers);
    }

    public Gui setCancelClick(boolean cancelClick) {
        this.cancelClick = cancelClick;
        return this;
    }

    public boolean cancelClick() {
        return cancelClick;
    }

    public GuiItem getItem(int slot) {
        int x = slot % 9;
        int y = slot / 9;

        for (GuiItem item : items) {
            if (item.getX() == x && item.getY() == y) {
                return item;
            }
        }
        return null;
    }

    public Gui setOnGlobalClick(Consumer<InventoryClickEvent> onGlobalClick) {
        this.onGlobalClick = onGlobalClick;
        return this;
    }

    public Gui setOnTopClick(Consumer<InventoryClickEvent> onTopClick) {
        this.onTopClick = onTopClick;
        return this;
    }

    public Gui setOnBottomClick(Consumer<InventoryClickEvent> onBottomClick) {
        this.onBottomClick = onBottomClick;
        return this;
    }

    @Override
    public Consumer<InventoryClickEvent> getOnGlobalDrag() {
        return null;
    }

    @Override
    public Consumer<InventoryClickEvent> getOnTopDrag() {
        return null;
    }

    @Override
    public Consumer<InventoryClickEvent> getOnBottomDrag() {
        return null;
    }

    @Override
    public Consumer<InventoryClickEvent> getOnGlobalClick() {
        return null;
    }

    @Override
    public Consumer<InventoryClickEvent> getOnTopClick() {
        return null;
    }

    @Override
    public Consumer<InventoryClickEvent> getOnBottomClick() {
        return null;
    }

    @Override
    public Consumer<InventoryCloseEvent> getOnClose() {
        return null;
    }

    public Gui setOnGlobalDrag(Consumer<InventoryClickEvent> onGlobalDrag) {
        this.onGlobalDrag = onGlobalDrag;
        return this;
    }

    public Gui setOnTopDrag(Consumer<InventoryClickEvent> onTopDrag) {
        this.onTopDrag = onTopDrag;
        return this;
    }

    public Gui setOnBottomDrag(Consumer<InventoryClickEvent> onBottomDrag) {
        this.onBottomDrag = onBottomDrag;
        return this;
    }

    public Gui setOnClose(Consumer<InventoryCloseEvent> onClose) {
        this.onClose = onClose;
        return this;
    }

    public Gui fetchFills(ConfigurationSection config) {
        String fillEmpty = config.getString("Fill Empty");
        if (fillEmpty != null) {
            ItemStack fillEmptyItem = new ItemStack(Material.valueOf(fillEmpty));
            fillEmpty(fillEmptyItem);
        }

        String fillBorder = config.getString("Fill Border");
        if (fillBorder != null) {
            ItemStack fillBorderItem = new ItemStack(Material.valueOf(fillBorder));
            fillBorder(fillBorderItem);
        }

        String fillBottom = config.getString("Fill Bottom");
        if (fillBottom != null) {
            ItemStack fillBottomItem = new ItemStack(Material.valueOf(fillBottom));
            fillBottom(fillBottomItem);
        }
        return this;
    }

    public Gui fillEmpty(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                addItem(new GuiItem(item, i % 9, i / 9));
            }
        }
        return this;
    }

    public Gui fillEmpty(Material material) {
        return fillEmpty(new ItemStack(material));
    }

    public Gui fillBorder(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, item);
            //inventory.setItem(i + (inventory.getSize() - 9), item);
            addItem(new GuiItem(item, i, 0));
        }
        for (int i = 1; i < inventory.getSize() / 9 - 1; i++) {
//            inventory.setItem(i * 9, item);
//            inventory.setItem(i * 9 + 8, item);
            addItem(new GuiItem(item, 0, i));
            addItem(new GuiItem(item, 8, i));
        }
        return this;
    }

    public Gui fillBorder(Material material) {
        return fillBorder(new ItemStack(material));
    }

    public Gui fillBottom(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);

        for (int i = inventory.getSize() - 9; i < inventory.getSize(); i++) {
//            inventory.setItem(i, item);
            addItem(new GuiItem(item, i % 9, i / 9));
        }
        return this;
    }

    public Gui fillBottom(Material material) {
        return fillBottom(new ItemStack(material));
    }

    @Override
    public void onClose(Player player) {
        //Player closed the inventory, lets remove him from the openers list if needed
        openers.remove(player);
        if (openers.isEmpty()) {
            GuiManager.markAsClosed(this);
        }
    }

    public UUID getUID() {
        return UID;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Gui gui = (Gui) obj;
        return UID.equals(gui.getUID());
    }

    @Override
    public int hashCode() {
        return UID.hashCode();
    }
}
