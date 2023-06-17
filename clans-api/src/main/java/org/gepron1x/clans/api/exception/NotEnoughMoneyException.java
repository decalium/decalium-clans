package org.gepron1x.clans.api.exception;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;

public class NotEnoughMoneyException extends DescribingException {

	private final double price;
	private final double got;
	public NotEnoughMoneyException(Component description, double price, double got) {
		super(description);
		this.price = price;
		this.got = got;
	}

	public NotEnoughMoneyException(ComponentLike description, double price, double got) {
		super(description);
		this.price = price;
		this.got = got;
	}

	public double got() {
		return got;
	}

	public double price() {
		return price;
	}
}
