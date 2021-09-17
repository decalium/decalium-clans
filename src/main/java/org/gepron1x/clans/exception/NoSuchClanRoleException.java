package org.gepron1x.clans.exception;

import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.captions.StandardCaptionKeys;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.ParserException;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NoSuchClanRoleException extends ParserException {

    private final String roleName;

    public NoSuchClanRoleException(@NonNull Class<?> argumentParser, @NonNull CommandContext<?> context, String roleName) {
        super(argumentParser, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_NO_INPUT_PROVIDED);
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
