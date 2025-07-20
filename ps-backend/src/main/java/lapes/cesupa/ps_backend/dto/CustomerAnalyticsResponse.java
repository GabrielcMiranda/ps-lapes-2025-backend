package lapes.cesupa.ps_backend.dto;

public record CustomerAnalyticsResponse(long newCustomers, double averageOrdersPerCustomer, double reorderRatePercentage) {

}
