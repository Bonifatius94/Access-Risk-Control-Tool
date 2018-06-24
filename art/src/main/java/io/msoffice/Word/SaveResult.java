package io.msoffice.Word;

import data.entities.CriticalAccessEntry;
import data.entities.CriticalAccessQuery;
import data.entities.Whitelist;

public class SaveResult {

    public SaveResult() {
    }

    public SaveResult(CriticalAccessQuery criticalaccessquery, Whitelist whitelist) throws Exception {

        ExportWord word = new ExportWord("ExportWord_template.docx","ExportWordNew.docx", criticalaccessquery, whitelist);

    }
}
