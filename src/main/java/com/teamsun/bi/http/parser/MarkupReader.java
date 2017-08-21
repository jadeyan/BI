package com.teamsun.bi.http.parser;

/*
 * MarkupReader.java
 *
 * Confidential and proprietary.
 */

//package .........BlackBerryProject.cmcc_blackberry.src;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import android.util.Log;

/**
 * 
 */
public class MarkupReader {
    
    /**
     * The End Of File character constant.
     */
    private static final char EOF = (char) -1;
    
    /**
     * The ASCII encoding for the space character.
     */
    private static final int ASCII_ENCODING_OF_SPACE_CHAR = 32;
    
    /**
     * A reader, reading UTF8 encoded input..
     */
    private Reader reader;
    
    /**
     * The last character read by this reader.
     */
    private char lastCharacterRead;

    /**
     * Text used during parsing. Initially null.
     */
    private StringBuffer textBuffer;
    
    /**
     * The last Token read. NEW 071031 LC
     */
    private MarkupToken currentToken;

    
    private int readLen;
    
    private MarkupToken implicitNextTag;
    
    public MarkupReader(final InputStream inputstream, String encoding){   
        try {
                        reader = new InputStreamReader(inputstream, encoding);
                } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                }
    }
    
    public MarkupToken readToken() throws IOException {
        if (implicitNextTag != null) {
            currentToken = implicitNextTag;
            implicitNextTag = null;
            return (currentToken);
        }
        readLen = 0;
        while (true) {
            read();
            S:
            switch (lastCharacterRead) {
                case EOF:
                  currentToken = null;
                    currentToken = createToken(MarkupToken.END_OF_FILE, "EOF", null);
                    return (currentToken);
                case '<':
                    read();
                    switch (lastCharacterRead) {
                        case '/':
                         currentToken = null;
                            currentToken = parseEndTag();
                            return (currentToken);
                        case '?':
                            currentToken = null;
                            currentToken = parseProcessingInstruction();
                            return (currentToken);
                        case '!': {
                            read();
                            switch (lastCharacterRead) {
                                case '[': {
                                    appendCDATA();
                                    break S;
                                }
                                default:
                                    final MarkupToken decl = parseDeclarationOrComment();
                                    if (decl != null) {
                                        currentToken = decl;
                                        return (decl);
                                    }
                                    break S;
                            }
                        }
                        default: {
                            if (!isNameStart(lastCharacterRead)) {
                                textBuffer().append('<');
                            } else {
                                 currentToken = null;
                                currentToken = parseStartOrEmptyElementTag();
                                return (currentToken);
                            }
                        }
                    }
                default: {
                    textBuffer().append(lastCharacterRead);
                    
                }
            }
        }
    }
    
    /**
     * @return A start tag, or an empty element tag.
     * @throws IMTException when reading fails
     * @see http://www.w3.org/TR/xml/#NT-EmptyElemTag
     */
    private MarkupToken parseStartOrEmptyElementTag() throws IOException {
        final String name = readName();
        Hashtable attributes = null;
        final int type = MarkupToken.START_TAG; // Default
        boolean emptyElementTag = false;

        READ_ATTRIBUTES:
        while (true) {
            skipSpaces();
            if (lastCharacterRead == '/') {
                emptyElementTag = true;
                skipUntil('>');
                break READ_ATTRIBUTES;
            }
            if (lastCharacterRead == '>') {
                break READ_ATTRIBUTES;
            }
            // Read Attribute Name=Value
            final String attrName = readName();
            skipSpaces();
            String attrValue = "";
            // check('=');
            if (lastCharacterRead == '=') {
                read();
                skipSpaces();
                attrValue = readAttributeValue();
            }
            // Update Attribute List
            if (attributes == null) {
                attributes = new Hashtable();
            }
            attributes.put(attrName, attrValue);
        }

        final MarkupToken token = createToken(type, name, attributes);
        if (emptyElementTag) {
            implicitNextTag = createToken(MarkupToken.END_TAG, name, null);
        }
        return (token);
    }
    
    /**
     * @return an attribute value
     * @throws IMTException if reading fails
     * @see http://www.w3.org/TR/xml/#NT-AttValue
     */
    private String readAttributeValue() throws IOException {
        final char quoteCharacter = lastCharacterRead;

        if ((quoteCharacter != '\'') && (quoteCharacter != '"')) {
            return (readUnquotedAttributeValue());
        }
        StringBuffer value = new StringBuffer();
        read();
        while (lastCharacterRead != quoteCharacter) {
            value.append(lastCharacterRead);
            read();
        }
        read(); 
        value = unescape(value);
        return (value.toString());

    }
    
    /**
     * Substitute entity references by special characters.
     * @param str
     *            The input StringBuffer
     * @return StringBuffer with all entity references substituted by special
     *         characters
     */
    private StringBuffer unescape(final StringBuffer str) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char lastCharacterRead = str.charAt(i);
            if (lastCharacterRead == '&') {
              try {
                if (str.charAt(i + 1) == '#') {
                  String number = readUntilSemic(str, i + 2);
                  int c = (number.charAt(0) == 'x' ? Integer.parseInt(
                          number.substring(1), 16) : Integer
                          .parseInt(number));
                  result.append((char) c);
                  i = i + number.length() + 2; // #number;
                } else {
                  String entity = readUntilSemic(str, i + 1);
                  String match = matchEntity(entity);
                  if (match == null) {
                      result.append(lastCharacterRead);
                  } else {
                    result.append(match);
                    i = i + entity.length() + 1; // entity;
                  }
                }
              } catch (Exception e) {
                result.append(lastCharacterRead);
              }
            } else {
              result.append(lastCharacterRead);
            }
        }
        return result;
    }
    
    private static String matchEntity(final String entity) {
        String match = (String) ENTITY_MAP.get(entity);
        return (match);
    }
    
    /**
     * A hash table representing a map from entity names, as used in SGML/XML
     * files, to the strings the entities represent. For instance the entity
     * "&amp;" represents the string "&".
     */
    private static final Hashtable ENTITY_MAP = new Hashtable();
    static {
        // SEE:
        // http://mindprod.com/jgloss/htmlcheat.html#SPECIALCHARACTERS
        addEntityDeclaration("amp", '&');
        addEntityDeclaration("apos", '\'');
        addEntityDeclaration("bull", '?');
        addEntityDeclaration("gt", '>');
        addEntityDeclaration("lt", '<');
        addEntityDeclaration("quot", '"');
        addEntityDeclaration("laquo", '?');
        addEntityDeclaration("raquo", '?'); // ==> ASCII 187
        addEntityDeclaration("nbsp", ' ');

        addEntityDeclaration("AElig", '?');
        addEntityDeclaration("aelig", '?');
        addEntityDeclaration("Oslash", '?');
        addEntityDeclaration("oslash", '?');
        addEntityDeclaration("Aring", '?');
        addEntityDeclaration("aring", '?');

        addEntityDeclaration("copy", '?');
        addEntityDeclaration("reg", '?');
        addEntityDeclaration("trade", '?');
    }
    
    public static void addEntityDeclaration(final String entity, final char c) {
        ENTITY_MAP.put(entity, "" + c);
    }
    
    /**
     * Read string until a semicolon is found, starting at position "i". Return
     * the string between position "i" and the semicolon. i.e.
     * readUntilSemic("fno000;rd", 3) will return "000";
     *
     * @param str the string to parse.
     * @param i The position within to string to start the parsing process at.
     * @return the string between position "i" and the terminating semicolon.
     */
    private static String readUntilSemic(final StringBuffer str, final int i) {
        StringBuffer res = new StringBuffer();
        char c;
        int j = i;
        while ((c = str.charAt(j++)) != ';') {
            res.append(c);
        }
        final String number = res.toString();
        return (number);
    }
    
    private String readUnquotedAttributeValue() throws IOException {
        StringBuffer value = new StringBuffer();
        while ((lastCharacterRead != ' ') && (lastCharacterRead != '>')) {
            value.append(lastCharacterRead);
            read();
        }
        value = unescape(value);
        //#debug debug
        //logger.write(IMTLogger.DEBUG, "MarkupReader.readUnquotedAttributeValue: " + value);

        return (value.toString());

    }
    
    /**
     * Get the text buffer. If empty then create a new one.
     *
     * @return the text buffer.
     */
    private StringBuffer textBuffer() {
        if (textBuffer == null) {
            textBuffer = new StringBuffer();
        }
        return (textBuffer);
    }
    
    /**
     * Assume that lastCharacterRead == firstcharacter of name or - in the event
     * of comment.
     *
     * @return
     * @throws IOException 
     */
    private MarkupToken parseDeclarationOrComment() throws IOException { 
    
        MarkupToken token = null;
        if (lastCharacterRead == '-') {
            read();
            check('-');
            boolean done = false;
            while (!done) {
                skipUntil('-');
                read();
                if (lastCharacterRead == '-') {
                    read();
                    if (lastCharacterRead == '>') {
                        done = true;
                    }
                }
            }
        } else {
            final String name = readName();
            final StringBuffer text = new StringBuffer();
            readUntil(text, '>');
            token = createToken(MarkupToken.DECLARATION, name, null);
        }

        return (token);
    }
    
    public static boolean equalsIgnoreCase(String s1, String s2) {
         if (s1.length() != s2.length())
                        return false;

         return s1.toLowerCase().compareTo(s2.toLowerCase()) == 0;
      }
    
    /**
     * Read CDATA
     * is read
     *
     * @throws IMTException if something goes wrong.
     */
    private void appendCDATA() throws IOException {

        read();
        if (isNameChar(lastCharacterRead)) {
            final String name = readName();
            if (equalsIgnoreCase(name, "CDATA")) {
                //if (name.equalsIgnoreCase("CDATA")) {
                check('[');
                readUntil(textBuffer(), ']');
            }
        }
        skipUntil('>');
    }
    
    /**
     * Skip content until a particular character is encountered.
     *
     * @param c the character to skip to.
     * @throws IMTException when reading fails
     */
    private void skipUntil(final char c) throws IOException {
        while (read() != c) {
        }
    }
    
    /**
     * Parse a processing instruction, using the syntax:
     * @return a processing instrudtion.
     * @throws IMTException when reading fails
     */
    private MarkupToken parseProcessingInstruction() throws IOException {

        // Read first char of name
        read();
        final String name = readName();

        final StringBuffer text = new StringBuffer();
        readUntil(text, '?');
       
        read();
        check('>');
       
        final MarkupToken token = createToken(MarkupToken.INSTRUCTION, name, null);
        return token; 
    }
    
    /**
     * @return a parsed end tag
     * @throws IOException when reading fails.
     */
    private MarkupToken parseEndTag() throws IOException {

        // Frist read char of name
        read();
        final String name = readName();
        skipSpaces();
        check('>');

        final MarkupToken token = createToken(MarkupToken.END_TAG, name, null);
        return token;
    }
    
    /**
     * Read characters from the input, appending to a string buffer until a
     * terminating character is read.
     *
     * @param buff the buffer to read input into.
     * @param c    The terminating character.
     * @throws IMTException If an error occurred during reading, or EOF was encountered.
     */
    private void readUntil(final StringBuffer buff, final char c)
            throws IOException {
        while (read() != c) {
            buff.append(lastCharacterRead);
        }
    }
    
    /**
     * Checks if the last character read was the same as the parameter. If it
     * wasn't then print a debug message (if debug output is enabled), and then
     * skip whitespaces. If skipping whitespaces results in an EOF being
     * encountered, an error is signaled.
     *
     * @param c
     * @throws IMTException thrown when problems reading data
     */
    private void check(final char c) throws IOException {
        if (lastCharacterRead != c) {
            skipSpaces();
        }
    }
    
    /**
     * Skip white spaces. Will fail if EOF is encountered.
     *
     * @throws IMTException thrown when problems reading data
     */
    private void skipSpaces() throws IOException {
        while (lastCharacterRead <= ' ') {
            read();
        }
    }
    
    /**
     * True iff the character is an US letter [a-zA-Z].
     *
     * @param c The character to test.
     * @return true if the character c is a US letter [a-zA-Z]. false otherwise.
     */
    private boolean isLetter(final char c) {
        return (((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z')));
    }
    
    /**
     * True iff the character is the legal start of a name, meaning [a-zA-Z:_].
     *
     * @param c character to test
     * @return true iff this is the legal start of a name.
     */
    private boolean isNameStart(final char c) {
        return (isLetter(c) || (c == '_') || (c == ':'));
    }
    
    /**
     * True iff the character is the legal start of a name, meaning
     * [-a-zA-Z:_0-9.].
     *
     * @param c character to test
     * @return true iff it is legal to use this character in a name.
     */
    private boolean isNameChar(final char c) {
        return (isNameStart(c) || Character.isDigit(c) || (c == '.') || (c == '-'));
    }
    
    /**
     * Read a name. Assume that the first character of the name is present in
     * the lastCharacterRead field.
     *
     * @return The name as a string
     * @throws IMTException Anything went wrong during the reading.
     */
    private String readName() throws IOException { // Precondition:
        final StringBuffer name = new StringBuffer();
        char c = lastCharacterRead;
        do {
            name.append(c);
            c = read();
        } while (isNameChar(c));
        return (name.toString());
        // Endcondition: lastCharacterRead== first character after name
    }
    
    /**
     * Create a new token element. Does NOT set currenToken!
     *
     * @param type       The type, One of START_TAG, END_TAG, INSTRUCTION, DECLARATION,
     *                   END_OF_FILE.
     * @param name       The name, a string.
     * @param attributes The attributes.
     * @return A new token element.
     */
    private MarkupToken createToken(final int type, final String name,
                                    final Hashtable attributes) {
        final MarkupToken token = new MarkupToken(type, name, attributes,
                 consumeToken());
        return (token);
    }
    
    /**
     * Consume the textBuffer as a token, so trim the
     * returned string.
     *
     * @return the content of the textBuffer, as a string.
     */
    private String consumeToken() {
        String text = consumeTextBuffer();
        if (text == null) {
            return null;
        } else {
            text = text.trim();
            return (text);
        }
    }
    
    /**
     * Consume the content of the textBuffer, return the consumed content as a
     * String, and set the textBuffer to be null.
     *
     * @return the content of the textBuffer, as a string.
     */
    private String consumeTextBuffer() {
        String text = null;
        if (textBuffer != null) {
                text = textBuffer.toString();
            text = unescape(textBuffer).toString();
            textBuffer = null;
            // if(NOT_IN_PRE)
            // TODO: PRE-FORMATING is not yet IMPLEMENTED !!!!
            // {
            if (text.length() == 0) {
                return (null);
            }
            // }
        }
        return (text);
    }
    
    /**
     * Read a character. If EOF is read, throw IOException. If LOG_RAW_INPUT is
     * true, copy the raw input being read into the rawInput variable. Set the
     * lastCharacterRead variable to the character being read. If the character
     * has an ASCII representation less than 32 (space), then change the
     * character into space before returning it.
     *
     * @return The character just read, or space if it was a non-printable
     *         character.
     * @throws IMTException if read fails, or if EOF is read.
     */
    private char read() throws IOException {
        if (lastCharacterRead == EOF) {
        }
        try {
            lastCharacterRead = (char) reader.read();
            readLen++;
        } catch (final IOException e) {
        }

        if (lastCharacterRead < ASCII_ENCODING_OF_SPACE_CHAR) {
            lastCharacterRead = ASCII_ENCODING_OF_SPACE_CHAR;
        }
        return (lastCharacterRead);
    }
    
} 
