package slimon.mods.aos;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by user on 12/14/2016.
 */
public class SpawnerNBT {

    public static void readFromNBT(TileSpawner spawner, NBTTagCompound tagCompound) {
        spawner.cooldown = tagCompound.getInteger(COOLDOWN);
        spawner.setRadius(tagCompound.getFloat(RADIUS));
        spawner.energyStorage.setCapacity(tagCompound.getInteger(ENERGYPERSPAWN));
        spawner.energyStorage.readFromNBT(tagCompound);
        spawner.timer = tagCompound.getInteger(TIMER);
        spawner.enabled = tagCompound.getBoolean(ENABLED);
        spawner.entityClass = (Class<? extends EntityLiving>) EntityList.stringToClassMapping.get(tagCompound.getString(ENTITYNAME));
        //spawner.spawnedEntity = spawner.copyEntity();
        NBTTagCompound entityTag = tagCompound.getCompoundTag(ENTITY);
        if(entityTag != null) {
            //System.out.println("READ ENTITY!");
            spawner.entityNBT = entityTag;
        }
        spawner.setExactCopy(tagCompound.getBoolean(EXACTCOPY));
    }

    public static void writeToNBT(NBTTagCompound tagCompound, TileSpawner spawner) {
        tagCompound.setInteger(COOLDOWN, spawner.cooldown);
        tagCompound.setFloat(RADIUS, spawner.radius);
        spawner.energyStorage.writeToNBT(tagCompound);
        tagCompound.setInteger(ENERGYPERSPAWN, spawner.energyStorage.getMaxEnergyStored());
        tagCompound.setInteger(TIMER, spawner.timer);
        tagCompound.setBoolean(EXACTCOPY, spawner.exactCopy);
        tagCompound.setBoolean(ENABLED, spawner.enabled);
        Class entityClass = spawner.entityClass;
        tagCompound.setString(ENTITYNAME, entityClass != null ? (String) EntityList.classToStringMapping.get(entityClass) : "");
        if(spawner.entityNBT != null) {
            tagCompound.setTag(ENTITY, spawner.entityNBT);
        }
    }

    /*public static void writeToNBT(NBTTagCompound tagCompound, int cooldown, float radius, int energy, int energyPerSpawn,
                                  int maxEnergyPerTick, int timer, boolean exactCopy, boolean enabled, String entityName) {
        tagCompound.setInteger(COOLDOWN, cooldown);
        tagCompound.setFloat(RADIUS, radius);
        tagCompound.setInteger(ENERGY, energy);
        tagCompound.setInteger(ENERGYPERSPAWN, energyPerSpawn);
        tagCompound.setInteger(MAXENERGYPERTICK, maxEnergyPerTick);
        tagCompound.setInteger(TIMER, timer);
        tagCompound.setBoolean(EXACTCOPY, exactCopy);
        tagCompound.setBoolean(ENABLED, enabled);
        tagCompound.setString(ENTITYNAME, entityName);
    }

    public static void writeToNBT(NBTTagCompound tagCompound, int cooldown, float radius, int energy, int energyPerSpawn,
                                  int maxEnergyPerTick, int timer, boolean exactCopy, boolean enabled, EntityLiving spawnedEntity) {
        writeToNBT(tagCompound, cooldown, radius, energy, energyPerSpawn, maxEnergyPerTick, timer, exactCopy, enabled,
                spawnedEntity != null ? EntityList.getEntityString(spawnedEntity) : "");
        if(spawnedEntity != null) {
            NBTTagCompound entityTag = new NBTTagCompound();
            spawnedEntity.writeEntityToNBT(entityTag);
            tagCompound.setTag(ENTITY, entityTag);
        }
    }

    public static void writeToNBT(NBTTagCompound tagCompound, TileSpawner spawner) {
        writeToNBT(tagCompound, spawner.cooldown, spawner.radius, spawner.energyStorage.getEnergyStored(), spawner.energyStorage.getMaxEnergyStored(),
                spawner.energyStorage.getMaxReceive(), spawner.timer, spawner.exactCopy, spawner.enabled, spawner.spawnedEntity);
    }*/

    public static final String COOLDOWN = "cooldown";
    public static final String RADIUS = "radius";
    //public static final String ENERGY = "Energy";
    public static final String ENERGYPERSPAWN = "energyPerSpawn";
    //public static final String MAXENERGYPERTICK = "maxEnergyPerTick";
    //public static final String INVALIDENTITY = "invalidEntity";
    public static final String TIMER = "timer";
    public static final String ENTITYNAME = "entityName";
    public static final String ENTITY = "entity";
    public static final String EXACTCOPY = "exactCopy";
    public static final String ENABLED = "enabled";
}
