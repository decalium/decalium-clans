package org.gepron1x.clans.plugin.war.announce.bossbar;

import net.kyori.adventure.bossbar.BossBar;
import org.gepron1x.clans.plugin.war.War;
import org.gepron1x.clans.plugin.war.announce.WarAudience;
import org.jetbrains.annotations.NotNull;

public final class BossBarListener implements BossBar.Listener {

    private final War war;

    public BossBarListener(War war) {

        this.war = war;
    }

    @Override
    public void bossBarProgressChanged(@NotNull BossBar bar, float oldProgress, float newProgress) {
        if(newProgress == 0) new WarAudience(war).hideBossBar(bar);
    }
}
