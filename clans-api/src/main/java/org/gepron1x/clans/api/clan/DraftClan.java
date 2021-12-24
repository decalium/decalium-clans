package org.gepron1x.clans.api.clan;

import net.kyori.adventure.util.Buildable;
import org.gepron1x.clans.api.statistic.StatisticHolder;

public interface DraftClan extends StatisticHolder, ClanBase, Buildable<DraftClan, DraftClan.Builder> {

    
    interface Builder extends ClanBase.Builder<Builder, DraftClan> { }

}
