package simpleranks.utils;

import org.bukkit.Material;

import java.util.Arrays;

public enum Color {

    BLACK("Black", Material.BLACK_WOOL, "0"),
    DARK_BLUE("Dark Blue", Material.BLUE_WOOL, "1"),
    DARK_GREEN("Dark Green", Material.GREEN_WOOL, "2"),
    DARK_AQUA("Dark Aqua", Material.DIAMOND_BLOCK, "3"),
    DARK_RED("Dark Red", Material.RED_WOOL, "4"),
    DARK_PURPLE("Dark Purple", Material.PURPLE_WOOL, "5"),
    GOLD("Gold", Material.ORANGE_WOOL, "6"),
    GRAY("Gray", Material.LIGHT_GRAY_WOOL, "7"),
    DARK_GRAY("Dark Gray", Material.GRAY_WOOL, "8"),
    BLUE("Blue", Material.LIGHT_BLUE_WOOL, "9"),
    GREEN("Green", Material.LIME_WOOL, "a"),
    AQUA("Aqua", Material.DIAMOND_BLOCK, "b"),
    RED("Red", Material.RED_WOOL, "c"),
    LIGHT_PURPLE("Light Purple", Material.PURPLE_WOOL, "d"),
    YELLOW("Yellow", Material.YELLOW_WOOL, "e"),
    WHITE("White", Material.WHITE_WOOL, "f");

    private String name;
    private Material material;
    private String color;

    Color(String name, Material material, String color) {
        this.name = name;
        this.material = material;
        this.color = color;
    }

    public String colorName() {
        return this.name;
    }

    public Material material() {
        return this.material;
    }

    public String colorCode() {
        return this.color;
    }

    public static Color getByColorCode(String colorCode) {
        return Arrays.stream(Color.values()).filter(color1 -> color1.color.equals(colorCode)).findFirst().get();
    }

    public static Color getByMaterial(Material material) {
        return Arrays.stream(Color.values()).filter(color1 -> color1.material == material).findFirst().get();
    }

    public static Color getByName(String name) {
        return Arrays.stream(Color.values()).filter(color1 -> color1.name.equals(name)).findFirst().get();
    }


}
