package org.gepron1x.clans.api.exception;

import org.gepron1x.clans.api.chat.action.Action;

public class NotEnoughMoneyException extends DescribingException {

	private final double price;
	private final double got;

	public NotEnoughMoneyException(Action action, double price, double got) {
		super(action);
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
