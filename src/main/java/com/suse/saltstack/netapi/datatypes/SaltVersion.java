package com.suse.saltstack.netapi.datatypes;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a salt version
 */
public class SaltVersion implements Comparable<SaltVersion> {

    private static Pattern SALT_VERSION_REGEX =
            Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)([Rr][Cc](\\d+))?$");

    private final int year;
    private final int month;
    private final int bugfix;
    private final Optional<Integer> releaseCandidate;

    /**
     * Parses a salt version string
     * @param versionString the salt version string
     * @return SaltVersion if the versionString is valid or empty Optional if not.
     */
    public static Optional<SaltVersion> parse(String versionString) {
        Matcher matcher = SALT_VERSION_REGEX.matcher(versionString);
        if (matcher.matches()) {
            int year = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int bugfix = Integer.parseInt(matcher.group(3));
            Optional<Integer> rc = Optional.ofNullable(matcher.group(5))
                    .map(Integer::parseInt);
            return Optional.of(new SaltVersion(year, month, bugfix, rc));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Creates a SaltVersion
     * @param year Year of the release
     * @param month Month of the release
     * @param bugfix Bugfix number incremented withing a feature release
     * @param releaseCandidate Optional release candidate tag
     */
    public SaltVersion(int year, int month, int bugfix,
            Optional<Integer> releaseCandidate) {
        this.year = year;
        this.month = month;
        this.bugfix = bugfix;
        this.releaseCandidate = releaseCandidate;
    }

    /**
     * Creates a SaltVersion
     * @param year Year of the release
     * @param month Month of the release
     * @param bugfix Bugfix number incremented withing a feature release
     * @param releaseCandidate release candidate tag
     */
    public SaltVersion(int year, int month, int bugfix, int releaseCandidate) {
        this(year, month, bugfix, Optional.of(releaseCandidate));
    }

    /**
     * Creates a SaltVersion without release candidate tag
     * @param year Year of the release
     * @param month Month of the release
     * @param bugfix Bugfix number incremented withing a feature release
     */
    public SaltVersion(int year, int month, int bugfix) {
        this(year, month, bugfix, Optional.empty());
    }

    /**
     * Getter for the release candidate of this SaltVersion
     *
     * @return the optional release candidate
     */
    public Optional<Integer> getReleaseCandidate() {
        return releaseCandidate;
    }

    /**
     * Getter for the year of this SaltVersion
     *
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * Getter for the month of this SaltVersion
     *
     * @return the month
     */
    public int getMonth() {
        return month;
    }

    /**
     * Getter for the bugfix of this SaltVersion
     *
     * @return the bugfix number
     */
    public int getBugfix() {
        return bugfix;
    }

    @Override
    public String toString() {
        return getYear() + "." + getMonth() + "." + getBugfix() + getReleaseCandidate()
                .map(rc -> "rc" + rc)
                .orElse("");
    }

    @Override
    public int compareTo(SaltVersion other) {
        if (this == other) {
            return 0;
        }

        if (this.getYear() > other.getYear()) {
            return 1;
        } else if (this.getYear() < other.getYear()) {
            return -1;
        }

        if (this.getMonth() > other.getMonth()) {
            return 1;
        } else if (this.getMonth() < other.getMonth()) {
            return -1;
        }

        if (this.getBugfix() > other.getBugfix()) {
            return 1;
        } else if (this.getBugfix() < other.getBugfix()) {
            return -1;
        }

        return this.getReleaseCandidate().map(lhsRc ->
                        other.getReleaseCandidate()
                                .map(lhsRc::compareTo)
                                .orElse(-1)
        ).orElseGet(() ->
                        other.getReleaseCandidate()
                                .map(rhsRc -> 1)
                                .orElse(0)
        );
    }
}
