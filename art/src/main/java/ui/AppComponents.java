package ui;

import data.localdb.ArtDbContext;

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

        // close old context
        if (dbContext != null) {
            dbContext.close();
        }

        // create a new context instance
        dbContext = new ArtDbContext(username, password);

        return dbContext;
    }

}
