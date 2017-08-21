package com.teamsun.bi.http.parser;

/*
 * MarkurToken.java
 * Confidential and proprietary.
 */

//package .........BlackBerryProject.cmcc_blackberry.src;

import java.util.Enumeration;
import java.util.Hashtable;

import android.util.Log;


public class MarkupToken { 

    /**
     * <b>MarkupToken</b> type for start tags. I.e. tags on the form: &lt;name
     */
    public static final int  START_TAG   = 2;

    /**
     * <b>MarkupToken</b> type for end tags. I.e. tags on the form:
     */
    public static final int  END_TAG     = 3;

    /**
     * <b>MarkupToken</b> type for processing instruction tags. I.e. tags on
     */
    public static final int  INSTRUCTION = 4;

    /**
     * <b>MarkupToken</b> type for declaration tags. I.e. tags on the form:
     */
    public static final int  DECLARATION = 5;

    /**
     * Special <b>MarkupToken</b> type representing 'end of file'.
     */
    public static final int  END_OF_FILE = 6;

    /**
     * Which type of type is this? Integer referring to one of the constants
     * START, END, INSTRUCTION, DECLARATION, END_OF_FILE declared in this class.
     */
    private int              type;

    /**
     * An unique identifier for a tag, as managed by the Tag class.
     */
    private int              tag;

    /**
     * A map mapping attribute names into attribute values.
     */
    private Hashtable        attributes;

    /**
     * XXX Don't know what this is.
     */
    private String           precedingText;

    /**
     * Creates a new <b>MarkupToken</b>.
     *
     * @param argType
     *            The MarkupToken type code
     * @param name
     *            The MarkupToken's name
     * @param argAttributes
     *            The attributes or null
     * @param argPrecedingText
     *            The preceding text or null
     */
    public MarkupToken(final int argType, final String name,
            final Hashtable argAttributes, final String argPrecedingText) {
        this.type = argType;
        // this.name=name;
        this.tag = Tag.define(name);
        this.attributes = argAttributes;
        this.precedingText = argPrecedingText;
        //#debug debug
//        logger.write(IMTLogger.DEBUG, "MarkupToken.CONSTRUCTOR - << TOKEN: " + this);
        Log.println(Log.INFO, "token", this.toString());
    }

    /**
     * Get the preceding text.
     *
     * @return preceding text.
     */
    public final String getPrecedingText() {
        return (precedingText);
    }

    /**
     * Get the tag identifier.
     *
     * @return tag identifier.
     */
    public final int getTag() {
        return (tag);
    }

    /**
     * Get the tag name.
     * 
     * @return tag name.
     */
    public final String getName() {
        return (Tag.getSymbol(tag));
    }

    /**
     * Get the tag type index.
     *
     * @return tag type index.
     */
    public final int getType() {
        return (type);
    }

    /**
     * True iff the tag is a start tag.
     *
     * @return is this tag a start tag?
     */
    public final boolean isStartTag() {
        return (type == START_TAG);
    }

    /**
     * True iff this tag is an end tag.
     *
     * @return is this tag an end tag?
     */
    public final boolean isEndTag() {
        return (type == END_TAG);
    }

    /**
     * True iff this this tag represents EOF.
     *
     * @return eof?
     */
    public final boolean isEOF() {
        return (type == END_OF_FILE);
    }

    /**
     * Return the hashtable containing all attributes.
     *
     * @return attributes hash.
     */
    public final Hashtable getAttributes() {
        return (attributes);
    }

    /**
     * Get a particular attribute. If this token does not contain it, return the
     * default value.
     *
     * @param name
     *            the name of the attribute.
     * @param defaultValue
     *            The default value for the attribute.
     * @return the attribute value.
     */
    public final String getAttribute(final String name,
            final String defaultValue) {
        String value = null;
        if (attributes != null) {
            value = (String) attributes.get(name);
        }

        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }

    /**
     * Get a particular attribute, default value is null.
     *
     * @param name
     *            attribute name.
     * @return the value of the attribute.
     */
    public final String getAttribute(final String name) {
        return (getAttribute(name, null));
    }

    /**
     * Return a string representation of the token to be used for debugging
     * purposes.
     *
     * @return A string representation of the token
     */
    public final String toString() {
        final StringBuffer buf = new StringBuffer();
        if (precedingText != null) {
            buf.append(precedingText);
        }
        switch (type) {
        case MarkupToken.START_TAG:
        case MarkupToken.END_TAG:
            editTag(buf);
            break;
        case MarkupToken.INSTRUCTION:
            buf.append("<?").append(getName()).append(" ... ?>");
            break; // <?NAME ... ?>
        case MarkupToken.DECLARATION:
            buf.append("<!").append(getName()).append(" ... >");
            break; // <!NAME ... >
        case MarkupToken.END_OF_FILE:
            buf.append("[EOF]");
            break;
        default:
            buf.append("UNKNOWN");
            break;
        }
        // buf.append(']');
        return (buf.toString());
    }

    /**
     * XXX Don't know what this does. The method name seems strange.
     *
     * @param buf
     *            the string buffer
     */
    private void editTag(final StringBuffer buf) {
        buf.append('<');
        if (type == END_TAG) {
            buf.append('/');
        }
        buf.append(getName());
        if (type == START_TAG && attributes != null) {
            appendAttributes(buf, attributes);
        }
        buf.append('>');
    }

    /**
     * Append attributes stored in a hashtable to a string buffer.
     *
     * @param buf
     *            the buffer
     * @param argAttrs
     *            the hashtable containing the attributes.
     */
    private void appendAttributes(final StringBuffer buf,
            final Hashtable argAttrs) {
        Enumeration e = argAttrs.keys();
        while (e.hasMoreElements()) {
            final String key   = (String) e.nextElement();
            final String value = (String) argAttrs.get(key);
            buf.append(' ').append(key).append("=\"").append(value)
                    .append('\"');
        }
    }
    
    
} 
