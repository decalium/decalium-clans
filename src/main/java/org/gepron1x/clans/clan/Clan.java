package org.gepron1x.clans.clan;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.kyori.adventure.util.Buildable;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.events.clan.ClanAddMemberEvent;
import org.gepron1x.clans.events.clan.ClanRemoveMemberEvent;
import org.gepron1x.clans.events.clan.ClanSetDisplayNameEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.gepron1x.clans.events.clan.ClanStatisticEvent;
import org.gepron1x.clans.statistic.IntStatisticContainer;
import org.gepron1x.clans.statistic.StatisticType;
import org.gepron1x.clans.util.Events;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class Clan implements IntStatisticContainer, Buildable<Clan, ClanBuilder> {
    private final String tag;
    private Component displayName;
    private final ClanMember creator;
    private final Map<UUID, ClanMember> members;
    private final Object2IntMap<StatisticType> stats;


    public Clan(String tag, // specially for builder
         Component displayName,
         ClanMember creator,
         Set<ClanMember> members,
         Object2IntMap<StatisticType> statistics) {
        this.tag = tag;
        this.displayName = displayName;
        this.members = new HashMap<>(members.size() + 1);
        for(ClanMember member : members) this.members.put(member.getUniqueId(), member);
        this.creator = creator;
        this.members.put(creator.getUniqueId(), creator);
        this.stats = statistics;

    }


    public void addMember(@NotNull ClanMember member) {
        Preconditions.checkArgument(!isMember(member), "player is already in a clan");

        if(Events.callCancellableEvent(new ClanAddMemberEvent(this, member)))
            members.put(member.getUniqueId(), member);
    }
    public boolean isMember(@NotNull ClanMember member) {
        return members.containsValue(member);
    }
    public boolean isMember(@NotNull UUID uniqueId) {
        return members.containsKey(uniqueId);
    }
    public boolean isMember(@NotNull OfflinePlayer player) {
        return isMember(player.getUniqueId());
    }
    @Nullable
    public ClanMember getMember(@NotNull UUID uuid) {
        return members.get(uuid);
    }
    @Nullable
    public ClanMember getMember(@NotNull OfflinePlayer player) {
        return getMember(player.getUniqueId());
    }
    public void removeMember(@NotNull UUID uniqueId) {
        Preconditions.checkArgument(isMember(uniqueId), String.format("no member with %s uuid", uniqueId));
       removeMember(members.get(uniqueId));
    }
    public void removeMember(@NotNull ClanMember member) {
        Preconditions.checkArgument(members.containsKey(member.getUniqueId()),
                String.format("user %s is not in a clan", member));
        Preconditions.checkArgument(!member.equals(creator), "cannot remove owner");
        if(Events.callCancellableEvent(new ClanRemoveMemberEvent(this, member)))
        removeMember(member.getUniqueId());
    }
    public void removeMember(@NotNull OfflinePlayer player) {
        removeMember(player.getUniqueId());
    }
    @NotNull
    public Collection<ClanMember> getMembers() {
        return Collections.unmodifiableCollection(members.values());
    }
    @NotNull
    public String getTag() {
        return tag;
    }

    @NotNull
    public Component getDisplayName() {
        return displayName;
    }


    public void setDisplayName(@NotNull Component displayName) {
        ClanSetDisplayNameEvent event = Events.callEvent(new ClanSetDisplayNameEvent(this, displayName));
        if(!event.isCancelled())
            this.displayName = event.getNewDisplayName();
    }


    @Override
    public void setStatistic(StatisticType type, int value) {
        ClanStatisticEvent event = Events.callEvent(new ClanStatisticEvent(this, type, value));
        if(event.isCancelled() || event.getValue().isEmpty()) return;
        stats.put(type, event.getValue().getAsInt());
    }

    @Override
    public boolean hasStatistic(StatisticType type) {
        return stats.containsKey(type);
    }

    @Override
    public OptionalInt getStatistic(StatisticType type) {
        int value = stats.getInt(type);
        return value == stats.defaultReturnValue() ? OptionalInt.empty() : OptionalInt.of(value);
    }

    @Override
    public void removeStatistic(StatisticType type) {
        ClanStatisticEvent event = Events.callEvent(new ClanStatisticEvent(this, type, null));
        if(event.isCancelled()) return;
        event.getValue().ifPresentOrElse(value -> stats.put(type, value), () -> stats.removeInt(type));
        stats.removeInt(type);
    }


    @Override
    public @NotNull ClanBuilder toBuilder() {
        return new ClanBuilder(tag)
                .creator(creator)
                .displayName(displayName)
                .members(members.values())
                .statistics(stats);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clan clan = (Clan) o;
        return tag.equals(clan.tag) &&
                displayName.equals(clan.displayName) &&
                creator.equals(clan.creator) &&
                members.equals(clan.members) &&
                stats.equals(clan.stats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, displayName, creator, members, stats);
    }
}
