package slimon.mods.aos;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by user on 12/12/2016.
 */
public class BlockSpawner extends BlockContainer {

    protected BlockSpawner() {
        super(Material.iron);
        this.isBlockContainer = true;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float offsetX, float offsetY, float offsetZ) {
        if(player.isSneaking() == Mobs.sneakForModeSwitch && player.getCurrentEquippedItem() == null) {
            TileEntity te = world.getTileEntity(x, y, z);
            if(te != null && te instanceof TileSpawner) {
                TileSpawner spawner = (TileSpawner) te;
                spawner.setExactCopy(!spawner.exactCopy);
                String print = AOS.getLocalizedString("exactCopySwitch");
                String yes = AOS.getLocalizedString("yes");
                String no = AOS.getLocalizedString("no");
                print = print.replace("%state%", spawner.exactCopy ? yes : no);
                AOS.messagePlayer(player, print);
            }
        }

        return super.onBlockActivated(world, x, y, z, player, side, offsetX, offsetY, offsetZ);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
        super.onNeighborBlockChange(world, x, y, z, neighborBlock);

        TileEntity te = world.getTileEntity(x, y, z);
        if(te != null && te instanceof TileSpawner) {
            TileSpawner spawner = (TileSpawner) te;
            spawner.enabled = isStronglyPowered(world, x, y, z) != Mobs.invertedActivation;
        }
    }

    public boolean isStronglyPowered(World world, int x, int y, int z) {
        return world.isBlockIndirectlyGettingPowered(x, y, z);
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileSpawner();
    }
}
