/*
 * Created on Dec 8, 2003
 *
 */
package org.windning.arule.rule.model;

import org.windning.arule.rule.exception.RuleException;


/**
 * @author cmayor
 *
 */
public class Expression {
	
	protected Expression(){}

	public boolean evaluate() throws RuleException, RuleException {
		return false;
	}
	
	public String toHQL() {
		return "";
	}
	
	public static Expression parse( String s ) {
		
		return null;
	}
	
	
	
}
