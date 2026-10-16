/**
 * Strategy Design Pattern: Defines a family of delivery fee calculation
 * algorithms and makes them interchangeable at runtime.
 */
public interface DeliveryServiceStrategy {
    String getServiceName();

    void validateEligibility(double chargeableWeight) throws InvalidDeliveryServiceException;

    double calculateDeliveryFee(double baseFee);
}
