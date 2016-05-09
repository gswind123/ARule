package org.windning.arule.bridge.enume;

public enum ValueEnum {
	ValueNull(0, "null");
	
	private int mValue;
	private String mDesc;
	ValueEnum(int val, String desc) {
		mValue = val;
		mDesc = desc;
	}
	
	public int getValue() {
		return mValue;
	}
	public String getDescription() {
		return mDesc;
	}
}
