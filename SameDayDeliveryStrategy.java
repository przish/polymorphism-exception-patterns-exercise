/**
 * Concrete Strategy for Same Day Delivery:
 * Delivery fee = (Base fee * 2) + Php 100
 * Only for parcels with a chargeable weight of 10 kg or less.
 */
public class SameDayDeliveryStrategy implements DeliveryServiceStrategy {
    private static final String SERVICE_NAME = "Same Day";
    private static final double MAX_CHARGEABLE_WEIGHT = 10.0;
    private static final double SAME_DAY_MULTIPLIER = 2.0;
    private static final double SAME_DAY_SURCHARGE = 100.0;

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public void validateEligibility(double chargeableWeight) throws InvalidDeliveryServiceException {
        if (chargeableWeight > MAX_CHARGEABLE_WEIGHT) {
            throw new InvalidDeliveryServiceException("Same Day delivery is only for parcels up to 10 kg!");
        }
    }

    @Override
    public double calculateDeliveryFee(double baseFee) {
        return (baseFee * SAME_DAY_MULTIPLIER) + SAME_DAY_SURCHARGE;
    }
}
