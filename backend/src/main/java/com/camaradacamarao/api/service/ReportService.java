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

    public List<Object[]> topItemsThisMonth() {
        LocalDate now = LocalDate.now();
        return orderItemRepository.findTopItemsByMonth(now.getMonthValue(), now.getYear());
    }

    public List<Object[]> topItemsThisYear() {
        LocalDate now = LocalDate.now();
        return orderItemRepository.findTopItemsByYear(now.getYear());
    }

    public List<Object[]> leastItemsThisMonth() {
        LocalDate now = LocalDate.now();
        return orderItemRepository.findLeastItemsByMonth(now.getMonthValue(), now.getYear());
    }

    public List<Object[]> leastItemsThisYear() {
        LocalDate now = LocalDate.now();
        return orderItemRepository.findLeastItemsByYear(now.getYear());
    }
}
