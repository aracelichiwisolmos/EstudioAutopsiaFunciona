/*
 *  This file is part of the VKM-Kernel library.
 * 
 *  Copyright (C) 2003-2008 by Martin Atzmueller, and contributors.
 *  Copyright (C) 2008-2016 by Martin Atzmueller, Florian Lemmerich, and contributors.
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Created on 30.06.2004
 *
 */
package org.vikamine.kernel.data.converters;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.logging.Logger;

/**
 * {@link ReaderUtils} contains utilities for token-based reader support.
 * 
 * @author atzmueller
 */
public class ReaderUtils {

    private ReaderUtils() {
	super();
    }

    public static void readFirstToken(StreamTokenizer tokenizer)
	    throws IOException {
	while (tokenizer.nextToken() == StreamTokenizer.TT_EOL) {
	    // do nothing
	}

	if ((tokenizer.ttype == '\'') || (tokenizer.ttype == '"')) {
	    tokenizer.ttype = StreamTokenizer.TT_WORD;
	} else if ((tokenizer.ttype == StreamTokenizer.TT_WORD)
		&& (tokenizer.sval.equals("?"))) {
	    tokenizer.ttype = '?';
	}
    }

    public static void readNextToken(StreamTokenizer tokenizer,
	    boolean isEOLSignificant) throws IOException {

	if (tokenizer.nextToken() == StreamTokenizer.TT_EOL) {
	    if (isEOLSignificant) {
		throwError(tokenizer, "premature end of line");
	    } else {
		return;
	    }
	}
	if (tokenizer.ttype == StreamTokenizer.TT_EOF) {
	    throwError(tokenizer, "premature end of file");
	} else if ((tokenizer.ttype == '\'') || (tokenizer.ttype == '"')) {
	    tokenizer.ttype = StreamTokenizer.TT_WORD;
	} else if ((tokenizer.ttype == StreamTokenizer.TT_WORD)
		&& (tokenizer.sval.equals("?"))) {
	    tokenizer.ttype = '?';
	}
    }

    public static void throwError(StreamTokenizer tokenizer, String theMsg)
	    throws IOException {
	IOException ex = new IOException(theMsg + ", read "
		+ tokenizer.toString());
	Logger.getLogger(ReaderUtils.class.getName()).throwing(
		ReaderUtils.class.getName(), "throwError", ex);
	throw ex;
    }

    public static void readLastToken(StreamTokenizer tokenizer,
	    boolean endOfFileOk) throws IOException {

	if ((tokenizer.nextToken() != StreamTokenizer.TT_EOL)
		&& ((tokenizer.ttype != StreamTokenizer.TT_EOF) || !endOfFileOk)) {
	    throwError(tokenizer, "end of line expected");
	}
    }

}
