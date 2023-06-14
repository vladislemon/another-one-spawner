package slimon.mods.aos;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by user on 12/19/2016.
 */
public class ItemTrap extends Item {

    public ItemTrap() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if(tagCompound != null) {
            NBTTagCompound entityTag = tagCompound.getCompoundTag(SpawnerNBT.ENTITY);
            if (entityTag != null) {
                Block block = world.getBlock(x, y ,z);
                String name = tagCompound.getString(SpawnerNBT.ENTITYNAME);
                EntityLiving entity = (EntityLiving)EntityList.createEntityByName(name, world);
                if(entity != null) {
                    entity.readFromNBT(entityTag);
                    if (block == AOS.instance.blockSpawner && player.isSneaking() == Mobs.sneakForBinding) {
                        if(!Mobs.isMobAllowed(entity.getClass())) {
                            AOS.messagePlayer(player, AOS.getLocalizedString("entityNotAllowed").replace("%name%", name));
                            return super.onItemUse(stack, player, world, x, y, z, side, xOffset, yOffset, zOffset);
                        }
                        Class entityClass = entity.getClass();
                        if(Mobs.map.containsKey(entityClass)) {
                            MobProperty mobProperty = Mobs.map.get(entityClass);
                            int needXpLevels = mobProperty.needXpLvls;
                            TileEntity te = world.getTileEntity(x, y, z);
                            if (te != null && te instanceof TileSpawner) {
                                TileSpawner spawner = (TileSpawner) te;
                                if (spawner.entityClass != null) {
                                    needXpLevels += Mobs.rebindLvlCost;
                                }
                                if (player.experienceLevel < needXpLevels) {
                                    AOS.messagePlayer(player, AOS.getLocalizedString("needMoreXp").replace("%count%", String.valueOf(needXpLevels)));
                                    return super.onItemUse(stack, player, world, x, y, z, side, xOffset, yOffset, zOffset);
                                }
                                player.addExperienceLevel(-needXpLevels);
                                spawner.entityNBT = entityTag;
                                spawner.entityClass = entity.getClass();
                                spawner.entityConstructor = null;
                                spawner.updateSettings(mobProperty);
                                stack.setTagCompound(null);
                            }
                        }
                    } else {
                        int[] coords = getCoordsAtSide(x, y, z, side);
                        entity.setPosition(coords[0] + 0.5F, coords[1], coords[2] + 0.5F);
                        if(!world.isRemote)
                            world.spawnEntityInWorld(entity);
                        stack.setTagCompound(null);
                    }
                }
            }
        }
        return super.onItemUse(stack, player, world, x, y, z, side, xOffset, yOffset, zOffset);
    }

    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if(entity instanceof EntityLiving) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if(tagCompound == null) {
                tagCompound = new NBTTagCompound();
                NBTTagCompound entityTag = new NBTTagCompound();
                ((EntityLiving) entity).writeEntityToNBT(entityTag);
                tagCompound.setString(SpawnerNBT.ENTITYNAME, EntityList.getEntityString(entity));
                tagCompound.setTag(SpawnerNBT.ENTITY, entityTag);
                stack.setTagCompound(tagCompound);
                player.worldObj.removeEntity(entity);
                return true;
            }
        }
        return false;
    }

    public static int[] getCoordsAtSide(int x, int y, int z, int side) {
        switch(side) {
            case 0: y--;
                break;
            case 1: y++;
                break;
            case 2: x--;
                break;
            case 3: x++;
                break;
            case 4: z--;
                break;
            case 5: z++;
                break;
        }
        return new int[] {x, y, z};
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
        NBTTagCompound itemTag = itemStack.getTagCompound();
        boolean flag = false;
        if(itemTag != null) {
            String entityName = itemTag.getString(SpawnerNBT.ENTITYNAME);
            if(entityName != null) {
                info.add(entityName);
                flag = true;
            }
        }
        if(!flag) info.add("Empty");
    }
}
