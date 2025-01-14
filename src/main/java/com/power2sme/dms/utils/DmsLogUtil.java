package com.power2sme.dms.utils;

import org.slf4j.Logger;

public final class DmsLogUtil {
	private DmsLogUtil() {
	}

	public static void logAtTrace(Logger log, Object msg) {
		if (log.isTraceEnabled()) {
			log.trace(String.valueOf(msg));
		}
	}

	public static void logAtDebug(Logger log, Object msg) {
		if (log.isDebugEnabled()) {
			log.debug(String.valueOf(msg));
		}
	}

	public static void logAtInfo(Logger log, Object msg) {
		if (log.isInfoEnabled()) {
			log.info(String.valueOf(msg));
		}
	}

	public static void logAtError(Logger log, String msg) {
		if (log.isErrorEnabled()) {
			log.error(String.valueOf(msg));
		}
	}

	public static void logAtError(Logger log, String msg, Throwable e) {
		if (log.isErrorEnabled()) {
			log.error(String.valueOf(msg), e);
		}
	}
}
