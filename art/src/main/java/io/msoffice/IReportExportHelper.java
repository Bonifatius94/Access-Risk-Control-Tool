package io.msoffice;

import data.entities.CriticalAccessQuery;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Locale;

public interface IReportExportHelper {

    /**
     * This method exports a critical access query to the given file with the given export format.
     *
     * @param query is the result of an anlysis
     * @param file is the File, where the critical entries are written in
     * @param language is the language of the export format
     * @param chartImages are the chart graphics displayed (only Word / PDF format)
     * @throws Exception caused by incompatible file type or failed write process and other I/O exceptions
     */
    void exportDocument(CriticalAccessQuery query, File file, Locale language, List<BufferedImage> chartImages) throws Exception;
}
