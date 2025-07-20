package lapes.cesupa.ps_backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lapes.cesupa.ps_backend.dto.CustomerAnalyticsResponse;
import lapes.cesupa.ps_backend.dto.PopularItemResponse;
import lapes.cesupa.ps_backend.dto.SalesReportResponse;
import lapes.cesupa.ps_backend.service.ReportService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sales")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<SalesReportResponse> getSalesReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        var salesReport = reportService.getSalesReport(startDate, endDate);
        return ResponseEntity.ok(salesReport);
    }

    @GetMapping("/popular-items")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<PopularItemResponse>> listPopularItems(){
        var items = reportService.getPopularItemsReport();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/customer-analytics")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<CustomerAnalyticsResponse> getAnalytics(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
            var analytics = reportService.getCustomerAnalytics(startDate, endDate);
            return ResponseEntity.ok(analytics);
    }
}
