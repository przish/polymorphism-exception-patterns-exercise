/**
 * Concrete Parcel subclass representing a Box.
 * - Additional fields: Length, Width, Height in cm (integer, > 0)
 * - Maximum weight: 50 kg
 * - Volumetric weight = (Length * Width * Height) / 3500
 * - Chargeable weight = the bigger of actual weight and volumetric weight,
 *   rounded UP to the next whole kg (2.0 -> 2, 2.1 -> 3)
 * - Base fee = Php 40 * chargeable weight
 */
public class BoxParcel extends Parcel {
    public static final double MAX_WEIGHT = 50.0;
    public static final double VOLUMETRIC_DIVISOR = 3500.0;
    public static final double RATE_PER_KG = 40.0;

    private final int length;
    private final int width;
    private final int height;

    public BoxParcel(String trackingNumber, String receiverName, double weight, int length, int width, int height)
            throws InvalidParcelDataException {
        this(trackingNumber, receiverName, weight, MAX_WEIGHT, length, width, height);
    }

    protected BoxParcel(String trackingNumber, String receiverName, double weight, double maxWeight,
            int length, int width, int height) throws InvalidParcelDataException {
        super(trackingNumber, receiverName, weight, maxWeight);
        validateDimension(length, "Length");
        validateDimension(width, "Width");
        validateDimension(height, "Height");
        this.length = length;
        this.width = width;
        this.height = height;
    }

    public static void validateDimension(int value, String dimensionName) throws InvalidParcelDataException {
        if (value <= 0) {
            throw new InvalidParcelDataException(dimensionName + " must be greater than 0 cm!");
        }
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double calculateVolumetricWeight() {
        return ((double) length * width * height) / VOLUMETRIC_DIVISOR;
    }

    @Override
    public String getParcelType() {
        return "Box";
    }

    @Override
    public double calculateChargeableWeight() {
        double biggerWeight = Math.max(getWeight(), calculateVolumetricWeight());
        return Math.ceil(biggerWeight);
    }

    @Override
    public double calculateBaseFee() {
        return RATE_PER_KG * calculateChargeableWeight();
    }
}
