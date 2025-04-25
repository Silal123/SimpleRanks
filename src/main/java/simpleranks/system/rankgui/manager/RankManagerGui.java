package simpleranks.system.rankgui.manager;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import simpleranks.system.ChatInput;
import simpleranks.utils.*;
import simpleranks.utils.config.DefaultConfiguration;

import java.util.*;

public class RankManagerGui extends Gui {

    private RankManagerPage page;
    private Rank rank;
    private Map<String, Object> createData = new HashMap<>();

    private int homePage = 0;
    private int ranksPage = 0;
    private int ITEMS_PER_PAGE = 18;
    private Map<Integer, Object> itemIndexes = new HashMap<>();

    private List<Group> groupStorage = new ArrayList<>();
    private List<Rank> rankStorage = new ArrayList<>();

    private Group selectedGroup;

    private Object editItem;

    public RankManagerGui(Player owner) {
        super(owner);
        this.loadHomeInventory();
    }

    public static void handleInventoryEvents(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;

        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);

        Optional<RankManagerGui> activeGui = Gui.getGuiForPlayer(p.getUniqueId(), RankManagerGui.class);
        if (activeGui.isEmpty()) return;

        if (!Objects.equals(e.getClickedInventory(), activeGui.get().activeInventory)) return;

        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 2);
        switch (activeGui.get().page) {
            case HOME -> activeGui.get().handleHomeInventoryEvents(e);
            case RANKS -> activeGui.get().handleRankListInventoryEvents(e);
            case CREATE -> activeGui.get().handleCreateInventoryEvents(e);
            case EDIT -> activeGui.get().handleEditInventoryEvents(e);
        }
    }


    public ItemStack buildRankItem(Rank rank) {
        return new ItemBuilder(Material.PAPER, 1)
                .setDisplayName(rank.color() + rank.displayName())
                .setLore("", "§7Color: §" + rank.color() + rank.colorCode(), "§7Position: §a" + rank.position(), (rank.group() == null ? null : "§7Group: §a" + rank.group().name()), "", "§aCLICK§7 to edit")
                .build();
    }

    public ItemStack buildRankGroupItem(Group group) {
        return new ItemBuilder(Material.BOOK, 1)
                .setDisplayName(group.name())
                .setLore("", "§7Ranks: §a" + group.getRanks().size(), "", "§aCLICK§7 to view ranks", "§cRIGHT-CLICK§7 to edit")
                .build();
    }

    public void loadHomeInventory() {
        page = RankManagerPage.HOME;
        activeInventory = createInventory(9 * 3);

        itemIndexes.clear();

        int startIndex = homePage * ITEMS_PER_PAGE;
        this.groupStorage = Group.groups();

        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            int itemIndex = startIndex + i;
            if (itemIndex > groupStorage.size() -1) break;
            Group group = groupStorage.get(itemIndex);
            activeInventory.setItem(i, buildRankGroupItem(group));
            itemIndexes.put(i, group);
        }

        ItemStack prev = new ItemBuilder(Material.ARROW, 1)
                .setDisplayName("§cPrevious page")
                .build();

        ItemStack next = new ItemBuilder(Material.SPECTRAL_ARROW, 1)
                .setDisplayName("§aNext page")
                .build();

        ItemStack back = new ItemBuilder(Material.BARRIER, 1)
                .setDisplayName("§cBack")
                .build();

        ItemStack create = new ItemBuilder(Material.SLIME_BALL, 1)
                .setDisplayName("§aCreate")
                .build();

        activeInventory.setItem(RankManagerItem.CREATE.slot(), create);
        activeInventory.setItem(RankManagerItem.PREVIOUS_PAGE.slot(), prev);
        activeInventory.setItem(RankManagerItem.NEXT_PAGE.slot(), next);
        activeInventory.setItem(RankManagerItem.BACK.slot(), back);

        updateInventory();
    }

    public void handleHomeInventoryEvents(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();

        if (slot == RankManagerItem.PREVIOUS_PAGE.slot()) {
            if (homePage < 1) {
                homePage = 0;
                return;
            }

            homePage--;
            this.reload();
            return;
        }

        if (slot == RankManagerItem.NEXT_PAGE.slot()) {
            int startIndex = (homePage + 1) * ITEMS_PER_PAGE;
            if (startIndex > groupStorage.size() - 1) return;
            homePage++;
            this.reload();
            return;
        }

        if (slot == RankManagerItem.BACK.slot()) {
            close();
            return;
        }

        if (slot == RankManagerItem.CREATE.slot()) {
            createData.put("type", CreateType.GROUP);
            this.loadCreateInventory();
            return;
        }

        if (itemIndexes.containsKey(slot)) {
            Object clicked = itemIndexes.get(slot);

            if (clicked instanceof Group g) {
                if (e.isRightClick()) {
                    editItem = g;
                    loadEditInventory();
                    return;
                }

                selectedGroup = g;
                loadRankListInventory();
                return;
            }

            return;
        }
    }

    public void loadRankListInventory() {
        page = RankManagerPage.RANKS;
        activeInventory = createInventory(3 * 9);

        itemIndexes.clear();

        // REPLACE homePage

        int startIndex = ranksPage * ITEMS_PER_PAGE;
        if (selectedGroup != null) {
            this.rankStorage = selectedGroup.getRanks();
        } else {
            this.loadHomeInventory();
            return;
        }

        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            int itemIndex = startIndex + i;
            if (itemIndex > rankStorage.size() -1) break;
            Rank rank = rankStorage.get(itemIndex);
            activeInventory.setItem(i, buildRankItem(rank));
            itemIndexes.put(i, rank);
        }

        ItemStack prev = new ItemBuilder(Material.ARROW, 1)
                .setDisplayName("§cPrevious page")
                .build();

        ItemStack next = new ItemBuilder(Material.SPECTRAL_ARROW, 1)
                .setDisplayName("§aNext page")
                .build();

        ItemStack back = new ItemBuilder(Material.BARRIER, 1)
                .setDisplayName("§cBack")
                .build();

        ItemStack create = new ItemBuilder(Material.SLIME_BALL, 1)
                .setDisplayName("§aCreate")
                .build();

        activeInventory.setItem(RankManagerItem.CREATE.slot(), create);
        activeInventory.setItem(RankManagerItem.PREVIOUS_PAGE.slot(), prev);
        activeInventory.setItem(RankManagerItem.NEXT_PAGE.slot(), next);
        activeInventory.setItem(RankManagerItem.BACK.slot(), back);

        updateInventory();
    }

    public void handleRankListInventoryEvents(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();

        if (slot == RankManagerItem.PREVIOUS_PAGE.slot()) {
            if (ranksPage < 1) {
                ranksPage = 0;
                return;
            }

            ranksPage--;
            this.reload();
            return;
        }

        if (slot == RankManagerItem.NEXT_PAGE.slot()) {
            int startIndex = (ranksPage + 1) * ITEMS_PER_PAGE;
            if (startIndex > rankStorage.size() - 1) return;
            ranksPage++;
            this.reload();
            return;
        }

        if (slot == RankManagerItem.BACK.slot()) {
            ranksPage = 0;
            this.loadHomeInventory();
            return;
        }

        if (slot == RankManagerItem.CREATE.slot()) {
            createData.put("type", CreateType.RANK);
            if (selectedGroup != null) createData.put("group", selectedGroup);
            this.loadCreateInventory();
            return;
        }

        if (itemIndexes.containsKey(slot)) {
            Object clicked = itemIndexes.get(slot);

            if (clicked instanceof Rank r) {
                if (e.isRightClick()) return;
                editItem = r;
                loadEditInventory();
                return;
            }

            return;
        }
    }

    public void loadCreateInventory() {
        page = RankManagerPage.CREATE;
        activeInventory = createInventory(9 * 3);

        if (!owner.hasPermission(Permissions.SETUP_GROUP_CREATE.perm()) || !owner.hasPermission(Permissions.SETUP_RANK_CREATE.perm())) {
            ItemStack noPermission = new ItemBuilder(Material.BARRIER, 1)
                    .setDisplayName("§cNo permission")
                    .setLore("§7You dont have permission to", "§7create groups or ranks!")
                    .build();

            activeInventory.setItem(13, noPermission);
            return;
        }

        CreateType type = (CreateType) createData.getOrDefault("type", CreateType.RANK);
        String name = (String) createData.get("name");

        ItemStack typeSelector = new ItemBuilder(type.material(), 1)
                .setDisplayName("§a" + type.typeName())
                .setLore("§7You selected the create type §a" + type.typeName())
                .build();

        activeInventory.setItem(RankManagerItem.CREATE_TYPE.slot(), typeSelector);

        if (type.equals(CreateType.RANK)) {
            Color color = (Color) createData.getOrDefault("color", Color.WHITE);
            Group group = (Group) createData.getOrDefault("group", Group.getDefaultGroup());

            ItemStack editName = new ItemBuilder(Material.NAME_TAG, 1)
                    .setDisplayName(name == null ? "§eSet the name" : "§a" + name)
                    .setLore("§7Set the name wich will display", "§7in the tab bar and chat")
                    .build();

            ItemStack editColor = new ItemBuilder(color.material(), 1)
                    .setDisplayName("§" + color.colorCode() + color.colorName())
                    .setLore("§7This color is later displayed in", "§7the tab bar and in chat")
                    .build();

            ItemStack changeGroup = new ItemBuilder(Material.BOOK, 1)
                    .setDisplayName("§a" + group.name())
                    .setLore("§7This is the group wich the", "§7rank belongs to")
                    .build();

            activeInventory.setItem(RankManagerItem.CREATE_NAME.slot(), editName);
            activeInventory.setItem(RankManagerItem.CREATE_COLOR.slot(), editColor);
            activeInventory.setItem(RankManagerItem.CREATE_GROUP.slot(), changeGroup);
        }

        if (type.equals(CreateType.GROUP)) {
            List<String> permissions = (List<String>) createData.getOrDefault("permissions", new ArrayList<>());

            ItemStack editName = new ItemBuilder(Material.NAME_TAG, 1)
                    .setDisplayName(name == null ? "§eSet the name" : "§a" + name)
                    .setLore("§7Set the name of the group")
                    .build();

            List<String> p = new ArrayList<>();
            for (int i = 0; i < Math.min(10, permissions.size()); i++) {
                p.add("§8-§7 " + permissions.get(i));
            }

            List<String> addPermissionsLore = new ArrayList<>();
            addPermissionsLore.add("§7Add a permission to the selected Group");
            addPermissionsLore.add("");
            addPermissionsLore.add("§7Permissions:");
            addPermissionsLore.addAll(p);

            ItemStack addPermission = new ItemBuilder(Material.CHEST_MINECART, 1)
                    .setDisplayName("§aAdd permission")
                    .setLore(addPermissionsLore)
                    .build();

            List<String> removePermissionsLore = new ArrayList<>();
            removePermissionsLore.add("§7Remove a permission from the selected Group");
            removePermissionsLore.add("");
            removePermissionsLore.add("§7Permissions:");
            removePermissionsLore.addAll(p);

            ItemStack removePermission = new ItemBuilder(Material.MINECART, 1)
                    .setDisplayName("§cRemove Permission")
                    .setLore(removePermissionsLore)
                    .build();

            activeInventory.setItem(RankManagerItem.CREATE_NAME.slot(), editName);
            activeInventory.setItem(RankManagerItem.CREATE_COLOR.slot(), addPermission);
            activeInventory.setItem(RankManagerItem.CREATE_GROUP.slot(), removePermission);
        }

        ItemStack confirm = new ItemBuilder(Material.SLIME_BALL, 1)
                .setDisplayName("§aConfirm")
                .build();

        ItemStack cancel = new ItemBuilder(Material.APPLE, 1)
                .setDisplayName("§cCancel")
                .build();

        activeInventory.setItem(RankManagerItem.CREATE_CONFIRM.slot(), confirm);
        activeInventory.setItem(RankManagerItem.CREATE_CANCEL.slot(), cancel);
    }

    public static void openCreator(Player p, Map<String, Object> create) {
        RankManagerGui man = new RankManagerGui(p);
        man.createData = create;
        man.loadCreateInventory();
    }

    public void handleCreateInventoryEvents(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();

        CreateType type = (CreateType) createData.getOrDefault("type", CreateType.RANK);
        String name = (String) createData.get("name");

        if (slot == RankManagerItem.CREATE_TYPE.slot()) {
            int currentIndex = Arrays.stream(CreateType.values()).toList().indexOf(type);

            if (currentIndex + 1 >= CreateType.values().length) {
                createData.put("type", Arrays.stream(CreateType.values()).toList().get(0));
                this.reload();
                return;
            }

            createData.put("type", Arrays.stream(CreateType.values()).toList().get(currentIndex + 1));
            this.reload();
            return;
        }

        if (slot == RankManagerItem.CREATE_CANCEL.slot()) {
            close();
            createData.clear();
            return;
        }

        if (type.equals(CreateType.RANK)) {
            if (!p.hasPermission(Permissions.SETUP_RANK_CREATE.perm())) {
                p.sendMessage(Prefix.SYSTEM.err() + "You need §crank create§7 permissions to do that!");
                return;
            }

            Color color = (Color) createData.getOrDefault("color", Color.WHITE);
            Group group = (Group) createData.getOrDefault("group", Group.getDefaultGroup());

            if (slot == RankManagerItem.CREATE_CONFIRM.slot()) {
                if (color == null) {
                    p.sendMessage(Prefix.SYSTEM.err() + "Please select a §ccolor§7!");
                    return;
                }

                if (group == null) {
                    p.sendMessage(Prefix.SYSTEM.err() + "Please select a §cgroup§7!");
                    return;
                }

                if (name == null) {
                    p.sendMessage(Prefix.SYSTEM.err() + "Please select a §cname§7!");
                    return;
                }

                close();
                Rank rank = Rank.newRank(name, color.colorCode());
                if (group != Group.getDefaultGroup()) rank.setGroup(group);
                p.sendMessage(Prefix.SYSTEM.def() + "The rank " + rank.color() + rank.displayName() + "§7 was successfully created!");
                return;
            }

            if (slot == RankManagerItem.CREATE_NAME.slot()) {
                close();
                p.sendMessage(Prefix.SYSTEM.def() + "Please input the name of the rank: (type 'exit' to exit)");
                ChatInput.listenForInput(p, (res) -> {
                    if (res.contains(" ")) {
                        p.sendMessage(Prefix.SYSTEM.err() + "Please pick a name §cwithout spaces§7!");
                        openCreator(p, createData);
                        return;
                    }

                    if (res.equals("exit")) {
                        p.sendMessage(Prefix.SYSTEM.def() + "Name set was §ccancelled§7!");
                        openCreator(p, createData);
                        return;
                    }

                    if (res.length() > Rank.NAME_CHAR_LIMIT) {
                        p.sendMessage(Prefix.SYSTEM.err() + "The specified name is too long! Please use a §cmaximum of " + Rank.NAME_CHAR_LIMIT + "§7 characters!");
                        openCreator(p, createData);
                        return;
                    }

                    if (Rank.isRankExistent(res)) {
                        p.sendMessage(Prefix.SYSTEM.err() + "A rank with the name §c" + res + "§7 already exists!");
                        openCreator(p, createData);
                        return;
                    }

                    createData.put("name", res);
                    p.sendMessage(Prefix.SYSTEM.def() + "You set the name of the rank to §a" + res + "§7!");
                    openCreator(p, createData);
                });
                return;
            }

            if (slot == RankManagerItem.CREATE_COLOR.slot()) {
                int currentIndex = Arrays.stream(Color.values()).toList().indexOf(color);

                if (currentIndex + 1 >= Color.values().length) {
                    createData.put("color", Arrays.stream(Color.values()).toList().get(0));
                    this.reload();
                    return;
                }

                createData.put("color", Arrays.stream(Color.values()).toList().get(currentIndex + 1));
                this.reload();
                return;
            }

            if (slot == RankManagerItem.CREATE_GROUP.slot()) {
                close();
                if (groupStorage.isEmpty()) groupStorage = Group.groups();
                p.sendMessage(Prefix.SYSTEM.def() + "All existing §agroups§7: ");
                for (Group g : groupStorage) {
                    p.sendMessage(Prefix.SYSTEM.def() + "§8-§7 " + g.name());
                }
                p.sendMessage(Prefix.SYSTEM.def() + "Please input the §aname of the Group§7: (type 'exit' to exit)");
                ChatInput.listenForInput(p, (res) -> {
                    if (res.contains(" ")) {
                        p.sendMessage(Prefix.SYSTEM.err() + "Please pick a name §cwithout spaces§7!");
                        openCreator(p, createData);
                        return;
                    }

                    if (res.equals("exit")) {
                        p.sendMessage(Prefix.SYSTEM.def() + "Group change was §ccancelled§7!");
                        openCreator(p, createData);
                        return;
                    }

                    if (!Group.isGroupExistent(res)) {
                        p.sendMessage(Prefix.SYSTEM.err() + "The specified §cGroup§7 is not existent!");
                        openCreator(p, createData);
                        return;
                    }

                    Group sg = Group.get(res);
                    createData.put("group", sg);
                    p.sendMessage(Prefix.SYSTEM.def() + "You selected the group §a" + group.name() + "§7!");
                    openCreator(p, createData);
                });
                return;
            }
        }

        if (type.equals(CreateType.GROUP)) {
            if (!p.hasPermission(Permissions.SETUP_GROUP_CREATE.perm())) {
                p.sendMessage(Prefix.SYSTEM.err() + "You need §cgroup create§7 permissions to do that!");
                return;
            }

            List<String> permissions = (List<String>) createData.getOrDefault("permissions", new ArrayList<>());

            if (slot == RankManagerItem.CREATE_CONFIRM.slot()) {
                if (permissions == null) {
                    permissions = new ArrayList<>();
                }

                if (name == null) {
                    p.sendMessage(Prefix.SYSTEM.err() + "Please select a §cname§7!");
                    return;
                }

                close();
                Group group = Group.newGroup(name, permissions);
                p.sendMessage(Prefix.SYSTEM.def() + "The group §a" + group.name() + "§7 was successfully created!");
                return;
            }

            if (slot == RankManagerItem.CREATE_NAME.slot()) {
                close();
                p.sendMessage(Prefix.SYSTEM.def() + "Please input the name of the group: (type 'exit' to exit)");
                ChatInput.listenForInput(p, (res) -> {
                    if (res.contains(" ")) {
                        p.sendMessage(Prefix.SYSTEM.err() + "Please pick a name §cwithout spaces§7!");
                        openCreator(p, createData);
                        return;
                    }

                    if (res.equals("exit")) {
                        p.sendMessage(Prefix.SYSTEM.def() + "Name set was §ccancelled§7!");
                        openCreator(p, createData);
                        return;
                    }

                    if (res.length() > Group.NAME_CHAR_LIMIT) {
                        p.sendMessage(Prefix.SYSTEM.err() + "The specified name is too long! Please use a §cmaximum of " + Group.NAME_CHAR_LIMIT + "§7 characters!");
                        openCreator(p, createData);
                        return;
                    }

                    if (Group.isGroupExistent(res)) {
                        p.sendMessage(Prefix.SYSTEM.err() + "A group with the name §c" + res + "§7 already exists!");
                        openCreator(p, createData);
                        return;
                    }

                    createData.put("name", res);
                    p.sendMessage(Prefix.SYSTEM.def() + "You set the name of the group to §a" + res + "§7!");
                    openCreator(p, createData);
                });
                return;
            }

            if (slot == RankManagerItem.CREATE_COLOR.slot()) {
                //ADD PERMISSION
                close();
                p.sendMessage(Prefix.SYSTEM.def() + "§7Enter the §apermission§7 you want to add: (type 'exit' to exit)");
                final List<String> perms = permissions;
                ChatInput.listenForInput(p, (res) -> {
                    if (res.contains(" ")) {
                        p.sendMessage(Prefix.SYSTEM.err() + "Please pick a name §cwithout spaces§7!");
                        openCreator(p, createData);
                        return;
                    }

                    if (res.equals("exit")) {
                        p.sendMessage(Prefix.SYSTEM.def() + "Process was §ccancelled§7!");
                        openCreator(p, createData);
                        return;
                    }

                    p.sendMessage(Prefix.SYSTEM.def() + "Adding permission §a" + res + "§7 to group");

                    perms.add(res);
                    createData.put("permissions", perms);

                    openCreator(p, createData);
                });
                return;
            }

            if (slot == RankManagerItem.CREATE_GROUP.slot()) {
                //REMOVE PERMISSION
                close();
                p.sendMessage(Prefix.SYSTEM.def() + "§7Enter the §apermission§7 you want to remove: (type 'exit' to exit)");
                final List<String> perms = permissions;
                ChatInput.listenForInput(p, (res) -> {
                    if (res.contains(" ")) {
                        p.sendMessage(Prefix.SYSTEM.err() + "Please pick a name §cwithout spaces§7!");
                        openCreator(p, createData);
                        return;
                    }

                    if (res.equals("exit")) {
                        p.sendMessage(Prefix.SYSTEM.def() + "Process was §ccancelled§7!");
                        openCreator(p, createData);
                        return;
                    }

                    p.sendMessage(Prefix.SYSTEM.def() + "Removing permission §a" + res + "§7 from group");

                    perms.remove(res);
                    createData.put("permissions", perms);

                    openCreator(p, createData);
                });
                return;
            }
        }
    }

    public void loadEditInventory() {
        page = RankManagerPage.EDIT;
        activeInventory = createInventory(9 * 3);

        if (editItem == null) return;

        if (editItem instanceof Rank r) {
            if (!owner.hasPermission(Permissions.SETUP_RANK_MODIFY.perm())) {
                ItemStack noPermission = new ItemBuilder(Material.BARRIER, 1)
                        .setDisplayName("§cNo permission")
                        .setLore("§7You dont have permission to", "§7edit ranks!")
                        .build();

                activeInventory.setItem(13, noPermission);
                return;
            }

            ItemStack editName = new ItemBuilder(Material.NAME_TAG, 1)
                    .setDisplayName("§aChange name")
                    .setLore("§7Edit the name of the current Rank", "", "§7Current: §a" + r.displayName())
                    .build();

            ItemStack editColor = new ItemBuilder(Color.getByColorCode(r.colorCode()).material(), 1)
                    .setDisplayName("§aChange color")
                    .setLore("§7Change the color of the current Rank", "", "§7Current: " + r.color() + r.colorCode())
                    .build();

            ItemStack changeGroup = new ItemBuilder(Material.BOOK, 1)
                    .setDisplayName("§aChange group")
                    .setLore("§7Change the Group of the current Rank", "", "§7Current: §a" + r.group().name())
                    .build();

            ItemStack moveUp = new ItemBuilder(Material.SLIME_BALL, 1)
                    .setDisplayName("§aMove up")
                    .setLore("§7Move the Rank up", "", "§7Position: §e" + r.position() + "§8 -> §a" + (r.position() - 1 < 0 ? 0 : r.position() - 1))
                    .build();

            ItemStack moveDown = new ItemBuilder(Material.APPLE, 1)
                    .setDisplayName("§cMove down")
                    .setLore("§7Move the Rank down", "", "§7Position: §e" + r.position() + "§8 -> §a" + (r.position() + 1))
                    .build();

            activeInventory.setItem(RankManagerItem.EDIT_NAME.slot(), editName);
            activeInventory.setItem(RankManagerItem.EDIT_COLOR.slot(), editColor);
            activeInventory.setItem(RankManagerItem.EDIT_GROUP.slot(), changeGroup);

            activeInventory.setItem(RankManagerItem.MOVE_UP.slot(), moveUp);
            activeInventory.setItem(RankManagerItem.MOVE_DOWN.slot(), moveDown);
        }

        if (editItem instanceof Group g) {
            if (!owner.hasPermission(Permissions.SETUP_GROUP_MODIFY.perm())) {
                ItemStack noPermission = new ItemBuilder(Material.BARRIER, 1)
                        .setDisplayName("§cNo permission")
                        .setLore("§7You dont have permission to", "§7edit groups!")
                        .build();

                activeInventory.setItem(13, noPermission);
                return;
            }

            ItemStack editName = new ItemBuilder(Material.NAME_TAG, 1)
                    .setDisplayName("§aChange name")
                    .setLore("§7Edit the name of the current Group", "", "§7Current: §a" + g.name())
                    .build();

            ItemStack editColor = new ItemBuilder(Material.BARRIER, 1)
                    .setDisplayName("§cNot available")
                    .build();

            ItemStack changeGroup = new ItemBuilder(Material.BARRIER, 1)
                    .setDisplayName("§cNot available")
                    .build();

            List<String> permissions = new ArrayList<>();
            List<String> p = g.permissions();
            for (int i = 0; i < Math.min(10, p.size()); i++) {
                permissions.add("§8-§7 " + p.get(i));
            }

            List<String> addPermissionsLore = new ArrayList<>();
            addPermissionsLore.add("§7Add a permission to the selected Group");
            addPermissionsLore.add("");
            addPermissionsLore.add("§7Permissions:");
            addPermissionsLore.addAll(permissions);

            ItemStack addPermission = new ItemBuilder(Material.CHEST_MINECART, 1)
                    .setDisplayName("§aAdd permission")
                    .setLore(addPermissionsLore)
                    .build();

            List<String> removePermissionsLore = new ArrayList<>();
            removePermissionsLore.add("§7Remove a permission from the selected Group");
            removePermissionsLore.add("");
            removePermissionsLore.add("§7Permissions:");
            removePermissionsLore.addAll(permissions);

            ItemStack removePermission = new ItemBuilder(Material.MINECART, 1)
                    .setDisplayName("§cRemove Permission")
                    .setLore(removePermissionsLore)
                    .build();

            activeInventory.setItem(RankManagerItem.EDIT_NAME.slot(), editName);
            activeInventory.setItem(RankManagerItem.EDIT_COLOR.slot(), editColor);
            activeInventory.setItem(RankManagerItem.EDIT_GROUP.slot(), changeGroup);

            activeInventory.setItem(RankManagerItem.MOVE_UP.slot(), addPermission);
            activeInventory.setItem(RankManagerItem.MOVE_DOWN.slot(), removePermission);
        }

        ItemStack back = new ItemBuilder(Material.BARRIER, 1)
                .setDisplayName("§cBack")
                .build();

        activeInventory.setItem(RankManagerItem.BACK.slot(), back);

        updateInventory();
    }

    public static void openEditor(Player p, Object editItem) {
        RankManagerGui gui = new RankManagerGui(p);
        gui.editItem = editItem;
        gui.loadEditInventory();
    }

    public void handleEditInventoryEvents(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();

        if (editItem instanceof Rank r) {
            if (!p.hasPermission(Permissions.SETUP_RANK_MODIFY.perm())) {
                p.sendMessage(Prefix.SYSTEM.err() + "You need §crank modify§7 permissions to do that!");
                return;
            }

            if (slot == RankManagerItem.BACK.slot()) {
                ranksPage = 0;
                this.loadRankListInventory();
                return;
            }

            if (slot == RankManagerItem.EDIT_NAME.slot()) {
                close();
                p.sendMessage(Prefix.SYSTEM.def() + "Please input the §anew name§7 of the rank §a" + r.displayName() + "§7: (type 'exit' to exit)");
                ChatInput.listenForInput(p, (res) -> {
                    if (res.contains(" ")) {
                        p.sendMessage(Prefix.SYSTEM.err() + "Please pick a name §cwithout spaces§7!");
                        openEditor(p, r);
                        return;
                    }

                    if (res.equals("exit")) {
                        p.sendMessage(Prefix.SYSTEM.def() + "Rename was §ccancelled§7!");
                        openEditor(p, r);
                        return;
                    }

                    if (res.length() > Rank.NAME_CHAR_LIMIT) {
                        p.sendMessage(Prefix.SYSTEM.err() + "The specified name is too long! Please use a §cmaximum of " + Rank.NAME_CHAR_LIMIT + "§7 characters!");
                        openEditor(p, r);
                        return;
                    }

                    if (Rank.isRankExistent(res)) {
                        p.sendMessage(Prefix.SYSTEM.err() + "A rank with the name §c" + res + "§7 already exists!");
                        openEditor(p, r);
                        return;
                    }

                    if (DefaultConfiguration.defaultRank.get().equals(r.displayName())) DefaultConfiguration.defaultRank.set(res);
                    r.setDisplayName(res);
                    p.sendMessage(Prefix.SYSTEM.def() + "You changed the name of the rank to §a" + res + "§7!");
                    openEditor(p, r);
                });
            }

            if (slot == RankManagerItem.EDIT_COLOR.slot()) {
                Color current = Color.getByColorCode(r.colorCode());
                int currentIndex = Arrays.stream(Color.values()).toList().indexOf(current);

                if (currentIndex + 1 >= Color.values().length) {
                    r.setColor(Arrays.stream(Color.values()).toList().get(0).colorCode());
                    this.reload();
                    return;
                }

                r.setColor(Arrays.stream(Color.values()).toList().get(currentIndex + 1).colorCode());
                this.reload();
                return;
            }

            if (slot == RankManagerItem.EDIT_GROUP.slot()) {
                close();
                if (groupStorage.isEmpty()) groupStorage = Group.groups();
                p.sendMessage(Prefix.SYSTEM.def() + "All existing §agroups§7: ");
                for (Group group : groupStorage) {
                    p.sendMessage(Prefix.SYSTEM.def() + (r.group().id() == group.id() ? "§a✔§7 " : "§8-§7") + " " + group.name());
                }
                p.sendMessage(Prefix.SYSTEM.def() + "Please input the §aname of the Group§7: (type 'exit' to exit)");
                ChatInput.listenForInput(p, (res) -> {
                    if (res.contains(" ")) {
                        p.sendMessage(Prefix.SYSTEM.err() + "Please pick a name §cwithout spaces§7!");
                        openEditor(p, r);
                        return;
                    }

                    if (res.equals("exit")) {
                        p.sendMessage(Prefix.SYSTEM.def() + "Group change was §ccancelled§7!");
                        openEditor(p, r);
                        return;
                    }

                    if (!Group.isGroupExistent(res)) {
                        p.sendMessage(Prefix.SYSTEM.err() + "The specified §cGroup§7 is not existent!");
                        openEditor(p, r);
                        return;
                    }

                    Group group = Group.get(res);
                    r.setGroup(group);
                    p.sendMessage(Prefix.SYSTEM.def() + "You changed the group of the Rank §6" + r.displayName() + "§7 to §a" + group.name() + "§7!");
                    openEditor(p, r);
                });
                return;
            }

            if (slot == RankManagerItem.MOVE_UP.slot()) {
                if (r.position() < 1) {
                    p.sendMessage(Prefix.SYSTEM.err() + "The rank is already at the §ctop§7!");
                    return;
                }
                int oldPos = r.position();
                Rank downRank = Rank.get(r.position() - 1);
                if (downRank != null) downRank.setPosition(downRank.position() +1);
                r.setPosition(r.position() -1);
                p.sendMessage(Prefix.SYSTEM.def() + "The rank was moved from §e" + oldPos + "§7 to §a" + r.position() + "§7");
                this.reload();
                return;
            }

            if (slot == RankManagerItem.MOVE_DOWN.slot()) {
                if (r.position() > Rank.ranks().size() - 2) {
                    p.sendMessage(Prefix.SYSTEM.err() + "The rank is already at the §cbottom§7!");
                    return;
                }
                int oldPos = r.position();
                Rank upRank = Rank.get(r.position() + 1);
                if (upRank != null) upRank.setPosition(upRank.position() -1);
                r.setPosition(r.position() +1);
                p.sendMessage(Prefix.SYSTEM.def() + "The rank was moved from §e" + oldPos + "§7 to §a" + r.position() + "§7");
                this.reload();
                return;
            }
        }

        if (editItem instanceof Group g) {
            if (!p.hasPermission(Permissions.SETUP_GROUP_MODIFY.perm())) {
                p.sendMessage(Prefix.SYSTEM.err() + "You need §cgroup modify§7 permissions to do that!");
                return;
            }

            if (slot == RankManagerItem.BACK.slot()) {
                homePage = 0;
                this.loadHomeInventory();
                return;
            }

            if (slot == RankManagerItem.EDIT_NAME.slot()) {
                close();
                p.sendMessage(Prefix.SYSTEM.def() + "Please input the §anew name§7 of the group §a" + g.name() + "§7: (type 'exit' to exit)");
                ChatInput.listenForInput(p, (res) -> {
                    if (res.contains(" ")) {
                        p.sendMessage(Prefix.SYSTEM.err() + "Please pick a name §cwithout spaces§7!");
                        openEditor(p, g);
                        return;
                    }

                    if (res.equals("exit")) {
                        p.sendMessage(Prefix.SYSTEM.def() + "Rename was §ccancelled§7!");
                        openEditor(p, g);
                        return;
                    }

                    if (res.length() > Group.NAME_CHAR_LIMIT) {
                        p.sendMessage(Prefix.SYSTEM.err() + "The specified name is too long! Please use a §cmaximum of " + Group.NAME_CHAR_LIMIT + "§7 characters!");
                        openEditor(p, g);
                        return;
                    }

                    if (Group.isGroupExistent(res)) {
                        p.sendMessage(Prefix.SYSTEM.err() + "A group with the name §c" + res + "§7 already exists!");
                        openEditor(p, g);
                        return;
                    }

                    g.setName(res);
                    p.sendMessage(Prefix.SYSTEM.def() + "You changed the name of the group to §a" + res + "§7!");
                    openEditor(p, g);
                });
                return;
            }

            if (slot == RankManagerItem.MOVE_UP.slot()) {
                close();
                p.sendMessage(Prefix.SYSTEM.def() + "§7Enter the §apermission§7 you want to add: (type 'exit' to exit)");
                ChatInput.listenForInput(p, (res) -> {
                    if (res.contains(" ")) {
                        p.sendMessage(Prefix.SYSTEM.err() + "Please pick a name §cwithout spaces§7!");
                        openEditor(p, g);
                        return;
                    }

                    if (res.equals("exit")) {
                        p.sendMessage(Prefix.SYSTEM.def() + "Process was §ccancelled§7!");
                        openEditor(p, g);
                        return;
                    }

                    p.sendMessage(Prefix.SYSTEM.def() + "Adding permission §a" + res + "§7 to group §a" + g.name());

                    List<String> perms = g.permissions();
                    perms.add(res);
                    g.setPermissions(perms);

                    openEditor(p, g);
                });
                return;
            }

            if (slot == RankManagerItem.MOVE_DOWN.slot()) {
                close();
                p.sendMessage(Prefix.SYSTEM.def() + "§7Enter the §apermission§7 you want to remove: (type 'exit' to exit)");
                ChatInput.listenForInput(p, (res) -> {
                    if (res.contains(" ")) {
                        p.sendMessage(Prefix.SYSTEM.err() + "Please pick a name §cwithout spaces§7!");
                        openEditor(p, g);
                        return;
                    }

                    if (res.equals("exit")) {
                        p.sendMessage(Prefix.SYSTEM.def() + "Process was §ccancelled§7!");
                        openEditor(p, g);
                        return;
                    }

                    p.sendMessage(Prefix.SYSTEM.def() + "Removing permission §a" + res + "§7 from group §a" + g.name());

                    List<String> perms = g.permissions();
                    perms.remove(res);
                    g.setPermissions(perms);

                    openEditor(p, g);
                });
                return;
            }
        }
    }

    @Override
    public void reload() {
        switch (page) {
            case HOME -> loadHomeInventory();
            case RANKS -> loadRankListInventory();
            case CREATE -> loadCreateInventory();
            case EDIT -> loadEditInventory();
        }
    }

    @Override
    public String getTitle() {
        return "§cRank Manager";
    }


}
