package org.gepron1x.clans.clan;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.Buildable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.gepron1x.clans.clan.home.ClanHome;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.event.clan.ClanStatisticEvent;
import org.gepron1x.clans.event.member.ClanAddMemberEvent;
import org.gepron1x.clans.event.member.ClanRemoveMemberEvent;
import org.gepron1x.clans.statistic.IntStatisticContainer;
import org.gepron1x.clans.statistic.StatisticType;
import org.gepron1x.clans.storage.property.DefaultProperty;
import org.gepron1x.clans.storage.property.Property;
import org.gepron1x.clans.util.CollectionUtils;
import org.gepron1x.clans.util.Events;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class Clan implements IntStatisticContainer, Buildable<Clan, ClanBuilder> {

    public static Property<Clan, Component> DISPLAY_NAME = new DefaultProperty<>("display_name",
            Clan.class,
            Component.class,
            clan -> clan.displayName,
            ((clan, component) -> clan.displayName = component));


    private final String tag;
    private Component displayName;
    private final ClanMember creator;
    private final Map<UUID, ClanMember> members;
    private final Map<String, ClanHome> homes;
    private final Object2IntMap<StatisticType> stats;


    public Clan(String tag, // specially for builder
         Component displayName,
         ClanMember creator,
         Set<ClanMember> members,
         Map<StatisticType, Integer> statistics, Set<ClanHome> homes) {
        this.tag = tag;
        this.displayName = displayName;

        this.members = CollectionUtils.toMap(ClanMember::getUniqueId, members);
        this.homes = CollectionUtils.toMap(ClanHome::getName, homes);

        this.creator = creator;
        this.members.put(creator.getUniqueId(), creator);
        this.stats = new Object2IntArrayMap<>(statistics);

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
        if(!Events.callCancellableEvent(new ClanRemoveMemberEvent(this, member))) return;
        removeMember(member.getUniqueId());
    }
    public void removeMember(@NotNull OfflinePlayer player) {
        removeMember(player.getUniqueId());
    }

    @NotNull
    public Collection<ClanMember> getMembers() {
        return Collections.unmodifiableCollection(members.values());
    }

    public void addHome(ClanHome home) {
        Preconditions.checkArgument(!homes.containsValue(home), "home is already added");
        homes.put(home.getName(), home);
    }
    public void removeHome(String name) {
        Preconditions.checkArgument(homes.containsKey(name), "no home with name "+name);
        homes.remove(name);
    }

    @Nullable public ClanHome getHome(String name) {
        return homes.get(name);
    }
    @NotNull
    public Collection<ClanHome> getHomes() {
        return Collections.unmodifiableCollection(homes.values());
    }
    public boolean hasHome(String name) {
        return homes.containsKey(name);
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
        DISPLAY_NAME.set(this, displayName);
    }
    public ClanMember getCreator() {
        return creator;
    }

    //makes life easier :)
    @NotNull
    public Set<Player> getOnlineMembers() {
        Set<Player> players = new HashSet<>();
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(isMember(player)) players.add(player);
        }
        return players;
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
                .members(getMembers())
                .homes(getHomes())
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
                homes.equals(clan.homes) &&
                stats.equals(clan.stats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, displayName, creator, members, homes, stats);
    }
}
