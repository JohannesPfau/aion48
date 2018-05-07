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

 * @-Aion-Lightning
 * @Goong_ADM

 

 */
package com.aionemu.gameserver.skillengine.condition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.TransformType;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FormCondition")
public class FormCondition extends Condition {

    @XmlAttribute(required = true)
    protected TransformType value;

    /*
     * (non-Javadoc)
     * @see com.aionemu.gameserver.skillengine.condition.Condition#validate(com.aionemu.gameserver.skillengine.model.Skill)
     * defines tranformtype in which player can cast given skill
     */
    @Override
    public boolean validate(Skill env) {
        if (env.getEffector() instanceof Player) {
            if (env.getEffector().getTransformModel().isActive() && env.getEffector().getTransformModel().getType() == value) {
                return true;
            } else {
                PacketSendUtility.sendPacket((Player) env.getEffector(), SM_SYSTEM_MESSAGE.STR_SKILL_CAN_NOT_CAST_IN_THIS_FORM);
                return false;
            }
        } else {
            return true;
        }
    }
}
