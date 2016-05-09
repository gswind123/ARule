/*
 * Created on Dec 8, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.windning.arule.rule;


import org.windning.arule.bridge.BridgeContext;
import org.windning.arule.rule.exception.RuleException;
import org.windning.arule.rule.model.BasicExpression;
import org.windning.arule.rule.model.BooleanExpression;
import org.windning.arule.rule.model.Expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * @author cmayor
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Rule {
	
	public static BooleanExpression parse(BridgeContext context, String s) throws RuleException {
		BooleanExpression be = null;
		
		ArrayList<String> tokens = getTokens(s);
		//crude check for parenthesis match
		int openparen = 0;
		int closeparen = 0;
		for (int j = 0 ; j < tokens.size(); j++) {
		    String tok = (String)tokens.get(j);
		    if (tok.equals("("))
		        openparen++;
		    if (tok.equals(")"))
		        closeparen++;
		}
		if (openparen != closeparen)
		    throw new RuleException("Malformed rule, parenthesis count mismatch");
		Iterator<String> i = tokens.iterator();
        return parse(context, i);
	}
	
    protected static BooleanExpression parse(BridgeContext context,  Iterator<String> i ) throws RuleException {
		BooleanExpression bexp = null;
        BooleanExpression be = null;
		Expression lhs = null;
		Expression rhs = null;
		BasicExpression basic = null;
		String operator = null;
		while( i.hasNext() ) {
			String item = i.next();
			if ( item.equals( "(" ) ) {
                bexp  = parse(context, i);
                if (be == null) {
                    be = new BooleanExpression(context, bexp);

                } else if (!be.hasOperand()) {
                }else {
                     if (operator == null)
                        throw new RuleException("Malformed rule");
                     be.addOperand(bexp, operator);
                }
			} else if( item.equals( ")" ) ){
				return be;
			} else if( isQuoted( item ) ) {
				//System.out.println( "Found quoted: "+item);
			} else if( isBoolean( item ) ) {
				//System.out.println( "Found boolean: "+item);
				operator = item;
			} else if( isOperator( item ) ) {
				// Operator
				//System.out.println( "Found operator: "+item);
				
			} else {
                basic = BasicExpression.parse(context, i, item);
                if (be == null) {
                    be = new BooleanExpression(context, basic);
                } else {
                    if (!be.hasOperand()) {
                        be = new BooleanExpression(context, basic);
                    } else {
                        be.addOperand(basic, operator);
                    }
                }
			}
		}
				
		return be;
    }
        
	/**
	 * @param item
	 * @return
	 */
	private static boolean isOperator(String item) {
		return item.equals( ">" ) || item.equals( ">=" ) || item.equals( "<" ) || item.equals( "<=" ) || item.equals( "==" ) || item.equals( "ends" ) || item.equals( "begins" ) || item.equals( "contains" );
	}

	/**
	 * @param item
	 * @return
	 */
	private static boolean isBoolean(String item) {
		return item.equals( "AND" ) || item.equals( "OR" );
	}

	/**
	 * @param item
	 * @return
	 */
	private static boolean isQuoted(String item) {
		return item.startsWith( "\"" ) && item.endsWith( "\"" );
	}

	public static ArrayList<String> getTokens( String s ) {
		StringTokenizer st = new StringTokenizer( s, "!()<>= \t\n\r\f\"", true );
		ArrayList<String> al = new ArrayList<String>();
		
		String ntoken = null;
		while( st.hasMoreTokens() || ntoken != null ) {
			String token = null;
			if (ntoken != null) {
				token = ntoken;
				ntoken = null;
		    }else {
			    token = st.nextToken();
			}
			String t = token;
			if( token.equals( "\"" ) ) {
				t = token; 
				while( st.hasMoreTokens() ) {
					token = st.nextToken();
					t = t + token;
					if( token.equals( "\"" ) )
						break;
				}
			}
			token = t;
			
			if (token.equals("<") || token.equals(">") || token.equals("=") || token.equals("!")) {
				if (st.hasMoreTokens()) {
				    ntoken = st.nextToken();
				    if (ntoken.equals("=")) {
				        token = token + ntoken;
				        ntoken = null;
				    } 
				}
			}
			
			if( token.trim().length() != 0 )
				al.add( token );
		}
		
		
		
		return al;
		
	}
	
	public static boolean isToken( String s ) {
		HashMap<String, String> tokens = new HashMap<String, String>();
		tokens.put( "(", "(" );
		tokens.put( ")", ")" );
		tokens.put( ">", ">" );
		tokens.put( "<", "<" );
		tokens.put( "<=", "<=" );
		tokens.put( ">=", ">=" );
		tokens.put( "==", "==" );
		tokens.put( "<>", "<>" );
		
		return tokens.get( s ) != null;
	}
	
	public static boolean isWhiteSpace( String s ) {
		return s.trim().length() == 0;
	}
	
}
