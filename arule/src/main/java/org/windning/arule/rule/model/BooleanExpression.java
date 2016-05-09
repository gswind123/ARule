/*
 * Created on Dec 8, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.windning.arule.rule.model;


import org.windning.arule.bridge.BridgeContext;
import org.windning.arule.rule.exception.RuleException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author cmayor
 *
 */
public class BooleanExpression extends Expression {
	
	public static final String AND = "AND";
	public static final String OR = "OR";
	public static final String XOR = "XOR";
	
	private BridgeContext mContext;
	
	private List<Object> operands;
	
	public BooleanExpression(BridgeContext context) {
		operands = new LinkedList<Object>();
	}
	
	public BooleanExpression(BridgeContext context, Expression e) {
		this(context);
		operands.add( e );
	}
	
	public void addOperand( Expression e, String o  ) {
		operands.add( o );
		operands.add( e );
	}
	
	public boolean hasOperand() {
	    return (operands.size() != 0);
	}
	
	public boolean evaluate() throws RuleException {
		
		boolean lhs = false;
		boolean rhs = false;
		String operator = "";
		boolean retVal = false;
		
		
		Iterator<Object> i = operands.iterator();
		
		if( i.hasNext() ) {
			Expression e = (Expression) i.next();
			lhs = e.evaluate();
		}

				
		while( i.hasNext() ) {

			// Get the next operator.  If we are out of operators then
			// return the calculated value.
			if( i.hasNext() ) {
				operator = (String)i.next();
			} else {
				return lhs;
			}
			
			if( i.hasNext() ) {
				Expression e = (Expression)i.next();
				rhs = e.evaluate();
			} else {
				throw new RuleException( "Operator not followed by an operand");
			}
			
			lhs = evaluate( lhs, operator, rhs );
			
		}
		
		/*
		if (lhs)
		    System.out.println("Success boolean exp = " + this.toString());
		else
		    System.out.println("FAILED boolean exp = " + this.toString());
		*/
		return lhs;
	}
	
	/**
	 * Calculates the result of a binary boolean expression.  Supported
	 * operators are AND, OR, and XOR.
	 * 
	 * @param lhs Left hand side of the expression 
	 * @param operator Operator of the expression - can be AND, OR, XOR
	 * @param rhs Right hand side of the expression
	 * @return The result of the expression: lhs operator rhs
	 */
	private boolean evaluate(boolean lhs, String operator, boolean rhs)
	throws RuleException {

		if( operator.equalsIgnoreCase( "AND") ) {
			return lhs && rhs;
		} else if( operator.equalsIgnoreCase( "OR") ) {
			return lhs || rhs;
		} else if( operator.equalsIgnoreCase( "XOR" ) ) {
			return lhs ^ rhs;
		} else if ( operator.length() == 0) {
		    return lhs;
		} else throw new RuleException( "Unrecognized operator: " + operator );
		
	}

	public String toHQL() {
		return "";
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer(":::> ");
		Iterator<Object> i = operands.iterator();
		while( i.hasNext() ) {
			sb.append( i.next() + " " );
		}
		sb.append("<:::");
		return sb.toString();
		
	}
	
}
