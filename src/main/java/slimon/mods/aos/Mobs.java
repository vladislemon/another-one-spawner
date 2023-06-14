package slimon.mods.aos;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by user on 12/26/2016.
 */
public class Mobs {

    static HashMap<Class<? extends EntityLiving>, MobProperty> map = new HashMap<Class<? extends EntityLiving>, MobProperty>();
    static int radius;
    static int rebindLvlCost;
    static boolean sneakForModeSwitch;
    static boolean sneakForBinding;
    static boolean invertedActivation;

    static void init(Config config) {
        radius = config.getProperty("Radius of mob spawning", 2, MobProperty.CATEGORY, GLOBAL, RADIUS).getInt();
        rebindLvlCost = config.getProperty("Entity rebind additional cost (xp levels)", 10, MobProperty.CATEGORY, GLOBAL, REBINDCOST).getInt();
        sneakForModeSwitch = config.getProperty("Need sneak for mode switch?", true, MobProperty.CATEGORY, GLOBAL, MODESNEAK).getBoolean();
        sneakForBinding = config.getProperty("Need sneak for entity binding?", false, MobProperty.CATEGORY, GLOBAL, BINDSNEAK).getBoolean();
        invertedActivation = config.getProperty("Is spawner activation by redstone signal inverted?", true, MobProperty.CATEGORY, GLOBAL, INVERTEDACTIVATION).getBoolean();
        Set<Class> entities = EntityList.classToStringMapping.keySet();
        for(Class c : entities) {
            if(EntityLiving.class.isAssignableFrom(c)) {
                String name = (String) EntityList.classToStringMapping.get(c);
                MobProperty property = new MobProperty(name, config);
                map.put((Class<? extends EntityLiving>)c, property);
            }
        }
    }

    static boolean isMobAllowed(Class<? extends EntityLiving> entityClass) {
        /*if(map.size() == 0) {
            init(Config.instance);
            Config.instance.save();
        }*/

        return map.containsKey(entityClass) && map.get(entityClass).allowed;
    }

    static final String RADIUS = "spawnRadius";
    static final String REBINDCOST = "rebindLvlCost";
    static final String MODESNEAK = "sneakForModeSwitch";
    static final String BINDSNEAK = "sneakForBinding";
    static final String INVERTEDACTIVATION = "invertedActivation";
    static final String GLOBAL = "global";
}
