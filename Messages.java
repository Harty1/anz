package com.anz.currency.convertor.constants;

public enum Messages {
	INVALID_INPUT_LENGTH ("Please enter valid number of input parameters"),
	PROPS_NOT_FOUND ("Currency Exchange properties file not found"),
	CURRENCY_NOT_LISTED ("Could not find the currency you are looking for"),
	USD_BASE ("USD"),
	EURO_BASE ("EUR"),
	CURR_AUD("AUD"),
	CURR_CAD("CAD"),
	CURR_CZK("CZK"),
	CURR_DKK("DKK");
	private final String name;
	
	private Messages(String name) {
		this.name = name;
	}
	
	public boolean equalsName(String otherName) {
		return this.name.equalsIgnoreCase(otherName);
	}
	
	public String toString() {
		return this.name;
	}
}
