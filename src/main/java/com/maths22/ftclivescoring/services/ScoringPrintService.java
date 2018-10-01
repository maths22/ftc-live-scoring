package com.maths22.ftclivescoring.services;

import org.springframework.stereotype.Service;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.OrientationRequested;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Service
public class ScoringPrintService {
    private PrintService printService;
    private boolean enabled = true;

//    public List<PrintService> getPrinters() {
//        return Arrays.asList(PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.PDF, null));
//    }

    public boolean reconfigurePrintService() {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        if (printerJob.printDialog()) {
            printService = printerJob.getPrintService();
            return true;
        } else {
            return false;
        }
    }

    public boolean printScoresheet(String name) {
        String reportName = "generated-reports/" + name + ".pdf";
        File reportFile = new File(reportName);
        if(!reportFile.exists()) {
            return false;
        }

        if(!enabled) {
            return true;
        }

        if(printService == null) {
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            if (printerJob.printDialog()) {
                printService = printerJob.getPrintService();
            } else {
                return false;
            }
        }

        DocFlavor docType = DocFlavor.INPUT_STREAM.AUTOSENSE;

        DocPrintJob printJob = printService.createPrintJob();
        PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
        attributeSet.add(new JobName(name, null));
        attributeSet.add(OrientationRequested.LANDSCAPE);
        Doc documentToBePrinted;
        try {
            documentToBePrinted = new SimpleDoc(new FileInputStream(reportName), docType, null);
            printJob.print(documentToBePrinted, attributeSet);
        } catch (FileNotFoundException | PrintException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
