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

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Watson
 */
public class UnBan extends AdminCommand {

    public UnBan() {
        super("unban");
    }

    private static final Logger log = LoggerFactory.getLogger("GM_MONITOR_LOG");

    @Override
    public void execute(Player admin, String... params) {
        if (params == null || params.length < 1) {
            PacketSendUtility.sendMessage(admin, "Syntax: //unban <player> [account|ip|full]");
            return;
        }

        // Banned player must be offline, so get his account ID from database
        String name = Util.convertName(params[0]);
        int accountId = DAOManager.getDAO(PlayerDAO.class).getAccountIdByName(name);
        if (accountId == 0) {
            PacketSendUtility.sendMessage(admin, "Player " + name + " was not found!");
            PacketSendUtility.sendMessage(admin, "Syntax: //unban <player> [account|ip|full]");
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
                PacketSendUtility.sendMessage(admin, "Syntax: //unban <player> [account|ip|full]");
                return;
            }
        }

        // Sends time -1 to unban
        LoginServer.getInstance().sendBanPacket(type, accountId, "", -1, admin.getObjectId());
        log.info("[unban] GM : " + admin.getName() + " has unbanned the in config [" + type + "] of player [" + name + "] in mapId '" + admin.getWorldId() + "'");
    }

    @Override
    public void onFail(Player player, String message) {
        PacketSendUtility.sendMessage(player, "Syntax: //unban <player> [account|ip|full]");
    }
}
