package org.windning.arule.exec.model;


import org.windning.arule.bridge.exception.InvalidNativeValueException;
import org.windning.arule.bridge.model.NativeField;

import java.util.ArrayList;

public class MethodInvocation extends Statement {
	private String mMethodName = "";
	private ArrayList<NativeField> mArgs = new ArrayList<NativeField>();
	
	public void setMethodName(String methodName) {
		mMethodName = methodName;
	}
	public void addArgument(NativeField arg) {
		mArgs.add(arg);
	}
	public void addArguments(ArrayList<NativeField> args) {
		mArgs.addAll(args);
	}
	public String getMethodName() {
		return mMethodName;
	}
	public Object[] getArguments() throws InvalidNativeValueException {
		int size = mArgs.size();
		Object[] argList = new Object[size];
		for(int i=0;i<size;i++) {
			argList[i] = mArgs.get(i).getValue();
		}
		return argList;
	}
	
}
