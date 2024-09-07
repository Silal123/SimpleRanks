package simpleranks.utils;

public enum Prefix {

    SYSTEM("§2S§aR", "§4S§cR");

    Prefix(String def, String err) {
        this.def = def;
        this.err = err;
    }
    private final String def;
    private final String err;

    public final String split = "§8 » §7";
    public String def() { return def + split; }
    public String err() { return err + split; }
}
