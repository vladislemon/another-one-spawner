package slimon.mods.aos;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by user on 12/12/2016.
 */

@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.VERSION,
        dependencies = Constants.DEPENDENCIES)

public class AOS {

    @Mod.Instance("AnotherOneSpawner")
    public static AOS instance;

    @SideOnly(Side.CLIENT)
    public CreativeTabs creativeTab;

    public AOS() {
        instance = this;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        initDebug();
        new Config(new Configuration(event.getSuggestedConfigurationFile()));
        Constants.CONFIG_DIR = event.getSuggestedConfigurationFile().getParent();
    }

    BlockSpawner blockSpawner;
    ItemSpawner itemSpawner;
    ItemTrap itemTrap;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if(Config.instance.isModEnabled()) {
            blockSpawner = new BlockSpawner();
            blockSpawner.setBlockName("Spawner");

            itemSpawner = new ItemSpawner(blockSpawner);
            itemSpawner.setUnlocalizedName("Spawner");
            GameRegistry.registerBlock(blockSpawner, itemSpawner.getClass(), "aos_block_spawner");
            GameRegistry.registerTileEntity(TileSpawner.class, "tileSpawner");

            itemTrap = new ItemTrap();
            itemTrap.setUnlocalizedName("MobTrap");
            GameRegistry.registerItem(itemTrap, "MobTrap");

            if(event.getSide().isClient()) {
                creativeTab = new CreativeTabs(Constants.MOD_ID) {
                    @Override
                    public Item getTabIconItem() {
                        return itemSpawner;
                    }
                };
                itemSpawner.setCreativeTab(creativeTab);
                itemTrap.setCreativeTab(creativeTab);
            }
            else {
                MinecraftForge.EVENT_BUS.register(new EntityInteractHandler());
            }
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Recipes.registerRecipes();
        Config.instance.save();
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerAboutToStartEvent event) {
        if(Config.instance.isModEnabled()) {
            Mobs.init(Config.instance);
            Config.instance.save();
        }
    }

    private void initDebug() {
        File f = new File("debug.txt");
        if(f.exists()) {
            f.delete();
        }
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void debug(String s) {
        PrintWriter pw;
        try {
            pw = new PrintWriter(new File("debug.txt"));
            pw.write(s);
            pw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void messagePlayer(EntityPlayer player, String message) {
        if(FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            ChatComponentTranslation msg = new ChatComponentTranslation(message);
            player.addChatMessage(msg);
        }
    }

    public static String getLocalizedString(String name) {
        return StatCollector.translateToLocal(Constants.MOD_ID + "." + name);
    }
}
