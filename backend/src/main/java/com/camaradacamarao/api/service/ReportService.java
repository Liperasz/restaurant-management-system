package com.camaradacamarao.api.service;

import com.camaradacamarao.api.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderItemRepository orderItemRepository;

    public List<Object[]> topItemsByPeriod(int month, int year) {
        return orderItemRepository.findTopItemsByMonth(month, year);
    }

    public List<Object[]> leastItemsByPeriod(int month, int year) {
        return orderItemRepository.findLeastItemsByMonth(month, year);
    }
}
