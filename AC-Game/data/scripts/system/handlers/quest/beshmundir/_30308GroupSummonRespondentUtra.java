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
package quest.beshmundir;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author Gigi
 */
public class _30308GroupSummonRespondentUtra extends QuestHandler {

    private final static int questId = 30308;

    public _30308GroupSummonRespondentUtra() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799322).addOnQuestStart(questId);
        qe.registerQuestNpc(799322).addOnTalkEvent(questId);
        qe.registerQuestNpc(799506).addOnTalkEvent(questId);
        qe.registerQuestItem(182209710, questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        final Player player = env.getPlayer();

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 799322) {
                if (env.getDialog() == DialogAction.QUEST_SELECT) {
                    if (giveQuestItem(env, 182209710, 1)) {
                        return sendQuestDialog(env, 4762);
                    } else {
                        return true;
                    }
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        }

        if (qs == null) {
            return false;
        }

        if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 799506:
                    switch (env.getDialog()) {
                        case QUEST_SELECT:
                            return sendQuestDialog(env, 1011);
                        case SET_SUCCEED:
                            env.getVisibleObject().getController().onDelete();
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            return true;
                    }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799322) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

    @Override
    public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
        final Player player = env.getPlayer();
        final int id = item.getItemTemplate().getTemplateId();
        final int itemObjId = item.getObjectId();

        if (id != 182209710) {
            return HandlerResult.UNKNOWN;
        }
        if (player.getWorldId() != 300170000) // TODO: Use zone instead of map
        {
            return HandlerResult.UNKNOWN;
        }
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            return HandlerResult.UNKNOWN;
        }

        PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 3000, 0, 0), true);
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0, 1, 0), true);
                player.getInventory().decreaseByObjectId(itemObjId, 1);
                QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 799506, player.getX(), player.getY(), player.getZ(),
                    player.getHeading());
            }
        }, 3000);
        return HandlerResult.SUCCESS;
    }
}
