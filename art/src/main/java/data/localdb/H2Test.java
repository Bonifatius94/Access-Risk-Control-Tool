package data.localdb;

public class H2Test {

    /**
     * This method runs a test program.
     *
     * @param args no args, will be ignored
     */
    public static void main(String[] args) {

        // create db context
        // this automatically creates a new database file with the database schema (code-first-approach)
        try (ArtDbContext context = new ArtDbContext("", "")) {

            System.out.println("database schema created");

        } catch (Exception ex) {

            System.out.println("program encountered an error during database schema creation");
            ex.printStackTrace();
        }
    }

}