/**
 * Concrete Strategy for Express Delivery:
 * Delivery fee = Base fee * 1.5
 */
public class ExpressDeliveryStrategy implements DeliveryServiceStrategy {
    private static final String SERVICE_NAME = "Express";
    private static final double EXPRESS_MULTIPLIER = 1.5;

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
        return baseFee * EXPRESS_MULTIPLIER;
    }
}
