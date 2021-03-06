package org.windning.arule.script.model;


import org.windning.arule.bridge.BridgeContext;
import org.windning.arule.bridge.exception.InvalidNativeValueException;
import org.windning.arule.exec.model.Statement;
import org.windning.arule.rule.model.BooleanExpression;

import java.util.ArrayList;

public class OperationModel implements Cloneable{
	private String mName = "";
	private BridgeContext mContext = new BridgeContext();
	private BooleanExpression mCondition = null;
	private ArrayList<Statement> mExecution = null;
	private String mPosBefore = "";
	private String mPosAfter = "";
	
	
	public OperationModel(){}
	
	public String getName() {
		return mName;
	}
	public BooleanExpression getCondition() {
		return mCondition;
	}
	public ArrayList<Statement> getExecution() {
		return mExecution;
	}
	public BridgeContext getContext() {
		return mContext;
	}
	public String getPosBefore() {
		return mPosBefore;
	}
	public String getPosAfter() {
		return mPosAfter;
	}
	
	public void setName(String name) {
		this.mName = name;
	}
	public void setCondition(BooleanExpression condition) {
		this.mCondition = condition;
	}
	public void setExecution(ArrayList<Statement> opt) {
		this.mExecution = opt;
	}
	public void setField(String name, String valueStr) throws InvalidNativeValueException {
		mContext.setField(name, valueStr);
	}
	public void setPosition(String posBefore, String posAfter) {
		this.mPosBefore = posBefore;
		this.mPosAfter = posAfter;
	}
}
