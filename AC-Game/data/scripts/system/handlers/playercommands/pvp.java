package playercommands;

import java.util.Calendar;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.ecfunctions.ffa.FFaStruct;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;
import com.aionemu.gameserver.utils.i18n.CustomMessageId;
import com.aionemu.gameserver.utils.i18n.LanguageHandler;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * Created by Ghostfur
 */
public class pvp extends PlayerCommand {

    public pvp() {
        super("pvp");
    }

    public void execute(Player player, String... param) {

        if (player.isAttackMode()) {
            PacketSendUtility.sendMessage(player, "You cannot Go to Insane PvP while in Attack Mode!");
            return;
        }

        if (player.isInDuelArena()) {
            PacketSendUtility.sendMessage(player, "You cannot go to Insane PvP While in 1 vs 1 !");
            return;
        }

        if (player.isInFFA()) {
            PacketSendUtility.sendMessage(player, "You cannot Go to Insane PvP While in FFA Arena!");
            return;
        }

        Calendar calendar = Calendar.getInstance();

        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            monPvP(player);
            givePvPWelcomeMsg(player, "monPvP");
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            wedPvP(player);
            givePvPWelcomeMsg(player, "wedPvP");
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            monPvP(player);
            givePvPWelcomeMsg(player, "monPvP");
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            wedPvP(player);
            givePvPWelcomeMsg(player, "wedPvP");
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            monPvP(player);
            givePvPWelcomeMsg(player, "monPvP");
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            wedPvP(player);
            givePvPWelcomeMsg(player, "wedPvP");
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            monPvP(player);
            givePvPWelcomeMsg(player, "monPvP");
        }
    }

    private void monPvP(Player player) {
        checkotherEvents(player);
        if (player.getWorldId() == 220040000) {
            PacketSendUtility.sendMessage(player, "You cannot use the command inside the Insane PvP Map!");
            return;
        }
        if (player.getRace() == Race.ASMODIANS && !player.isInPrison()) {
            goTo(player, WorldMapType.BELUSLAN.getId(), 2857.0227f, 1774.8656f, 734.1889f);
        } else if (player.getRace() == Race.ELYOS && !player.isInPrison()) {
            goTo(player, WorldMapType.BELUSLAN.getId(), 2366.439f, 2034.8425f, 652.47253f);
        }
    }

    private void tuePvP(Player player) {
        checkotherEvents(player);
        if (player.getWorldId() == 600100000) {
            PacketSendUtility.sendMessage(player, "You cannot use the command inside the Insane PvP Map!");
            return;
        }
        if (player.getRace() == Race.ASMODIANS && !player.isInPrison()) {
            goTo(player, WorldMapType.LEVINSHOR.getId(), 1841.7157f, 1781.8903f, 305.25f);
        } else if (player.getRace() == Race.ELYOS && !player.isInPrison()) {
            goTo(player, WorldMapType.LEVINSHOR.getId(), 98.4f, 109.3f, 347.5f);
        }
    }

    private void wedPvP(Player player) {
        checkotherEvents(player);
        if (player.getRace() == Race.ASMODIANS && player.getWorldId() != 210040000 && !player.isInPrison()) {
            goTo(player, WorldMapType.HEIRON.getId(), 1871.135f, 1724.2709f, 188.16214f);
        } else if (player.getRace() == Race.ELYOS && player.getWorldId() != 210040000 && !player.isInPrison()) {
            goTo(player, WorldMapType.HEIRON.getId(), 1576.9691f, 2726.3843f, 118.25f);
        }
    }

    private void checkotherEvents(Player player) {
        if (player.isAttackMode()) {
            PacketSendUtility.sendMessage(player, LanguageHandler.translate(CustomMessageId.NOT_USE_WHILE_FIGHT));
            return;
        }
        if (player.isInDuelArena()) {
            return;
        }
        if (player.isInFFA() && player.getWorldId() == FFaStruct.worldId) {
            return;
        }
    }

    private static void goTo(final Player player, int worldId, float x, float y, float z) {
        WorldMap destinationMap = World.getInstance().getWorldMap(worldId);
        if (destinationMap.isInstanceType()) {
            TeleportService2.teleportTo(player, worldId, getInstanceId(worldId, player), x, y, z);
        } else {
            TeleportService2.teleportTo(player, worldId, x, y, z);
        }
    }

    private static int getInstanceId(int worldId, Player player) {
        if (player.getWorldId() == worldId) {
            WorldMapInstance registeredInstance = InstanceService.getRegisteredInstance(worldId, player.getObjectId());
            if (registeredInstance != null) {
                return registeredInstance.getInstanceId();
            }
        }
        WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(worldId);
        InstanceService.registerPlayerWithInstance(newInstance, player);
        return newInstance.getInstanceId();
    }

    private void givePvPWelcomeMsg(Player player, String PvPMap) {
        String msg = "";
        if (PvPMap.equalsIgnoreCase("monPvP")) {
            if (player.getWorldId() == 220080000) {
                return;
            }
        } else if (PvPMap.equalsIgnoreCase("TuePvP")) {
            if (player.getWorldId() == 600100000) {
                return;
            }
        } else if (PvPMap.equalsIgnoreCase("wedPvP")) {
            if (player.getWorldId() == 210070000) {
                return;
            }
        } else if (PvPMap.equalsIgnoreCase("monPvP")) {
            if (player.getWorldId() == 220080000) {
                return;
            }
        } else if (PvPMap.equalsIgnoreCase("TuePvP")) {
            if (player.getWorldId() == 600100000) {
                return;
            }
        } else if (PvPMap.equalsIgnoreCase("wedPvP")) {
            if (player.getWorldId() == 210070000) {
                return;
            }
        }

        if (player.getRace() == Race.ASMODIANS) {
            msg = "all the ELYOS :]";
        } else if (player.getRace() == Race.ELYOS) {
            msg = "all the ASMODIANS :]";
        }
        PacketSendUtility.sendYellowMessageOnCenter(player, "[PvP Zone] Welcome to the Insane PvP Zone!!");
        PacketSendUtility.sendYellowMessage(player, "\n[PvP Rules]" + "\n # No Camping at Spawn Area" + "\n # No Hacking" + "\n # No Bug Abusing"
            + "\n # And as always remember to kill " + msg);
    }
}
