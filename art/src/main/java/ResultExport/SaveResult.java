package ResultExport;

import data.entities.AccessPattern;
import data.entities.AccessPatternCondition;
import data.entities.AccessPatternConditionProperty;
import data.entities.Whitelist;
import io.msoffice.excel.AccessPatternImportHelper;

import ResultExport.SaveResult;
import com.jfoenix.controls.JFXButton;
import data.entities.AccessCondition;
import data.entities.AccessPattern;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;

import io.msoffice.excel.AccessPatternImportHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;


import java.util.List;

public class SaveResult {

    public SaveResult() {
    }

    public SaveResult(List<AccessPattern> patterns, Whitelist whitelist) throws Exception {

        ExportWord word = new ExportWord("ExportWord.docx", patterns, whitelist);

    }



}