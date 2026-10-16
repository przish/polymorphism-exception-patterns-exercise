import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton Design Pattern (Thread-safe):
 * Guarantees a single centralized instance for managing all parcels in the system.
 * Uses a private constructor, volatile instance reference, and static getInstance()
 * with double-checked locking.
 */
public class ParcelManager {
    private static volatile ParcelManager instance;

    private final Map<String, Parcel> parcelsByTrackingNumber;

    private ParcelManager() {
        this.parcelsByTrackingNumber = new LinkedHashMap<>();
    }

    public static ParcelManager getInstance() {
        if (instance == null) {
            synchronized (ParcelManager.class) {
                if (instance == null) {
                    instance = new ParcelManager();
                }
            }
        }
        return instance;
    }

    public void validateUniqueTrackingNumber(String trackingNumber)
            throws InvalidParcelDataException, DuplicateTrackingNumberException {
        Parcel.validateTrackingNumber(trackingNumber);
        String normalized = trackingNumber.trim();
        if (parcelsByTrackingNumber.containsKey(normalized)) {
            throw new DuplicateTrackingNumberException(
                    "Duplicate Tracking Number! A parcel with tracking number " + normalized + " already exists.");
        }
    }

    public void addParcel(Parcel parcel) throws DuplicateTrackingNumberException {
        String key = parcel.getTrackingNumber();
        if (parcelsByTrackingNumber.containsKey(key)) {
            throw new DuplicateTrackingNumberException(
                    "Duplicate Tracking Number! A parcel with tracking number " + key + " already exists.");
        }
        parcelsByTrackingNumber.put(key, parcel);
    }

    public Parcel findParcel(String trackingNumber) throws ParcelNotFoundException {
        if (trackingNumber == null) {
            throw new ParcelNotFoundException("Parcel not found!");
        }
        Parcel parcel = parcelsByTrackingNumber.get(trackingNumber.trim());
        if (parcel == null) {
            throw new ParcelNotFoundException("Parcel not found!");
        }
        return parcel;
    }

    public Parcel getParcelForServiceChange(String trackingNumber)
            throws ParcelNotFoundException, ParcelAlreadyDeliveredException {
        Parcel parcel = findParcel(trackingNumber);
        if (parcel.getStatus() == ParcelStatus.DELIVERED) {
            throw new ParcelAlreadyDeliveredException("Parcel already delivered!");
        }
        return parcel;
    }

    public void markAsDelivered(String trackingNumber)
            throws ParcelNotFoundException, ParcelAlreadyDeliveredException {
        Parcel parcel = findParcel(trackingNumber);
        parcel.markAsDelivered();
    }

    public List<Parcel> getAllParcels() {
        return Collections.unmodifiableList(new ArrayList<>(parcelsByTrackingNumber.values()));
    }
}
