import java.util.regex.Pattern;

/**
 * Abstract base class representing a generic Parcel.
 * Demonstrates Polymorphism through abstract and overridden methods
 * across DocumentParcel, BoxParcel, and FragileParcel subclasses.
 */
public abstract class Parcel {
    public static final Pattern TRACKING_NUMBER_PATTERN = Pattern.compile("^ABC\\d{4}$");

    private final String trackingNumber;
    private final String receiverName;
    private final double weight;
    private DeliveryServiceStrategy deliveryService;
    private ParcelStatus status;

    protected Parcel(String trackingNumber, String receiverName, double weight, double maxWeight)
            throws InvalidParcelDataException {
        validateTrackingNumber(trackingNumber);
        validateReceiverName(receiverName);
        validateWeight(weight, maxWeight);

        this.trackingNumber = trackingNumber.trim();
        this.receiverName = receiverName.trim();
        this.weight = weight;
        this.status = ParcelStatus.PENDING;
    }

    public static void validateTrackingNumber(String trackingNumber) throws InvalidParcelDataException {
        if (trackingNumber == null || !TRACKING_NUMBER_PATTERN.matcher(trackingNumber.trim()).matches()) {
            throw new InvalidParcelDataException(
                    "Invalid Tracking Number! Must be 'ABC' followed by 4 digits (e.g., ABC0001).");
        }
    }

    public static void validateReceiverName(String receiverName) throws InvalidParcelDataException {
        if (receiverName == null || receiverName.trim().isEmpty()) {
            throw new InvalidParcelDataException("Receiver Name cannot be empty!");
        }
    }

    public static void validateWeight(double weight, double maxWeight) throws InvalidParcelDataException {
        if (weight <= 0) {
            throw new InvalidParcelDataException("Weight must be greater than 0 kg!");
        }
        if (weight > maxWeight) {
            throw new InvalidParcelDataException(
                    String.format("Weight exceeds the maximum allowed weight of %.1f kg for this parcel type!", maxWeight));
        }
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public double getWeight() {
        return weight;
    }

    public DeliveryServiceStrategy getDeliveryService() {
        return deliveryService;
    }

    public ParcelStatus getStatus() {
        return status;
    }

    /**
     * Strategy Pattern: Swaps or assigns the delivery service strategy at runtime
     * after validating chargeable weight eligibility.
     */
    public void setDeliveryService(DeliveryServiceStrategy deliveryService)
            throws InvalidDeliveryServiceException, InvalidParcelDataException {
        if (deliveryService == null) {
            throw new InvalidParcelDataException("Delivery service cannot be null!");
        }
        deliveryService.validateEligibility(calculateChargeableWeight());
        this.deliveryService = deliveryService;
    }

    public void markAsDelivered() throws ParcelAlreadyDeliveredException {
        if (this.status == ParcelStatus.DELIVERED) {
            throw new ParcelAlreadyDeliveredException("Parcel was already delivered.");
        }
        this.status = ParcelStatus.DELIVERED;
    }

    /**
     * Polymorphic method returning the display name of the parcel type.
     */
    public abstract String getParcelType();

    /**
     * Polymorphic method to compute the chargeable weight in kg.
     */
    public abstract double calculateChargeableWeight();

    /**
     * Polymorphic method to compute the base fee in Php.
     */
    public abstract double calculateBaseFee();

    /**
     * Computes the total delivery fee by delegating to the active DeliveryServiceStrategy.
     */
    public double calculateDeliveryFee() {
        if (deliveryService == null) {
            return calculateBaseFee();
        }
        return deliveryService.calculateDeliveryFee(calculateBaseFee());
    }
}
