package org.gepron1x.clans;

public final class Permissions {
    private Permissions() {
        throw new UnsupportedOperationException();
    }
    public static final String SEPARATOR = ".";
    public static final String PREFIX = "clans";
    private static String clan(String... value) {
        return permission(PREFIX, permission(value));
    }
    private static String permission(String... args) {
        return String.join(SEPARATOR, args);
    }
    public static final String CREATE = clan("create");
    public static final String DELETE = clan("delete");
    public static final String FORCE_DELETE = clan("admin", "delete");
    public static final String LIST = clan("list");
    public static final String SET_DISPLAY_NAME = clan("set", "displayname");
    public static final String INVITE = clan("invite");
    public static final String MEMBER_SET_ROLE = clan("member", "set", "role");
    public static final String MEMBER_KICK = clan("member", "kick");
    public static final String CREATE_HOME = clan("home", "create");

}
