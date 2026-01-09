package speedrunnervshunter.commands;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import speedrunnervshunter.utils.Role;

public class SetRoles {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("speedrunnervshunter")
            .then(CommandManager.literal("SpeedRunner")
            .then(CommandManager.argument("player", EntityArgumentType.player())
            .executes(SetRoles::setSpeedrunner)))
            .then(CommandManager.literal("Hunter")
            .then(CommandManager.argument("player", EntityArgumentType.player())
            .executes(SetRoles::setHunter)))
            .then(CommandManager.literal("StartSpeedRun")
            .executes(SetRoles::run))
        );
    }

    public static int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if(Role.INSTANCE.speedrunner != "" && Role.INSTANCE.hunter != ""){
            sendTimedTitle(context.getSource().getServer(), "3", net.minecraft.util.Formatting.RED, 1);
            sendTimedTitle(context.getSource().getServer(), "2", net.minecraft.util.Formatting.GOLD, 2);
            sendTimedTitle(context.getSource().getServer(), "1", net.minecraft.util.Formatting.YELLOW, 3);
            sendTimedTitle(context.getSource().getServer(), "GO!", net.minecraft.util.Formatting.GREEN, 4);
        }else{
            context.getSource().getPlayer().sendMessage(Text.literal("Cannot start, no speed runner or hunter is set").formatted(net.minecraft.util.Formatting.GOLD, net.minecraft.util.Formatting.RED));
        }
        return 1;
    }

    public static int setSpeedrunner(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if(Role.INSTANCE.speedrunner == ""){
            Role.INSTANCE.speedrunner = EntityArgumentType.getPlayer(context, "player").getStringifiedName();

            for(ServerPlayerEntity player : context.getSource().getServer().getPlayerManager().getPlayerList()){
                player.sendMessage(Text.literal("Speed Runner role set to " +EntityArgumentType.getPlayer(context, "player").getStringifiedName()).formatted(net.minecraft.util.Formatting.GOLD, net.minecraft.util.Formatting.GREEN));
            }
            return 1;
        }else{
            context.getSource().getPlayer().sendMessage(Text.literal("Finish the game first before setting another speed runner.").formatted(net.minecraft.util.Formatting.GOLD, net.minecraft.util.Formatting.RED));
            return 0;
        }
    }
    
    public static int setHunter(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if(Role.INSTANCE.hunter == ""){
            Role.INSTANCE.hunter = EntityArgumentType.getPlayer(context, "player").getStringifiedName();

            for(ServerPlayerEntity player : context.getSource().getServer().getPlayerManager().getPlayerList()){
                player.sendMessage(Text.literal("Hunter role set to " +EntityArgumentType.getPlayer(context, "player").getStringifiedName()).formatted(net.minecraft.util.Formatting.GOLD, net.minecraft.util.Formatting.RED));
            }
            return 1;
        }else{   
            context.getSource().getPlayer().sendMessage(Text.literal("Finish the game first before setting another hunter.").formatted(net.minecraft.util.Formatting.GOLD, net.minecraft.util.Formatting.RED));
            return 0;
        }
    }

    public static void sendTimedTitle(MinecraftServer server, String text, Formatting color, int delay) {
        scheduler.schedule(() -> {

            for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()){
                player.networkHandler.sendPacket(new TitleS2CPacket(
                    Text.literal(text).formatted(color)
                ));

                if(delay < 4){
                    player.getEntityWorld().playSound(
                        null,
                        player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(),
                        SoundCategory.MASTER,
                        1.0f,
                        1.0f
                    );
                }else{
                    player.getEntityWorld().playSound(
                        null,
                        player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                        SoundCategory.MASTER,
                        1.0f,
                        1.0f
                    );
                }
            }
        }, delay, TimeUnit.SECONDS);
    }
}
