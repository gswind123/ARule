package org.windning.arule.script.exception;

public class ScriptException extends Exception{
	public ScriptException(String desc) {
		super(desc);
	}
	public ScriptException(Exception e) {
		super(e);
	}
}
