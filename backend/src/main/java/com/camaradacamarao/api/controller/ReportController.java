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
    public List<Object[]> getTopItems(@RequestParam(defaultValue = "month") String period) {
        if ("year".equalsIgnoreCase(period)) {
            return reportService.topItemsThisYear();
        }
        return reportService.topItemsThisMonth();
    }

    @GetMapping("/least-items")
    public List<Object[]> getLeastItems(@RequestParam(defaultValue = "month") String period) {
        if ("year".equalsIgnoreCase(period)) {
            return reportService.leastItemsThisYear();
        }
        return reportService.leastItemsThisMonth();
    }
}
