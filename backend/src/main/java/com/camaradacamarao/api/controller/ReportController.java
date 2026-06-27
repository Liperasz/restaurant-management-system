package com.camaradacamarao.api.controller;

import com.camaradacamarao.api.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/top-items")
    public List<Object[]> getTopItems(
            @RequestParam int month,
            @RequestParam int year) {
        return reportService.topItemsByPeriod(month, year);
    }

    @GetMapping("/least-items")
    public List<Object[]> getLeastItems(
            @RequestParam int month,
            @RequestParam int year) {
        return reportService.leastItemsByPeriod(month, year);
    }
}
