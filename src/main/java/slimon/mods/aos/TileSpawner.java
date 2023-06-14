package slimon.mods.aos;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

/**
 * Created by user on 12/12/2016.
 */
public class TileSpawner extends TileEntity implements IEnergyReceiver {

    public int cooldown;
    public int timer;
    public float radius;
    public int inEnergy;
    public long lastReceiveTime;

    //public EntityLiving spawnedEntity;
    public Class<? extends EntityLiving> entityClass;
    public Constructor entityConstructor;
    public boolean invalidEntity;
    public boolean exactCopy;
    public boolean enabled;
    //public boolean entityNBTReaded;

    public NBTTagCompound entityNBT;

    public EnergyStorage energyStorage;

    public TileSpawner() {
        super();
        energyStorage = new EnergyStorage(0);
        enabled = true;
    }

    /*public TileSpawner(Class<? extends EntityLiving> entityClass, int cooldown, float radius) {
        this.cooldown = cooldown;
        this.invalidEntity = false;
        this.entityClass = entityClass;
        this.spawnedEntity = createSpawnedEntity();
        setRadius(radius);
        resetTimer();
    }

    public TileSpawner(EntityLiving entity, int cooldown, float radius) {
        this.cooldown = cooldown;
        this.invalidEntity = false;
        this.entityClass = entity.getClass();
        this.spawnedEntity = entity;
        setRadius(radius);
        resetTimer();
    }*/

    public void setRadius(float radius) {
        this.radius = Math.min(20F, Math.max(1F, radius));
    }

    public void updateEntity() {
        if(enabled) {
            timer--;
            if (timer <= 0) {

                EntityLiving newEntity = copyEntity();
                if (newEntity != null) {
                    setSpawnedEntityPos(newEntity);

                    if (canSpawn(newEntity)) {
                        if (!worldObj.isRemote) {
                            energyStorage.setEnergyStored(0);
                            worldObj.spawnEntityInWorld(newEntity);
                        }
                        else {
                            renderSpawnEffects(newEntity);
                        }
                    }
                }
                resetTimer();
            }
            if (worldObj.isRemote) {
                renderWorkEffects();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void renderWorkEffects() {
        double d0 = (double)((float)xCoord + worldObj.rand.nextFloat());
        double d1 = (double)((float)yCoord + worldObj.rand.nextFloat());
        double d2 = (double)((float)zCoord + worldObj.rand.nextFloat());
        worldObj.spawnParticle("smoke", d0, d1, d2, 0.0D, 0.0D, 0.0D);
        worldObj.spawnParticle("flame", d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }

    @SideOnly(Side.CLIENT)
    private void renderSpawnEffects(EntityLiving spawnedEntity) {
        spawnedEntity.spawnExplosionParticle();
    }

    private boolean canSpawn(EntityLiving entity) {
        boolean noEntityColl = worldObj.checkNoEntityCollision(entity.boundingBox);
        boolean noBlocksColl = worldObj.getCollidingBoundingBoxes(entity, entity.boundingBox).isEmpty();
        boolean isLiquid = worldObj.isAnyLiquid(entity.boundingBox);
        boolean enoughEnergy = energyStorage.getEnergyStored() >= energyStorage.getMaxEnergyStored();
        return noEntityColl && noBlocksColl && !isLiquid && enoughEnergy;
    }

    private void setSpawnedEntityPos(Entity entity) {
        Random rand = worldObj.rand;

        float cX = xCoord + 0.5F;
        float cZ = zCoord + 0.5F;
        float rndX = rand.nextFloat();
        float rndZ = rand.nextFloat();

        float x, z;
        int mulX, mulZ;

        mulX = rndX < 0.5F ? -1 : 1;
        mulZ = rndZ < 0.5F ? -1 : 1;

        x = cX + mulX*(0.5F + rndX * (radius - 0.5F));
        z = cZ + mulZ*(0.5F + rndZ * (radius - 0.5F));

        int rounded = (int) radius;
        int y = yCoord + rand.nextInt(rounded + 2) / 2;

        entity.setPositionAndRotation(x, y, z, rand.nextFloat()*360, 0);
    }

    private void initConstructor() {
        if(entityClass == null) return;
        Constructor[] constructors = entityClass.getConstructors();

        for(Constructor c : constructors) {
            Class[] paramTypes = c.getParameterTypes();
            //AOS.instance.debug(Arrays.toString(paramTypes));
            if(paramTypes != null && paramTypes.length == 1) {
                Class param = paramTypes[0];
                if(param.equals(World.class)) {
                    entityConstructor = c;
                    break;
                }
            }
        }
    }

    private EntityLiving createSpawnedEntity() {
        EntityLiving spawnedEntity = null;
        if(entityConstructor == null) {
            initConstructor();
            invalidEntity = entityConstructor == null;

            MobProperty property = Mobs.map.get(entityClass);
            if(property != null) {
                if(!property.allowed) {
                    invalidEntity = true;
                }
                else {
                    updateSettings(property);
                }
            }
            if(property == null || invalidEntity) {
                resetSettings();
            }
        }
        if(entityConstructor != null && worldObj != null) {
            try {
                spawnedEntity = (EntityLiving) entityConstructor.newInstance(worldObj);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return spawnedEntity;
    }

    public EntityLiving copyEntity() {
        EntityLiving entity = createSpawnedEntity();
        if(entity != null && !invalidEntity) {
            if(exactCopy) {
                entity.readFromNBT(entityNBT);
            }
            else {
                //System.out.println(entity.worldObj);
                entity.onSpawnWithEgg(null);
            }
        }
        return entity;
    }

    public void updateSettings(MobProperty property) {
        radius = Mobs.radius;
        cooldown = property.minDelay;
        int spawnCost = exactCopy ? Math.round(property.spawnCost * property.exactCopyCoeff) : property.spawnCost;
        energyStorage.setCapacity(spawnCost);
        energyStorage.setMaxReceive((int) Math.ceil(((float) spawnCost) / property.minDelay));
    }

    public void resetSettings() {
        entityConstructor = null;
        entityClass = null;
        radius = 0;
        cooldown = 0;
        energyStorage.setCapacity(0);
        energyStorage.setMaxReceive(0);
    }

    private void resetTimer() {
        timer = cooldown;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        long time = worldObj.getWorldTime();
        if(time != lastReceiveTime) {
            inEnergy = 0;
            if(!simulate)
                lastReceiveTime = time;
        }
        int max = Math.min(maxReceive, Math.max(energyStorage.getMaxReceive() - inEnergy, 0));
        int received = enabled ? energyStorage.receiveEnergy(max, simulate) : 0;
        if(!simulate)
            inEnergy += received;
        return received;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return energyStorage.getMaxEnergyStored();
    }

    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        SpawnerNBT.readFromNBT(this, tagCompound);
    }

    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        SpawnerNBT.writeToNBT(tagCompound, this);
    }

    public void setExactCopy(boolean value) {
        if(entityClass != null && Mobs.map.containsKey(entityClass)) {
            this.exactCopy = value;
            int baseCost = Mobs.map.get(entityClass).spawnCost;
            float coeff = Mobs.map.get(entityClass).exactCopyCoeff;
            int cost = Math.round(baseCost * (exactCopy ? coeff : 1));
            int maxInput = (int) Math.ceil((float) cost / cooldown);
            energyStorage.setCapacity(cost);
            energyStorage.setMaxReceive(maxInput);
        }
    }
}
