/*
 * Created on Dec 12, 2003
 *
 */
package org.windning.arule.rule.model;

import org.windning.arule.bridge.BridgeContext;
import org.windning.arule.rule.exception.RuleException;

/**
 * @author cmayor
 *
 * This interface represents an argument to an expression.
 * @see Expression
 */
public abstract class Argument {
	public abstract Constant getValue(BridgeContext context) throws RuleException;

	/**
	 * @param s
	 * @return
	 */
	public static Argument parse(String s) {
		if( isQuoted( s ) )  {
			return new Constant( removeQuotes( s ) );
		} else if( isNumber( s ) ) {
			return new Constant( s );
		} else {
			return new FieldArgument( s ); 
		}
	}

	private static boolean isQuoted(String item) {
		return item.startsWith( "\"" ) && item.endsWith( "\"" );
	}
	
	public static boolean isNumber( String s ) {
		try {
			Double dub = new Double( Double.parseDouble( s ) );
			return true;
		} catch( NumberFormatException nfe ) {
		}		
		
		return false;
	}	
	
	private static String removeQuotes( String s ) {
		return s.replaceAll( "\"", "");
	}	
	
}
