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
     * @return a boolean that indicates whether the user credentials are valid and db context initialization worked fine
     */
    public static boolean tryInitDbContext(String username, String password) {

        TraceOut.enter();

        // close old context
        if (dbContext != null) {
            dbContext.close();
        }

        boolean ret = true;

        try {

            // create a new db context instance (if username or password is invalid an exception is thrown which results into returning false)
            dbContext = new ArtDbContext(username, password);

        } catch (Exception ex) {

            ex.printStackTrace();
            TraceOut.writeException(ex);
            ret = false;
        }

        TraceOut.leave();
        return ret;
    }

}
