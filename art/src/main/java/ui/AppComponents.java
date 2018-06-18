package ui;

import data.localdb.ArtDbContext;
import tools.tracing.TraceOut;

public class AppComponents {

    // =============================
    //       database context
    // =============================

    private static ArtDbContext dbContext = null;

    public static ArtDbContext getDbContext() {
        return dbContext;
    }

    /**
     * This method creates a new instance of ArtDbContext using the given username and password.
     *
     * @param username the username of the new database context
     * @param password the password of the new database context
     * @return a new instance of ArtDbContext
     */
    public static ArtDbContext initDbContext(String username, String password) {

        TraceOut.enter();

        // close old context
        if (dbContext != null) {
            dbContext.close();
        }

        try {

            // create a new db context instance (if username or password is invalid an exception is thrown)
            dbContext = new ArtDbContext(username, password);

        } catch (Exception ex) {

            // TODO: implement custom exception
            throw new IllegalArgumentException("Username or password is invalid.");
        }

        TraceOut.leave();
        return dbContext;
    }

}
