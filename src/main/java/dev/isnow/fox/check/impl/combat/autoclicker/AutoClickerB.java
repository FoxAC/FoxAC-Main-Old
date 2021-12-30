package dev.isnow.fox.check.impl.combat.autoclicker;


import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "AutoClicker", type = "B", description = "Checks the outliers on your clicks")
public class AutoClickerB extends Check {
    private int outliers;
    private int lastoutlier;
    private float threshold;
    private int flying;

    public AutoClickerB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isArmAnimation() && !isExempt(ExemptType.AUTOCLICKER) && flying != 0) {

            if (flying > 3 && flying < 10) {
                outliers++;
            }

            int currentOutliers = outliers;
            int diff = Math.abs(currentOutliers - lastoutlier);
            if (currentOutliers == 0 && diff == currentOutliers) {
                if (threshold > 32) {
                    fail("diff=" + diff + ", threshold=" + threshold + ", cps=" + data.getClickProcessor().getCps());
                }
                threshold += 0.10f;
            } else {
                threshold = 0;
            }
            lastoutlier = currentOutliers;
            outliers = flying = 0;
        } else if (packet.isFlying()) {
            flying++;
        }
    }
}
