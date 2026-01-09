package speedrunnervshunter;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import speedrunnervshunter.utils.EventRegistries;
import speedrunnervshunter.utils.ModRegistries;
import speedrunnervshunter.utils.Role;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeedRunnerVsHunter implements ModInitializer {
	public static final String MOD_ID = "speedrunner-vs-hunter";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		ModRegistries.registerCommands();
		EventRegistries.registerEvents();
		ServerTickEvents.END_SERVER_TICK.register(server -> {
            ServerPlayerEntity target = server.getPlayerManager().getPlayer(Role.INSTANCE.speedrunner);
            
            if(target != null){
                for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()){
					if(player.getStringifiedName() == Role.INSTANCE.hunter && !player.getInventory().containsAny(stack -> stack.isOf(Items.COMPASS))){
						player.getInventory().offerOrDrop(new ItemStack(Items.COMPASS));
					}
                    if(player.getStringifiedName() == Role.INSTANCE.hunter) updateCompassInInventory(player, target, target.getEntityWorld());
                }
            }
        });
	}

	private void updateCompassInInventory(ServerPlayerEntity hunter, ServerPlayerEntity speedRunner, World world) {
        for(int i = 0; i < hunter.getInventory().size(); i++){
            ItemStack stack = hunter.getInventory().getStack(i);
            
            if(stack.isOf(Items.COMPASS)){
                GlobalPos speedRunnerPos = GlobalPos.create(
					world.getRegistryKey(), 
					speedRunner.getBlockPos()
				);

				LodestoneTrackerComponent component = new LodestoneTrackerComponent(
					Optional.of(speedRunnerPos), 
					false
				);

				stack.set(DataComponentTypes.CUSTOM_NAME, 
					Text.literal(speedRunner.getStringifiedName()).formatted(Formatting.GOLD, Formatting.BOLD)
				); stack.set(DataComponentTypes.LODESTONE_TRACKER, component); stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
            }
        }
    }
}