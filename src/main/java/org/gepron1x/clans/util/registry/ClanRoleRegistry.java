package org.gepron1x.clans.util.registry;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import org.gepron1x.clans.clan.member.role.ClanRole;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;

public class ClanRoleRegistry extends HashRegistry<String, ClanRole> {

    private final ClanRole defaultRole;
    private final ClanRole ownerRole;

    private ClanRoleRegistry(int initialCapacity, @NotNull ClanRole defaultRole, @NotNull ClanRole ownerRole) {
        super(ClanRole::getName, initialCapacity);

        this.defaultRole = defaultRole;
        this.ownerRole = ownerRole;
    }

    public static ClanRoleRegistry create(ClanRole defaultRole, ClanRole ownerRole, Collection<ClanRole> others) {
        HashSet<ClanRole> roles = new HashSet<>(others);
        roles.add(defaultRole);
        roles.add(ownerRole);
        ClanRoleRegistry registry = new ClanRoleRegistry(others.size(), defaultRole, ownerRole);
        registry.addAll(roles);
        return registry;
    }
    public static ClanRoleRegistry create(ClanRole defaultRole, ClanRole ownerRole, ClanRole... others) {
        return create(defaultRole, ownerRole, Arrays.asList(others));
    }

    @NotNull
    public ClanRole getDefaultRole() {
        return defaultRole;
    }
    @NotNull
    public ClanRole getOwnerRole() {
        return ownerRole;
    }

    @Override
    public void clear() {
        backingMap.entrySet().removeIf(entry -> {
            ClanRole value = entry.getValue();
            return !(value.equals(defaultRole) || value.equals(ownerRole));
        });
    }
    @Override
    public boolean remove(@NotNull String key) {
        Preconditions.checkArgument(!(defaultRole.getName().equals(key) || ownerRole.getName().equals(key)), "cannot remove" +
                "default/owner role");
        return super.remove(key);
    }

}
