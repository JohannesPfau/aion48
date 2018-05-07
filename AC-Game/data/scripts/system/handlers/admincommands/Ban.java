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

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * @author Watson
 */
public class Ban extends AdminCommand {

    public Ban() {
        super("ban");
    }

    private static final Logger log = LoggerFactory.getLogger("GM_MONITOR_LOG");

    @Override
    public void execute(Player admin, String... params) {
        if (params == null || params.length < 1) {
            PacketSendUtility.sendMessage(admin, "Syntax: //ban <player> [account|ip|full] [time in minutes]");
            return;
        }

        // We need to get player's account ID
        String name = Util.convertName(params[0]);
        int accountId = 0;
        String accountIp = "";

        // First, try to find player in the World
        Player player = World.getInstance().findPlayer(name);
        if (player != null) {
            accountId = player.getClientConnection().getAccount().getId();
            accountIp = player.getClientConnection().getIP();
        }

        // Second, try to get account ID of offline player from database
        if (accountId == 0) {
            accountId = DAOManager.getDAO(PlayerDAO.class).getAccountIdByName(name);
        }

        // Third, fail
        if (accountId == 0) {
            PacketSendUtility.sendMessage(admin, "Player " + name + " was not found!");
            PacketSendUtility.sendMessage(admin, "Syntax: //ban <player> [account|ip|full] [time in minutes]");
            return;
        }

        byte type = 3; // Default: full
        if (params.length > 1) {
            // Smart Matching
            String stype = params[1].toLowerCase();
            if (("account").startsWith(stype)) {
                type = 1;
            } else if (("ip").startsWith(stype)) {
                type = 2;
            } else if (("full").startsWith(stype)) {
                type = 3;
            } else {
                PacketSendUtility.sendMessage(admin, "Syntax: //ban <player> [account|ip|full] [time in minutes]");
                return;
            }
        }

        int time = 0; // Default: infinity
        if (params.length > 2) {
            try {
                time = Integer.parseInt(params[2]);
            } catch (NumberFormatException e) {
                PacketSendUtility.sendMessage(admin, "Syntax: //ban <player> [account|ip|full] [time in minutes]");
                return;
            }
        }
        if (time == 0) {
            time = 60 * 24 * 365 * 10; //pseudo infinity. TODO: rework
        }

        LoginServer.getInstance().sendBanPacket(type, accountId, accountIp, time, admin.getObjectId());
        log.info("[ban] GM : " + admin.getName() + " has banned a player in config [" + type + "] of player [" + name + "] in mapId '"
            + admin.getWorldId() + "'");
    }

    @Override
    public void onFail(Player player, String message) {
        PacketSendUtility.sendMessage(player, "Syntax: //ban <player> [account|ip|full] [time in minutes]");
    }
}
