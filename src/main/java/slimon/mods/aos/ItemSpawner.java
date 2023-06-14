package slimon.mods.aos;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by user on 12/14/2016.
 */
public class ItemSpawner extends ItemBlock {

    public ItemSpawner(Block block) {
        super(block);
    }

    /*public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        EntitySkeleton skeleton = (EntitySkeleton) EntityList.createEntityByName("Skeleton", world);
        skeleton.setSkeletonType(1);
        skeleton.setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword));
        //skeleton.setCurrentItemOrArmor(1, new ItemStack(Items.diamond_boots));
        skeleton.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
        SpawnerNBT.writeToNBT(tagCompound, 40, 2, 0, 1000, 100, 0, skeleton);
        NBTTagCompound itemTag = new NBTTagCompound();
        itemTag.setTag(TILEBINDING, tagCompound);
        itemStack.setTagCompound(itemTag);
        return itemStack;
    }*/

    /*@Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int metadata, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te != null && te instanceof TileSpawner) {
            player.addExperienceLevel(1);
            TileSpawner spawner = (TileSpawner) te;
            NBTTagCompound tagCompound = stack.getTagCompound().getCompoundTag(TILEBINDING);
            spawner.readFromNBT(tagCompound);
        }

        return super.onItemUse(stack, player, world, x, y, z, metadata, hitX, hitY, hitZ);
    }*/

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        boolean success = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
        if(!world.isRemote && success) {
            Block block = world.getBlock(x, y, z);
            if(block instanceof BlockSpawner) {
                TileEntity spawner = world.getTileEntity(x, y, z);
                NBTTagCompound itemTag = stack.getTagCompound();
                if(itemTag != null) {
                    NBTTagCompound tagCompound = itemTag.getCompoundTag(TILEBINDING);
                    if(tagCompound != null) {
                        spawner.readFromNBT(tagCompound);
                        //((TileSpawner) spawner).setExactCopy(true);
                        world.setTileEntity(x, y, z, spawner);
                    }
                }
            }
        }

        return success;
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List itemList) {

        for(Object idObj : EntityList.IDtoClassMapping.keySet()) {
            int id = (Integer) idObj;
            if(EntityList.entityEggs.containsKey(id)) {
                ItemStack itemStack = new ItemStack(item);
                NBTTagCompound tagCompound = new NBTTagCompound();
                MobProperty property = Mobs.map.get(EntityList.IDtoClassMapping.get(id));
                if(property == null) break;
                int radius = Mobs.radius;
                int cooldown = property.minDelay;
                int cost = property.spawnCost;
                int maxEnergyPerTick = (int)Math.ceil(((float) cost) / cooldown);
                SpawnerNBT.writeToNBT(tagCompound, cooldown, radius, 0, cost, maxEnergyPerTick, 0, EntityList.getStringFromID(id));
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setTag(TILEBINDING, tagCompound);
                itemStack.setTagCompound(itemTag);
                itemList.add(itemStack);
            }
        }

        super.getSubItems(item, tabs, itemList);
    }*/

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
        NBTTagCompound itemTag = itemStack.getTagCompound();
        boolean flag = false;
        if(itemTag != null) {
            NBTTagCompound tagCompound = itemTag.getCompoundTag(TILEBINDING);
            if(tagCompound != null) {
                info.add(tagCompound.getString(SpawnerNBT.ENTITYNAME));
                flag = true;
            }
        }
        if(!flag) info.add("This box is empty, bro");
    }

    public static final String TILEBINDING = "tileBinding";
}
