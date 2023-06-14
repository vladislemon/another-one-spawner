package slimon.mods.aos;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.io.File;

/**
 * Created by Notezway on 22.12.2015.
 */
public class Recipes {

    public static void registerRecipes() {
        RecipeConfig config = new RecipeConfig(new File(Constants.CONFIG_DIR + File.separator + "AOS_Recipes.txt"));
        config.load();

        parseAndRegister("MobTrap", config.getString("MobTrap", "\"minecraft:dirt\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\""));
        parseAndRegister("aos_block_spawner", config.getString("Spawner", "\"minecraft:stone\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\""));

        config.save();

    }

    private static void parseAndRegister(String resultName, String recipe) {
        Item resultItem = GameRegistry.findItem(Constants.MOD_ID, resultName);
        if (resultItem == null) return;
        parseAndRegister(new ItemStack(resultItem, 1, OreDictionary.WILDCARD_VALUE), recipe);
    }

    private static void parseAndRegister(ItemStack result, String recipe) {
        String[] array = recipe.split(",");
        //for(String s : array) {
        //    System.out.println(s);
        //}
        Item recipeItem;
        Object[] recipeArray = null;
        boolean flag, isOreDictName, globalFlag = false, globalOreDict = false, shapeless;
        String[] rawNameArray;
        String rawName, itemName, modId, metaString;
        shapeless = extractName(array[0]).equals("shapeless");
        int shift, count = 0, metadata;
        if(shapeless) {
            shift = 1;
            recipeArray = new Object[array.length - 1];
        }
        else {
            shift = 0;
            recipeArray = new Object[array.length*2 + 3];
            recipeArray[0] = "012";
            recipeArray[1] = "345";
            recipeArray[2] = "678";
        }
        for(int i = shift; i < array.length; i++) {
            rawName = extractName(array[i]);
            flag = false;
            if(!rawName.isEmpty()) {
                rawNameArray = rawName.split(":");
                if(rawNameArray.length >= 2) {
                    modId = rawNameArray[0];
                    itemName = rawNameArray[1];
                    metadata = 0;
                    if(rawNameArray.length == 3) {
                        metaString = rawNameArray[2];
                        metadata = metaString.equals("*") ? OreDictionary.WILDCARD_VALUE : Integer.parseInt(metaString);
                    }
                    isOreDictName = modId.equals("ore");
                    globalOreDict |= isOreDictName;
                    recipeItem = isOreDictName ? null : GameRegistry.findItem(modId, itemName);
                    if ((isOreDictName && OreDictionary.doesOreNameExist(itemName)) || recipeItem != null) {
                        if(shapeless) {
                            recipeArray[i - 1] = isOreDictName ? itemName : new ItemStack(recipeItem, 1, metadata);
                        }
                        else {
                            recipeArray[(i + 1) * 2 + 1] = (i + "").charAt(0);
                            recipeArray[(i + 1) * 2 + 2] = isOreDictName ? itemName : new ItemStack(recipeItem, 1, metadata);
                        }
                        count++;
                        globalFlag = flag = true;
                    }
                }
            }
            if(!flag) {
                if(shapeless) {
                    recipeArray[i - 1] = "";
                }
                else {
                    recipeArray[(i + 1) * 2 + 1] = '#';
                    recipeArray[(i + 1) * 2 + 2] = "";
                }
            }
        }
        if(globalFlag) {
            if(shapeless) {
                Object[] oldRecipeArray = recipeArray;
                recipeArray = new Object[count];
                System.arraycopy(oldRecipeArray, 0, recipeArray, 0, count);
            }
            for(Object obj : recipeArray) {
                System.out.println(obj);
            }
            if(globalOreDict) {
                if(shapeless) {
                    System.out.println("dict shapeless");
                    GameRegistry.addRecipe(new ShapelessOreRecipe(result, recipeArray));
                }
                else {
                    System.out.println("dict shaped");
                    GameRegistry.addRecipe(new ShapedOreRecipe(result, recipeArray));
                }
            }
            else {
                if(shapeless) {
                    System.out.println("shapeless");
                    GameRegistry.addShapelessRecipe(result, recipeArray);
                }
                else {
                    System.out.println("shaped");
                    GameRegistry.addShapedRecipe(result, recipeArray);
                }
            }
        }
    }

    private static String extractName(String raw) {
        return raw.replaceAll(" ", "").replaceAll("\"", "");
    }
}
