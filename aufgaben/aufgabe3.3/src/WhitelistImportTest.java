import java.util.List;

public class WhitelistImportTest {

    public static void main(String[] args) {

        try {
            final String filePath = "out\\production\\aufgabe3.3\\Example - Whitelist.xlsx";
            List<WhitelistUser> users = new WhitelistImportHelper().importWhitelist(filePath);
            ((List) users).forEach(x -> System.out.println(x));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
