package io.msoffice.excel.ResultWordExport;

import data.entities.CriticalAccessEntry;
import data.entities.Whitelist;

public class SaveResult {

    public SaveResult() {
    }

    public SaveResult(CriticalAccessEntry criticalaccessentry, Whitelist whitelist) throws Exception {

        ExportWord word = new ExportWord("ExportWord_template.docx","ExportWordNew.docx", criticalaccessentry, whitelist);

    }
}
