package com.davenonymous.smarthome.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateHelper {
	public static Instant START_DATE = Instant.parse("2025-01-01T00:00:00Z");
	public static int TICKS_PER_HOUR = 1000;
	public static int TICKS_PER_DAY = 24000;

	public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
		.withZone(ZoneId.systemDefault());

	public static Instant fromTick(long tick) {
		long passedDays = (long)tick / 24000;
		long passedHours = ((long)tick % 24000) / 1000;
		long passedMinutes = (long)((((long)tick % 24000) % 1000) / 1000.0 * 60);
		long passedSeconds = (long)((((((long)tick % 24000) % 1000) % 1000) / 1000.0 * 60 * 60));
		return START_DATE.plusSeconds(passedDays * 86400 + passedHours * 3600 + passedMinutes * 60 + passedSeconds);
	}
}
