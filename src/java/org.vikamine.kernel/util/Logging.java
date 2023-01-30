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
 * Created on 10.11.2003
 */
package org.vikamine.kernel.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.rmi.server.UID;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Utility for loggin support.
 * 
 * @author atzmueller
 */
public class Logging {

    private static class Formatter extends SimpleFormatter {

	private final int stackTraceLength;

	public Formatter(int stackTraceLength) {
	    this.stackTraceLength = stackTraceLength;
	}

	@Override
	public synchronized String format(LogRecord record) {
	    if (record.getThrown() != null) {
		StackTraceElement[] ste = record.getThrown().getStackTrace();
		int stackSize = Math.min(stackTraceLength, ste.length);
		StackTraceElement[] newSTE = new StackTraceElement[stackSize];
		for (int i = 0; i < stackSize; i++)
		    newSTE[i] = ste[i];
		record.getThrown().setStackTrace(newSTE);
	    }
	    return super.format(record);
	}

    }

    private static boolean initialized = false;

    private static final File TEMP_DIR = new File(System
	    .getProperty("java.io.tmpdir"));

    private static final long TIME_DIFF = 1000 * 60 * 60 * 24 * 7; // 1 week

    private static final String PREFIX = "_vikamine_";
    private static final String SUFFIX = ".log";

    // corresponds to String in java.util.loggin.FileHandler.openFiles()
    private static final String LOCK = ".lck";

    private static String logFile = TEMP_DIR + File.separator + PREFIX
	    + new UID().toString().replaceAll("\\W", "_") + SUFFIX;

    private static FileHandler fileHandler;
    
    private static Logger logger;

    public static final Level DEFAULT_CONSOLE_LEVEL = Level.FINEST;
    public static final Level DEFAULT_FILE_LEVEL = Level.SEVERE;
    public static final int DEFAULT_CONSOLE_STACK_TRACE_LENGTH = 5;
    public static final int DEFAULT_FILE_STACK_TRACE_LENGTH = 1;

    private static String osVersion = System.getProperty("os.name") + " / "
	    + System.getProperty("os.arch") + " / "
	    + System.getProperty("os.version");
    private static String javaVersion = System.getProperty("java.version");
    private static Level c_level = DEFAULT_CONSOLE_LEVEL;
    private static int c_stackTraceLength = DEFAULT_CONSOLE_STACK_TRACE_LENGTH;
    private static Level f_level = DEFAULT_FILE_LEVEL;
    private static int f_stackTraceLength = DEFAULT_FILE_STACK_TRACE_LENGTH;

    private static void deleteOldLogFiles() {
	FilenameFilter filter = new FilenameFilter() {
	    @Override
	    public boolean accept(File dir, String name) {
		return name.startsWith(PREFIX)
			&& (name.endsWith(SUFFIX) || (name.endsWith(SUFFIX
				+ LOCK)));
	    }
	};

	File[] files = TEMP_DIR.listFiles(filter);
	if (files == null)
	    return;
	for (File file : files) {
	    if ((System.currentTimeMillis() - file.lastModified()) > TIME_DIFF)
		if (!file.delete()) {
		    Logger.getLogger(Logging.class.getName())
			    .warning(
				    "Error while deleting log-File : "
					    + file.toString());
		}
	}
    }

    public static void init(String resbundlename) {
	if (!initialized) {
	    logger = Logger.getLogger("org.vikamine");
	    logger.setUseParentHandlers(false);
	    logger.setLevel(Level.ALL);
	    
	    deleteOldLogFiles();
	    ResourceBundle bundle = ResourceBundle.getBundle(resbundlename);
	    c_level = Level.parse(bundle.getString("console.level"));
	    c_stackTraceLength = Integer.parseInt(bundle
		    .getString("console.stacktracelength"));
	    f_level = Level.parse(bundle.getString("file.level"));
	    f_stackTraceLength = Integer.parseInt(bundle
		    .getString("file.stacktracelength"));

	    Handler consoleHandler = new ConsoleHandler();
	    consoleHandler.setLevel(c_level);
	    consoleHandler.setFormatter(new Formatter(c_stackTraceLength));
	    logger.addHandler(consoleHandler);

	    try {
		Handler fileHandler = getFileHandler();
		fileHandler.setLevel(f_level);
		fileHandler.setFormatter(new Formatter(f_stackTraceLength));
		logger.addHandler(fileHandler);
	    } catch (SecurityException e) {
		Logger.getLogger(Logging.class.getName()).throwing(
			Logging.class.getName(), "init", e);
	    }

	    // System.out.println(getInitText());
	    initialized = true;
	}
    }

    public static FileHandler getFileHandler() {
	if (fileHandler == null) {
	    try {
		fileHandler = new FileHandler(logFile);
		fileHandler.setLevel(f_level);
		fileHandler.setFormatter(new Formatter(f_stackTraceLength));
	    } catch (IOException e) {
		Logger.getLogger(Logging.class.getName()).throwing(
			Logging.class.getName(), "init", e);
	    }
	}
	return fileHandler;
    }
    
    public static String getInitText() {
	return "++++++++++++++++ Logger for org.vikamine.* ++++++++++++++++"
		+ "\n" + "  Operating System is " + osVersion + "\n"
		+ "  Java Version is " + javaVersion + "\n"
		+ "  Console.Level is " + c_level.getName() + "\n"
		+ "  Console.StackTraceLength is " + c_stackTraceLength + "\n"
		+ "  File.Level is " + f_level.getName() + "\n"
		+ "  File.StackTraceLength is " + f_stackTraceLength + "\n"
		+ "  File output set to: " + logFile + "\n"
		+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++++";
    }

    public static String getLoggingFilename() {
	return logFile;
    }
}
