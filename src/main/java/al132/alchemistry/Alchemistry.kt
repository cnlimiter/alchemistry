package al132.alchemistry

import al132.alchemistry.blocks.ModBlocks
import al132.alchemistry.command.DissolverCommand
import al132.alchemistry.crafting.DankFoodHandler
import al132.alchemistry.crafting.MachineResettingHandler
import al132.alchemistry.crafting.SaltyFoodHandler
import al132.alchemistry.items.ModItems
import crafttweaker.CraftTweakerAPI
import crafttweaker.IAction
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.*
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.apache.logging.log4j.Logger
import java.util.*


//TODO: Everything
@net.minecraftforge.fml.common.Mod(modid = Reference.MODID,
        name = Reference.MODNAME,
        version = Reference.VERSION,
        dependencies = Reference.DEPENDENCIES,
        modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object Alchemistry {

    //https://github.com/jaredlll08/ModTweaker/blob/1.12/src/main/java/com/blamejared/ModTweaker.java
    val LATE_REMOVALS: LinkedList<IAction> = LinkedList()
    val LATE_ADDITIONS: LinkedList<IAction> = LinkedList()

    lateinit var logger: Logger

    @SidedProxy(clientSide = "al132.alchemistry.ClientProxy", serverSide = "al132.alchemistry.CommonProxy")
    var proxy: CommonProxy? = null

    @EventHandler
    fun preInit(e: FMLPreInitializationEvent) {
        proxy!!.preInit(e)
    }

    @EventHandler
    fun init(e: FMLInitializationEvent) = proxy!!.init(e)

    @EventHandler
    fun postInit(e: FMLPostInitializationEvent) = proxy!!.postInit(e)

    @EventHandler
    fun serverStarting(e: FMLServerStartingEvent) {
        e.registerServerCommand(DissolverCommand())
    }

    @EventHandler
    fun loadComplete(e: FMLLoadCompleteEvent) {
        try {
            LATE_REMOVALS.forEach(CraftTweakerAPI::apply)
            LATE_ADDITIONS.forEach(CraftTweakerAPI::apply)
        } catch (e: Exception) {
            e.printStackTrace()
            CraftTweakerAPI.logError("Error while applying actions", e)
        }
        LATE_REMOVALS.clear()
        LATE_ADDITIONS.clear()
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    object Registration {
        @JvmStatic
        @SubscribeEvent
        fun registerBlocks(event: RegistryEvent.Register<Block>) {
            ModBlocks.registerBlocks(event)
        }

        @JvmStatic
        @SubscribeEvent
        fun registerItems(event: RegistryEvent.Register<Item>) {
            ModBlocks.registerItemBlocks(event)
            ModItems.registerItems(event)
        }

        @SideOnly(Side.CLIENT)
        @JvmStatic
        @SubscribeEvent
        fun registerModels(event: ModelRegistryEvent) {
            ModBlocks.registerModels()
            ModItems.registerModels()
        }

        @JvmStatic
        @SubscribeEvent
        fun registerCraftingHandler(event: RegistryEvent.Register<IRecipe>) {
            event.registry.register(DankFoodHandler())
            event.registry.register(SaltyFoodHandler())
            event.registry.register(MachineResettingHandler())

        }
    }
}