package instance.AionBGs;

import java.util.Map;

import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.services.ecfunctions.ffa.DFFAService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

@InstanceID(320110000)
public class DuoFFA_AlquimiaLab extends DFFAService {

    private Map<Integer, StaticDoor> doors;

    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        doors = instance.getDoors();
        doors.get(6).setOpen(true);
        doors.get(7).setOpen(true);
    }

    @Override
    public void onEnterInstance(Player player) {
        player.getController().cancelCurrentSkill();

    }

    @Override
    public boolean isEnemy(Player attacker, Player target) {
        if (attacker != target) {
            return true;
        }
        return super.isEnemy(attacker, target);
    }

    @Override
    public boolean onDie(Player player, Creature lastAttacker) {
        if (player.isInDuoFFA()) {
            DFFAService.getInstance().onDead(player, lastAttacker);
        } else {
            PacketSendUtility.broadcastPacket(player,
                new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
            PacketSendUtility.sendPacket(player, new SM_DIE(false, false, 0, 8));
        }
        return true;
    }

    @Override
    public boolean onReviveEvent(Player player) {
        if (player.isInDuoFFA()) {
            onRevive(player);
        }
        return true;
    }

    @Override
    public void onPlayerLogOut(Player player) {
        TeleportService2.moveToBindLocation(player, false);
    }
}
