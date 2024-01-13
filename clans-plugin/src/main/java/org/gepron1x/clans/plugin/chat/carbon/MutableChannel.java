package org.gepron1x.clans.plugin.chat.carbon;

import net.draycia.carbon.api.channels.ChannelPermissionResult;
import net.draycia.carbon.api.channels.ChatChannel;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
// no hashCode and equals implementation, this is intentional
public final class MutableChannel implements ChatChannel {


	private ChatChannel delegate;

	public MutableChannel(ChatChannel delegate) {
		this.delegate = delegate;
	}
	@Override
	public ChannelPermissionResult speechPermitted(CarbonPlayer carbonPlayer) {
		return delegate.speechPermitted(carbonPlayer);
	}

	@Override
	public ChannelPermissionResult hearingPermitted(CarbonPlayer player) {
		return delegate.hearingPermitted(player);
	}

	@Override
	public List<Audience> recipients(CarbonPlayer sender) {
		return delegate.recipients(sender);
	}

	@Override
	public @Nullable String quickPrefix() {
		return delegate.quickPrefix();
	}

	@Override
	public boolean shouldRegisterCommands() {
		return delegate.shouldRegisterCommands();
	}

	@Override
	public String commandName() {
		return delegate.commandName();
	}

	@Override
	public List<String> commandAliases() {
		return delegate.commandAliases();
	}

	@Override
	public @MonotonicNonNull String permission() {
		return delegate.permission();
	}

	@Override
	public double radius() {
		return delegate.radius();
	}

	@Override
	public boolean emptyRadiusRecipientsMessage() {
		return delegate.emptyRadiusRecipientsMessage();
	}

	@Override
	public Component render(CarbonPlayer sender, Audience recipient, Component message, Component originalMessage) {
		return delegate.render(sender, recipient, message, originalMessage);
	}

	@Override
	public @NotNull Key key() {
		return delegate.key();
	}

	public void setDelegate(ChatChannel channel) {
		this.delegate = channel;
	}

	@Override
	public String toString() {
		return this.delegate.toString();
	}
}
