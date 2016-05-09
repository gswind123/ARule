package org.windning.arule.script;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.windning.arule.bridge.BridgeContext;
import org.windning.arule.bridge.exception.InvalidNativeValueException;
import org.windning.arule.bridge.model.NativeMethod;
import org.windning.arule.exec.ExecutionEngine;
import org.windning.arule.exec.exception.ExecutionException;
import org.windning.arule.exec.model.ExecuteReturn;
import org.windning.arule.exec.model.MethodInvocation;
import org.windning.arule.exec.model.Statement;
import org.windning.arule.rule.Rule;
import org.windning.arule.rule.exception.RuleException;
import org.windning.arule.rule.model.BooleanExpression;
import org.windning.arule.script.exception.ScriptException;
import org.windning.arule.script.model.OperationModel;

import java.util.ArrayList;
import java.util.Iterator;

public class ScriptEngine {
	static private final String KeyOperates = "operations";
	
	static private final String KeyOptName = "name";
	static private final String KeyOptFields = "fields";
	static private final String KeyOptPosition = "position";
	static private final String KeyOptCond = "condition";
	static private final String KeyOptExec = "execution";
	
	static private final String KeyPosBefore = "before";
	static private final String KeyPosAfter = "after";
	
		
	public static ArrayList<OperationModel> parseScript(String script) throws ScriptException {
		try{
			JSONObject scriptObj = new JSONObject(script);
			ArrayList<OperationModel> operates = new ArrayList<OperationModel>();
			JSONArray jsonOperates;
			if(scriptObj == null || (jsonOperates = scriptObj.getJSONArray(KeyOperates)) == null) {
				return operates;
			}
			int optSize = jsonOperates.length();
			for(int i=0;i<optSize;i++) {
				JSONObject jsonOpt = jsonOperates.getJSONObject(i);
				OperationModel optModel = new OperationModel();

				String name = null;
				try{
					jsonOpt.getString(KeyOptName);
				}catch(JSONException e){}
				if((name = jsonOpt.getString(KeyOptName)) != null) {
					optModel.setName(name);
				}
				//fields must be parsed before condition and execution to ensure context
				JSONObject jsonFields = null;
				try{
					jsonFields = jsonOpt.getJSONObject(KeyOptFields);
				}catch(JSONException e){};
				if(jsonFields != null) {
					Iterator<String> keyIter = jsonFields.keys();
					while(keyIter.hasNext()) {
                        String fieldName = keyIter.next();
						String value = jsonFields.getString(fieldName);
						if(value == null) {
							throw new ScriptException("Field " + fieldName + " in " +
									jsonOpt.toString() + " should not be <null>");
						}
						try {
							optModel.setField(fieldName, value);
						} catch (InvalidNativeValueException e) {
							throw new ScriptException(e);
						}
					}
				}

				JSONObject jsonPosition = null;
				try{
					jsonPosition = jsonOpt.getJSONObject(KeyOptPosition);
				}catch(JSONException e){}
				if(jsonPosition != null) {
					String beforePos = null;
					String afterPos = null;
					try{
						beforePos = jsonPosition.getString(KeyPosBefore);
					}catch(JSONException e) {}
					try{
						afterPos = jsonPosition.getString(KeyPosAfter);
					}catch(JSONException e) {}

					optModel.setPosition(beforePos, afterPos);
				}

				String condition = null;
				try{
					condition = jsonOpt.getString(KeyOptCond);
				}catch(JSONException e) {}
				if(condition != null) {
					BooleanExpression condExp;
					try {
						condExp = Rule.parse(optModel.getContext(), condition);
						optModel.setCondition(condExp);
					} catch (RuleException e) {
						e.printStackTrace();
					}
				}

				String execution = null;
				try{
					execution = jsonOpt.getString(KeyOptExec);
				}catch(JSONException e) {}
				if(execution != null) {
					try {
						optModel.setExecution(ExecutionEngine.parse(execution, optModel.getContext()));
					} catch (ExecutionException e) {
						throw new ScriptException(e);
					}
				}

				operates.add(optModel);
			}

			return operates;
		} catch(JSONException e) {
			throw new ScriptException(e);
		}

	}
	
	/**
	 * Run a script model's operation
	 * @param operation
	 * @param runtime
	 * @param nativeOwner
	 * @return execution's return value ; true for default
	 * @throws ScriptException 
	 */
	public static boolean runOperation(OperationModel operation, ScriptContext runtime, Object nativeOwner) 
			throws ScriptException {
		BridgeContext bridgeCxt;
		if(operation == null || (bridgeCxt=operation.getContext()) == null) {
			return true;
		}
		bridgeCxt.setNativeOwner(nativeOwner);
		BooleanExpression condExp = operation.getCondition();
		if(condExp == null) {
			return true;
		}
		boolean cond = false;
		try {
			cond = condExp.evaluate();
		} catch (RuleException e) {
			throw new ScriptException(e);
		}
		if(cond) {
			ArrayList<Statement> statements = operation.getExecution();
			for(Statement statement : statements) {
				if(statement instanceof ExecuteReturn) {
					return ((ExecuteReturn) statement).result;
				} else if(statement instanceof MethodInvocation) {
					MethodInvocation invocation = (MethodInvocation) statement;
					NativeMethod method = runtime.getInterface(invocation.getMethodName());
					if(method != null) {
						try {
							method.execute(invocation.getArguments());
						} catch (InvalidNativeValueException e) {
							throw new ScriptException(e);
						}
					}
				}
			}
		}
		return true;
		
	}
}
