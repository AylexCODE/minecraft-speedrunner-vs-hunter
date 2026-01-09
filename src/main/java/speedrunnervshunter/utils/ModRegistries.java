package speedrunnervshunter.utils;

import speedrunnervshunter.commands.SetRoles;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModRegistries {
    public static void registerCommands(){
        CommandRegistrationCallback.EVENT.register(SetRoles::register);
    }
}
