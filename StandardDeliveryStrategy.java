/**
 * Concrete Strategy for Standard Delivery:
 * Delivery fee = Base fee
 */
public class StandardDeliveryStrategy implements DeliveryServiceStrategy {
    private static final String SERVICE_NAME = "Standard";

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public void validateEligibility(double chargeableWeight) {
        // Eligible for all valid parcel weights
    }

    @Override
    public double calculateDeliveryFee(double baseFee) {
        return baseFee;
    }
}
