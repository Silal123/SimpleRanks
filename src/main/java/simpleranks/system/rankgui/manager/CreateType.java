package simpleranks.system.rankgui.manager;

import org.bukkit.Material;

public enum CreateType {

    RANK("Rank", Material.PAPER), GROUP("Group", Material.BOOK);

    private String name;
    private Material material;

    CreateType(String name, Material material) {
        this.name = name;
        this.material = material;
    }

    public String typeName() { return this.name; }
    public Material material() { return this.material; }

}
