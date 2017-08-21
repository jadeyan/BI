package com.teamsun.bi.http.parser;

import java.util.Hashtable;


public class Tag {
	
    private static final Hashtable<String, Integer> SYMBOL_TABLE = new Hashtable<String, Integer>();
    
    /**
     * A variable that after initialization will contain the number of different
     * tags the Tag class can keep track of.
     */
    private static int             nSymbols;
    
    /**
     * The initial size of the symbol storage.
     */
    private static final int       INITIAL_SIZE = 10;
    
    /**
     * The increment that the symbol storage will grow by when needed.
     */
    private static final int       INCREMENT    = 10;
    
    
    public static final int       chart          = 0; //tag  chart
    
    public static final int       metricProperty = 1; //tag  metricProperty
    
    public static final int       metric         = 2;//TAG metric 
    
    public static final int       org            = 3;//TAG org
    
    public static final int       set            = 4; //TAG set
    
    public static final int       valueRange     = 5;//TAG valueRange
    
    public static final int       range          = 6;//TAG range 
    
    public static final int       curret         = 7;//TAG curret
    
    public static final int       cursor         = 8;//TAG cursor
    
    public static final int       categories     = 9;//categories
    
    public static final int       category       = 10;//TAG category
    
    public static final int       dataset        = 11;//TAG dataset 
    
    public static final int       vertline       = 12;//TAG vertline
    
    public static final int       message        = 13;//TAG message
    
    public static final int       error          = 14;//TAG
    
    public static final int       line           = 15;//TAG line
    
    public static final int       eof            = 16;//end of tag
    
    
    
    /**
     * The symbol storage.
     */
    private static String[]        symbols      = new String[INITIAL_SIZE];
    
    public static String getSymbol(int i){
    	return symbols[i];
    }
    
    static{
    	define("chart");
    	define("metricProperty");
    	define("metric");
    	define("org");
    	define("set");
    	define("valueRange");
    	define("range");
    	define("curret");
    	define("cursor");
    	define("categories");
    	define("category");
    	define("dataset");
    	define("vertline");
    	define("message");
    	define("error");
    	define("line");
    	define("eof");
    }
    
    /**
     * Define a tag symbol and map it to an index.
     *
     * @param argSymbol
     *            the symbol being defined
     * @return the integer representing the string.
     */
    public static synchronized int define(final String argSymbol) {
        final String symbol = argSymbol.toLowerCase();
        final Object val = SYMBOL_TABLE.get(symbol);
        if (val != null) {
            return (((Integer) val).intValue());
        }
        SYMBOL_TABLE.put(symbol, new Integer(nSymbols));
        ensureCapacity();
        symbols[nSymbols] = symbol;
        //#debug debug
//#         //logger.write(IMTLogger.DEBUG, "Tag.define: symbol(" + nSymbols + ")=" + symbol(nSymbols));

        return (nSymbols++);
    }
    
    /**
     * Ensure that the "symbols" array is big enough. If it isn't then allocate
     * a new array and copy the content of the old one into the new one, then
     * replace the old one with the new one.
     */
    private static void ensureCapacity() {
        if (symbols.length <= (nSymbols)) {
            final String[] newArray = new String[symbols.length + INCREMENT];
            System.arraycopy(symbols, 0, newArray, 0, symbols.length);
            symbols = newArray;
        }
    }


}
