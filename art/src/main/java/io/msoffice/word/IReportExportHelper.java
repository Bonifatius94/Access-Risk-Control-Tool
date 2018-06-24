package io.msoffice.word;

import data.entities.CriticalAccessQuery;

public interface IReportExportHelper {

    void exportReport(CriticalAccessQuery query, String filePath, ReportExportType type) throws Exception;

}
