package org.windning.arule.exec;


import org.windning.arule.bridge.BridgeContext;
import org.windning.arule.bridge.model.NativeField;
import org.windning.arule.exec.exception.ExecutionException;
import org.windning.arule.bridge.model.ValueField;
import org.windning.arule.exec.model.ExecuteReturn;
import org.windning.arule.exec.model.MethodInvocation;
import org.windning.arule.exec.model.Statement;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExecutionEngine {
	static private final Pattern MethodInvokePattern = Pattern.compile("^([a-zA-Z0-9$\\.]+)\\(([a-zA-Z0-9,\"\\s$\\.]*)\\)$");
	static public final Pattern ReturnPattern = Pattern.compile("^return[\\s]+([a-zA-Z0-9$]+)$");

	private static String removeSpace(String rawStr) {
		boolean inString = false;
		int length = rawStr.length();
		StringBuilder result = new StringBuilder(length+1);
		for(int i=0;i<length;i++) {
			char c = rawStr.charAt(i);
			if(c == '\"') {
				inString = !inString;
			}
			if(!inString && c == ' ') {
				//skip space
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

	public static ArrayList<Statement> parse(String execCode, BridgeContext context)
			throws ExecutionException {
		String[] stateInsts = execCode.split(";+");
		boolean hasReturnInst = false;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		for(String inst : stateInsts) {
			Matcher methodMatcher = MethodInvokePattern.matcher(inst);
			if(methodMatcher.find() && methodMatcher.groupCount()>=2) {
				MethodInvocation state = new MethodInvocation();
				state.setMethodName(methodMatcher.group(1));
				String rawArgStr = methodMatcher.group(2);
				rawArgStr = removeSpace(rawArgStr);
				String[] args = rawArgStr.split(",");
				state.addArguments(parseArguments(args, context));
				statements.add(state);
				continue;
			}
			Matcher returnMatcher = ReturnPattern.matcher(inst);
			if(returnMatcher.find() && returnMatcher.groupCount()>=1) {
				String value = returnMatcher.group(1);
				statements.add(new ExecuteReturn(Boolean.parseBoolean(value)));
				hasReturnInst = true;
			} else {
				throw new ExecutionException("Syntex Error: Invalid statement \""+inst+"\"");
			}
		}
		if(!hasReturnInst) {
			statements.add(new ExecuteReturn(true));
		}
		
		return statements;
	}
	
	private static ArrayList<NativeField> parseArguments(String[] args, BridgeContext context)
			throws ExecutionException {
		ArrayList<NativeField> fieldArgs = new ArrayList<NativeField>();
		for(String arg : args) {
			NativeField value = ValueField.newInstance(arg);
			if(value == null) { //arg is a reference
				value = context.getField(arg);
			}
			if(value != null) {
				fieldArgs.add(value);
			} else {
				throw new ExecutionException("Syntax Error: Invalid argument \""+ arg + "\".");
			}
		}
		return fieldArgs;
	}

}
