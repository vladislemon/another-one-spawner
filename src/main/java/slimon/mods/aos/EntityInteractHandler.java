package slimon.mods.aos;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

/**
 * Created by user on 1/7/2017.
 */
public class EntityInteractHandler {

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onEvent(EntityInteractEvent event) {
        EntityPlayer player = event.entityPlayer;
        System.out.println(1);
        if(player != null && event.target instanceof EntityLiving) {
            EntityLiving target = (EntityLiving) event.target;
            ItemStack stack = player.getCurrentEquippedItem();
            System.out.println(2);
            if(stack != null && stack.getItem() == AOS.instance.itemTrap) {
                System.out.println(3);
                NBTTagCompound tagCompound = stack.getTagCompound();
                if (tagCompound == null) {
                    System.out.println(4);
                    tagCompound = new NBTTagCompound();
                    NBTTagCompound entityTag = new NBTTagCompound();
                    target.writeEntityToNBT(entityTag);
                    tagCompound.setString(SpawnerNBT.ENTITYNAME, EntityList.getEntityString(target));
                    tagCompound.setTag(SpawnerNBT.ENTITY, entityTag);
                    stack.setTagCompound(tagCompound);
                    player.worldObj.removeEntity(target);
                }
            }
        }
    }
}
