package speedrunnervshunter.events;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import speedrunnervshunter.utils.Role;

public class SpeedRunOver {
    public static void registerSpeedRunOver(net.minecraft.entity.LivingEntity entity, net.minecraft.entity.damage.DamageSource damageSource) {
        if(entity instanceof ServerPlayerEntity player){
            if(player.getStringifiedName().equals(Role.INSTANCE.speedrunner)){
                if(damageSource.getAttacker() instanceof ServerPlayerEntity killer){
                    killer.networkHandler.sendPacket(new TitleS2CPacket(
                        Text.literal("YOU WON").formatted(net.minecraft.util.Formatting.GOLD, net.minecraft.util.Formatting.GREEN)
                    )); launchFirework(killer);
                }

                launchFirework(player);
                player.networkHandler.sendPacket(new TitleS2CPacket(
                    Text.literal("YOU LOSE").formatted(net.minecraft.util.Formatting.RED, net.minecraft.util.Formatting.BOLD)
                ));

                Role.INSTANCE.speedrunner = "";
                Role.INSTANCE.hunter= "";
            }else{
                if(!Role.INSTANCE.hunter.isEmpty()) player.getEntityWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.MASTER, 1.0f, 1.0f);
            }
        }
    }
    
    private static void launchFirework(ServerPlayerEntity player) {
        FireworkExplosionComponent explosionRed = new FireworkExplosionComponent(FireworkExplosionComponent.Type.LARGE_BALL, IntList.of(DyeColor.RED.getFireworkColor()), IntList.of(DyeColor.PURPLE.getFireworkColor()), false, false);
        FireworkExplosionComponent explosionBlue = new FireworkExplosionComponent(FireworkExplosionComponent.Type.BURST, IntList.of(DyeColor.WHITE.getFireworkColor()), IntList.of(DyeColor.CYAN.getFireworkColor()), false, false);
        FireworkExplosionComponent explosionWhite = new FireworkExplosionComponent(FireworkExplosionComponent.Type.STAR, IntList.of(DyeColor.BLUE.getFireworkColor()), IntList.of(DyeColor.BLUE.getFireworkColor()), false, false);

        ItemStack fireworkRed = new ItemStack(Items.FIREWORK_ROCKET);
        ItemStack fireworkBlue = new ItemStack(Items.FIREWORK_ROCKET);
        ItemStack fireworkWhite = new ItemStack(Items.FIREWORK_ROCKET);

        fireworkRed.set(DataComponentTypes.FIREWORKS, new FireworksComponent(0, java.util.List.of(explosionRed)));
        fireworkBlue.set(DataComponentTypes.FIREWORKS, new FireworksComponent(0, java.util.List.of(explosionBlue)));
        fireworkWhite.set(DataComponentTypes.FIREWORKS, new FireworksComponent(0, java.util.List.of(explosionWhite)));

        FireworkRocketEntity rocketRed = new FireworkRocketEntity(player.getEntityWorld(), player, player.getX(), player.getY(), player.getZ(), fireworkRed);
        FireworkRocketEntity rocketBlue = new FireworkRocketEntity(player.getEntityWorld(), player, player.getX(), player.getY(), player.getZ(), fireworkBlue);
        FireworkRocketEntity rocketWhite = new FireworkRocketEntity(player.getEntityWorld(), player, player.getX(), player.getY(), player.getZ(), fireworkWhite);
        
        player.getEntityWorld().spawnEntity(rocketRed);
        player.getEntityWorld().spawnEntity(rocketBlue);
        player.getEntityWorld().spawnEntity(rocketWhite);
    }
}
