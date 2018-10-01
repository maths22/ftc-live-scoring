package com.maths22.ftclivescoring.controllers;

import com.maths22.ftclivescoring.services.ScoringPrintService;
import com.maths22.ftclivescoring.services.ScoringReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.InputStream;

@Controller
public class PrintController {

    private final ScoringPrintService printService;

    private final ScoringReportService reportService;

    @Autowired
    public PrintController(ScoringPrintService printService, ScoringReportService reportService) {
        this.printService = printService;
        this.reportService = reportService;
    }

    @PostMapping("/api/print")
    public String makeReport(InputStream in) {
        if(!reportService.saveReport(in, "scoring_sheet")) return "bad";
        return printService.printScoresheet("scoring_sheet") ? "ok" : "bad";
    }

    @PostMapping("/api/print/enable")
    public String enable(boolean enable) {
        printService.setEnabled(enable);
        return "ok";
    }

    @PostMapping("/api/print/configure")
    public String reconfigurePrinter() {
        return printService.reconfigurePrintService() ? "ok" : "bad";
    }
}
