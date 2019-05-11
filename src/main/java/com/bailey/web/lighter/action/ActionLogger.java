package com.bailey.web.lighter.action;

import com.bailey.web.lighter.WebLighterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionLogger {
    final public static Logger logger = LoggerFactory.getLogger(WebLighterConfig.LIB_NAME + ".Action");
    final public static String RC = System.getProperty("line.separator");
}
