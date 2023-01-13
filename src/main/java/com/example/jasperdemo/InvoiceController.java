package com.example.jasperdemo;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
public class InvoiceController {

    private final UserRepository userRepository;

    public InvoiceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/pdf")
    public Response generatePdf(@RequestParam String title) {
        InputStream stream = null;
        try {
            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(userRepository.findAll());
            stream = new FileInputStream("src/main/resources/invoice.jrxml");
            JasperReport report = JasperCompileManager
                    .compileReport(stream);
            Map<String, Object> map = new HashMap<>();
            map.put("title", title);
            JasperPrint print = JasperFillManager.fillReport(report, map, beanCollectionDataSource);
//            JasperExportManager.exportReportToPdfFile(print, "invoice.pdf");
            byte[] data = JasperExportManager.exportReportToPdf(print);
//            HttpHeaders httpHeaders = new HttpHeaders();
//            httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename = invoice.pdf");
//            return ResponseEntity.ok().headers(httpHeaders).contentType(MediaType.APPLICATION_PDF).body(data);
            return new Response(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
