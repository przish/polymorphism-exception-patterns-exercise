/**
 * Concrete Parcel subclass representing a Document.
 * - Additional field: Number of Pages (integer, 1 to 100)
 * - Maximum weight: 0.5 kg
 * - Chargeable weight = actual weight
 * - Base fee = Php 50 flat
 */
public class DocumentParcel extends Parcel {
    public static final double MAX_WEIGHT = 0.5;
    public static final int MIN_PAGES = 1;
    public static final int MAX_PAGES = 100;
    public static final double FLAT_BASE_FEE = 50.0;

    private final int numberOfPages;

    public DocumentParcel(String trackingNumber, String receiverName, double weight, int numberOfPages)
            throws InvalidParcelDataException {
        super(trackingNumber, receiverName, weight, MAX_WEIGHT);
        validateNumberOfPages(numberOfPages);
        this.numberOfPages = numberOfPages;
    }

    public static void validateNumberOfPages(int numberOfPages) throws InvalidParcelDataException {
        if (numberOfPages < MIN_PAGES || numberOfPages > MAX_PAGES) {
            throw new InvalidParcelDataException(
                    String.format("Number of pages must be between %d and %d!", MIN_PAGES, MAX_PAGES));
        }
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    @Override
    public String getParcelType() {
        return "Document";
    }

    @Override
    public double calculateChargeableWeight() {
        return getWeight();
    }

    @Override
    public double calculateBaseFee() {
        return FLAT_BASE_FEE;
    }
}
