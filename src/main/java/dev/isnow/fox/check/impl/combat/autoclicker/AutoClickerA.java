

package dev.isnow.fox.check.impl.combat.autoclicker;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "AutoClicker", type = "A", description = "Detects high amounts of clicks in a second.")
public final class AutoClickerA extends Check {
    public AutoClickerA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isArmAnimation()) {
            final double cps = data.getClickProcessor().getCps();

            final boolean exempt = isExempt(ExemptType.DROP, ExemptType.AUTOCLICKER);
            final boolean invalid = cps > 60 && !Double.isInfinite(cps) && !Double.isNaN(cps);

            if (invalid && !exempt) {
                fail("CPS=" + cps);
            }
        }
    }
}
