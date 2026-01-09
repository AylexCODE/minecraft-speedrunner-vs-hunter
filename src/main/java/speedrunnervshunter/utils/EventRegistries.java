package speedrunnervshunter.utils;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import speedrunnervshunter.events.SpeedRunOver;

public class EventRegistries {
     public static void registerEvents(){
        ServerLivingEntityEvents.AFTER_DEATH.register(SpeedRunOver::registerSpeedRunOver);
     }
}
