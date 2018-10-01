package com.maths22.ftclivescoring.services;

import com.maths22.ftclivescoring.controllers.PrintController;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JsonDataSource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class ScoringReportService {
    public boolean saveReport(InputStream in, String name) {
        String reportName = "generated-reports/" + name + ".pdf";
        File reportFile = new File(reportName);
        if(!reportFile.getParentFile().exists() && !reportFile.getParentFile().mkdirs()) {
            return false;
        }


        JasperReport jasperReport;
        JasperReport subReport;
        try {
            jasperReport = JasperCompileManager.compileReport(
                    PrintController.class.getResourceAsStream("/reports/FTC_RR_2_Alliance_Report.jrxml"));
            subReport = JasperCompileManager.compileReport(
                    PrintController.class.getResourceAsStream("/reports/FTC_RR_Alliance_Report.jrxml"));
        } catch (JRException e) {
            e.printStackTrace();
            return false;
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("subReport", subReport);
        URL url = this.getClass().getClassLoader().getResource("images/FIRSTTech_IconVert_RGB.png");
        parameters.put("ftc_logo", url);
        URL url2 = this.getClass().getClassLoader().getResource("images/FIRST-FTC-RelicRecovery'17-18-Color.png");
        parameters.put("rr_logo", url2);
        JasperPrint print;
        try {
            print = JasperFillManager.fillReport(jasperReport, parameters, new JsonDataSource(in));
        } catch (JRException e) {
            e.printStackTrace();
            return false;
        }

        try {
            JasperExportManager.exportReportToPdfStream(print, new FileOutputStream(reportFile));
        } catch (JRException | FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
