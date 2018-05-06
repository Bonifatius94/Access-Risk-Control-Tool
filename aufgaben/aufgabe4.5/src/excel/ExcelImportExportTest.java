package excel;

import excel.config.AuthorizationPattern;
import excel.config.AuthorizationPatternExportHelper;
import excel.config.AuthorizationPatternImportHelper;
import excel.whitelist.WhitelistExportHelper;
import excel.whitelist.WhitelistImportHelper;
import excel.whitelist.WhitelistEntry;

import java.util.List;

public class ExcelImportExportTest {

    /**
     * This is a test program for evaluating MS Excel import / export logic
     *
     * @param args program args (not used)
     * @author Marco Tröster (marco.troester@student.uni-augsburg.de)
     */
    public static void main(String[] args) {

        try {

            // import whitelist and export imported data afterwards
            List<WhitelistEntry> users = new WhitelistImportHelper().importWhitelist("Example - Whitelist.xlsx");
            users.forEach(x -> System.out.println(x.toString()));
            new WhitelistExportHelper().exportWhitelist("whitelist.xlsx", users);

            // import config and export imported data afterwards
            List<AuthorizationPattern> patterns = new AuthorizationPatternImportHelper().importAuthorizationPattern("Example - Zugriffsmuster.xlsx");
            patterns.forEach(x -> System.out.println(x.toString()));
            new AuthorizationPatternExportHelper().exportAuthorizationPattern("config.xlsx", patterns);

        } catch (Exception ex) { ex.printStackTrace(); }
    }

}
