package org.windning.arule.script;

import org.windning.arule.bridge.model.NativeMethod;

import java.util.HashMap;
import java.util.Map;

public class ScriptContext {
	private Map<String, NativeMethod> mMethodMap = new HashMap<String, NativeMethod>();
	
	public void registerInterface(String methodName, NativeMethod method) {
		mMethodMap.put(methodName, method);
	}
	public NativeMethod getInterface(String methodName) {
		return mMethodMap.get(methodName);
	}
}
