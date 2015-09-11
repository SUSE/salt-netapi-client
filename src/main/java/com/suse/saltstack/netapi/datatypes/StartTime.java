package com.suse.saltstack.netapi.datatypes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.suse.saltstack.netapi.exception.ParsingException;

/**
 * StartDate is a convenience wrapper allowing for parsing of StartDate in the
 * timezone appropriate to a given master.
 */
public class StartTime {

    private final String dateString;
    private transient final Date defaultTzDate;

    // StartTime example from API: "2015, Mar 04 19:28:29.724698"
    private transient final SimpleDateFormat startJobDateFormat =
            new SimpleDateFormat("yyyy, MMM dd HH:mm:ss.SSS", Locale.US);

    /**
     * Construct a {@link StartTime} from a date given as string.
     *
     * @param dateString the start time formatted as string to be parsed
     * @throws ParsingException in case of a problem while parsing dateString
     */
    public StartTime(String dateString) throws ParsingException {
        try {
            if (dateString != null) {
                this.defaultTzDate = startJobDateFormat.parse(dateString);
            } else {
                this.defaultTzDate = null;
            }
        } catch (ParseException e) {
            throw new ParsingException(e);
        }
        this.dateString = dateString;
    }

    /**
     * Returns {@link Date} representation of StartTime as parsed at a given timezone.
     * Master does not return a timezone associated with StartTime timestamp string,
     * therefore an explicit timezone needs to be provided for correct parsing.
     *
     * @param tz TimeZone associated with master.
     * @return {@link Date} representation of StartTime at provided timezone
     */
    public synchronized Date getDate(TimeZone tz) {
        if (dateString == null) {
            return null;
        }

        startJobDateFormat.setTimeZone(tz);
        try {
            return startJobDateFormat.parse(dateString);
        } catch (ParseException e) {
            // This exception should not be possible. Even when dealing with TZ DST
            // transitions, all dates exist. Time that does not exist because of
            // forward DST transition, get automatically forwarded during parsing.
            throw new RuntimeException("Internal problem with Java and timezones " +
                    dateString + " @TZ: " + tz.toString());
        }
    }

    /**
     * Returns {@link Date} representation of StartTime as parsed using default timezone.
     *
     * <p>NOTE: If master is using a different timezone than the default timezone of the
     * user of this API, then the returned Date will be incorrect.
     *
     * @return {@link Date} representation of StartTime using default timezone.
     */
    public Date getDate() {
        return defaultTzDate;
    }

    /**
     * Returns a string representation of StartTime. This is the same string that is
     * passed to the constructor.
     */
    @Override
    public String toString() {
        return dateString;
    }
}
