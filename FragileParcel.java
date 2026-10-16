/**
 * Concrete Parcel subclass representing a Fragile parcel (extends BoxParcel).
 * - Additional field: Handling Note (string, non-empty)
 * - Maximum weight: 30 kg
 * - Chargeable weight = same rule as BOX
 * - Base fee = BOX base fee + Php 100 handling fee
 */
public class FragileParcel extends BoxParcel {
    public static final double MAX_WEIGHT = 30.0;
    public static final double HANDLING_FEE = 100.0;

    private final String handlingNote;

    public FragileParcel(String trackingNumber, String receiverName, double weight,
            int length, int width, int height, String handlingNote)
            throws InvalidParcelDataException {
        super(trackingNumber, receiverName, weight, MAX_WEIGHT, length, width, height);
        validateHandlingNote(handlingNote);
        this.handlingNote = handlingNote.trim();
    }

    public static void validateHandlingNote(String handlingNote) throws InvalidParcelDataException {
        if (handlingNote == null || handlingNote.trim().isEmpty()) {
            throw new InvalidParcelDataException("Handling Note cannot be empty!");
        }
    }

    public String getHandlingNote() {
        return handlingNote;
    }

    @Override
    public String getParcelType() {
        return "Fragile";
    }

    @Override
    public double calculateBaseFee() {
        return super.calculateBaseFee() + HANDLING_FEE;
    }
}
