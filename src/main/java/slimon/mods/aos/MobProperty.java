package slimon.mods.aos;

/**
 * Created by user on 12/26/2016.
 */
public class MobProperty {

    String name;
    int minDelay;
    int spawnCost;
    boolean allowed;
    int needXpLvls;
    float exactCopyCoeff;

    public MobProperty(String name, int minDelay, int spawnCost, boolean allowed, int needXpLvls, float exactCopyCoeff) {
        this.name = name;
        this.minDelay = minDelay;
        this.spawnCost = spawnCost;
        this.allowed = allowed;
        this.needXpLvls = needXpLvls;
        this.exactCopyCoeff = exactCopyCoeff;
    }

    public MobProperty(String name, Config config) {
        this.name = name;
        readFromConfig(config);
    }

    public void readFromConfig(Config config) {
        //System.out.println(name);
        minDelay = config.getProperty("Minimum delay between spawn operations", 40, CATEGORY, name, MINDELAY).getInt();
        spawnCost = config.getProperty("Cost of one spawn operation (RF)", 10000, CATEGORY, name, SPAWNCOST).getInt();
        allowed = config.getProperty("Is mob spawn operation allowed?", true, CATEGORY, name, ALLOWED).getBoolean();
        needXpLvls = config.getProperty("Count of xp levels needed for entity binding", 20, CATEGORY, name, NEEDXP).getInt();
        exactCopyCoeff = (float) config.getProperty("Count of xp levels needed for entity binding", 1.5, CATEGORY, name, EC_COEFF).getDouble();
    }

    public static final String CATEGORY = "Mobs";
    public static final String MINDELAY = "minDelay";
    public static final String SPAWNCOST = "spawnCost";
    public static final String ALLOWED = "allowed";
    public static final String NEEDXP = "xpLvlCost";
    public static final String EC_COEFF = "exactCopyCoeff";
}
