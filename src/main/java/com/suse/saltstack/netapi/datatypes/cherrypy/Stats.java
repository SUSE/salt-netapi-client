package com.suse.saltstack.netapi.datatypes.cherrypy;

/**
 * Representation of CherryPy server statistics.
 */
public class Stats {

    private final Applications applications;
    private final HttpServer httpServer;

    public Stats(Applications applications, HttpServer httpServer)
            throws IllegalArgumentException {
        if (applications == null || httpServer == null) {
            throw new IllegalArgumentException(
                    "applications and httpServer must not be null");
        }
        this.applications = applications;
        this.httpServer = httpServer;
    }

    public Applications getApplications() {
        return applications;
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }
}
