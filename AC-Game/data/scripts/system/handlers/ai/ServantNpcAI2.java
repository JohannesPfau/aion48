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
package ai;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.NpcObjectType;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 * @Reworked Kill3r
 */
@AIName("servant")
public class ServantNpcAI2 extends GeneralNpcAI2 {

    private static final Logger log = LoggerFactory.getLogger(ServantNpcAI2.class);

    public void think() {
        // servants are not thinking
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        if (getCreator() != null) {
            ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    if (getOwner().getNpcObjectType() != NpcObjectType.TOTEM) {
                        AI2Actions.targetCreature(ServantNpcAI2.this, (Creature) getCreator().getTarget());
                    }
                    healOrAttack();
                }
            }, 200);
        }
    }

    private void healOrAttack() {
        if (skillId == 0) {
            skillId = getSkillList().getRandomSkill().getSkillId();
        }
        int duration = getOwner().getNpcObjectType() == NpcObjectType.TOTEM ? 3000 : 5000;
        final int delay;
        switch (skillId) {
            case 21954: //battle Banner Effect Skill_Ids
            case 22654:
            case 22655:
                delay = 1000;
                duration = 3000;
                break;
            default:
                delay = 1000;
                break;
        }
        Future<?> task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                getOwner().getController().useSkill(skillId, 1);
            }
        }, delay, duration);
        getOwner().getController().addTask(TaskId.SKILL_USE, task);
    }

    @Override
    public boolean isMoveSupported() {
        return false;
    }

    @Override
    protected AIAnswer pollInstance(AIQuestion question) {
        switch (question) {
            case SHOULD_DECAY:
                return AIAnswers.NEGATIVE;
            case SHOULD_RESPAWN:
                return AIAnswers.NEGATIVE;
            case SHOULD_REWARD:
                return AIAnswers.NEGATIVE;
            default:
                return null;
        }
    }
}
