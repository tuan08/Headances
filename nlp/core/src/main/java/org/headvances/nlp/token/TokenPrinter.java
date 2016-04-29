package org.headvances.nlp.token;

import java.io.PrintStream;
/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class TokenPrinter {
	abstract public void print(PrintStream out, IToken[] token) ;
}
