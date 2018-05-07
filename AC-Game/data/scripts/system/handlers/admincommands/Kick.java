/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * Credits goes to all Open Source Core Developer Groups listed below
 * Please do not change here something, ragarding the developer credits, except the "developed by XXXX".
 * Even if you edit a lot of files in this source, you still have no rights to call it as "your Core".
 * Everybody knows that this Emulator Core was developed by Aion Lightning 
 * @-Aion-Unique-
 * @-Aion-Lightning
 * @Aion-Engine
 * @Aion-Extreme
 * @Aion-NextGen
 * @Aion-Core Dev.
 */
package admincommands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * @author Elusive
 */
public class Kick extends AdminCommand {

    public Kick() {
        super("kick");
    }

    private static final Logger log = LoggerFactory.getLogger("GM_MONITOR_LOG");

    @Override
    public void execute(Player admin, String... params) {
        if (params.length < 1) {
            PacketSendUtility.sendMessage(admin, "syntax //kick <character_name> | <All>");
            return;
        }

        if (params[0] != null && "All".equalsIgnoreCase(params[0])) {
            for (final Player player : World.getInstance().getAllPlayers()) {
                if (!player.isGM()) {
                    player.getClientConnection().close(new SM_QUIT_RESPONSE(), false);
                    PacketSendUtility.sendMessage(admin, "Kicked player : " + player.getName());
                }
            }
        } else {
            Player player = World.getInstance().findPlayer(Util.convertName(params[0]));
            if (player == null) {
                PacketSendUtility.sendMessage(admin, "The specified player is not online.");
                return;
            }
            player.getClientConnection().close(new SM_QUIT_RESPONSE(), false);
            PacketSendUtility.sendMessage(admin, "Kicked player : " + player.getName());
            log.info("[kick] GM : " + admin.getName() + " Kicked Player [" + player.getName() + "] in mapId '" + admin.getWorldId() + "'");
        }

    }

    @Override
    public void onFail(Player player, String message) {
        PacketSendUtility.sendMessage(player, "syntax //kick <character_name> | <All>");
    }
}
