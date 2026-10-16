import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ParcelManager parcelManager = ParcelManager.getInstance();

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            printMainMenu();
            int choice = promptInt("Enter your choice (1-5): ");
            System.out.println();

            switch (choice) {
                case 1 -> handleAddParcel();
                case 2 -> handleViewAllParcels();
                case 3 -> handleChangeDeliveryService();
                case 4 -> handleMarkAsDelivered();
                case 5 -> {
                    System.out.println("Exiting Parcel Delivery System. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice! Please select a valid option (1-5).");
            }
            System.out.println();
        }
    }

    private static void printMainMenu() {
        System.out.println("==========================================");
        System.out.println("          PARCEL DELIVERY SYSTEM          ");
        System.out.println("==========================================");
        System.out.println("1. Add Parcel");
        System.out.println("2. View All Parcels");
        System.out.println("3. Change Delivery Service");
        System.out.println("4. Mark as Delivered");
        System.out.println("5. Exit");
        System.out.println("==========================================");
    }

    private static void handleAddParcel() {
        System.out.println("--- Add Parcel ---");
        int typeChoice = promptParcelType();
        System.out.println();

        String trackingNumber = promptValidTrackingNumber();
        String receiverName = promptValidReceiverName();

        double maxWeight = switch (typeChoice) {
            case 1 -> DocumentParcel.MAX_WEIGHT;
            case 2 -> BoxParcel.MAX_WEIGHT;
            case 3 -> FragileParcel.MAX_WEIGHT;
            default -> throw new IllegalStateException("Unexpected parcel type: " + typeChoice);
        };

        double weight = promptValidWeight(maxWeight);

        Parcel parcel = null;
        while (parcel == null) {
            try {
                if (typeChoice == 1) {
                    int pages = promptValidPages();
                    parcel = new DocumentParcel(trackingNumber, receiverName, weight, pages);
                } else if (typeChoice == 2) {
                    int length = promptValidDimension("Enter Length (cm): ", "Length");
                    int width = promptValidDimension("Enter Width (cm): ", "Width");
                    int height = promptValidDimension("Enter Height (cm): ", "Height");
                    parcel = new BoxParcel(trackingNumber, receiverName, weight, length, width, height);
                } else if (typeChoice == 3) {
                    int length = promptValidDimension("Enter Length (cm): ", "Length");
                    int width = promptValidDimension("Enter Width (cm): ", "Width");
                    int height = promptValidDimension("Enter Height (cm): ", "Height");
                    String handlingNote = promptValidHandlingNote();
                    parcel = new FragileParcel(trackingNumber, receiverName, weight, length, width, height, handlingNote);
                }
            } catch (InvalidParcelDataException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        System.out.println();
        // Select delivery service (loop if Same Day is not allowed)
        while (true) {
            DeliveryServiceStrategy strategy = promptDeliveryServiceStrategy();
            try {
                parcel.setDeliveryService(strategy);
                break;
            } catch (InvalidDeliveryServiceException | InvalidParcelDataException e) {
                System.out.println(e.getMessage());
                System.out.println();
            }
        }

        try {
            parcelManager.addParcel(parcel);
            System.out.println();
            System.out.printf("Parcel added successfully! Delivery fee: Php %.2f%n", parcel.calculateDeliveryFee());
        } catch (DuplicateTrackingNumberException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void handleViewAllParcels() {
        System.out.println("--- All Parcels ---");
        List<Parcel> parcels = parcelManager.getAllParcels();

        if (parcels.isEmpty()) {
            System.out.println("No parcels found in the system.");
            return;
        }

        String[] headers = {
                "Tracking #", "Type", "Receiver", "Chargeable Weight", "Service", "Delivery Fee", "Status"
        };

        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
        }

        for (Parcel parcel : parcels) {
            String weightStr = String.format("%.2f kg", parcel.calculateChargeableWeight());
            String feeStr = String.format("Php %.2f", parcel.calculateDeliveryFee());

            widths[0] = Math.max(widths[0], parcel.getTrackingNumber().length());
            widths[1] = Math.max(widths[1], parcel.getParcelType().length());
            widths[2] = Math.max(widths[2], parcel.getReceiverName().length());
            widths[3] = Math.max(widths[3], weightStr.length());
            widths[4] = Math.max(widths[4], parcel.getDeliveryService().getServiceName().length());
            widths[5] = Math.max(widths[5], feeStr.length());
            widths[6] = Math.max(widths[6], parcel.getStatus().getDisplayName().length());
        }

        StringBuilder separatorBuilder = new StringBuilder("+");
        StringBuilder rowFormatBuilder = new StringBuilder("|");
        for (int width : widths) {
            separatorBuilder.append("-".repeat(width + 2)).append("+");
            rowFormatBuilder.append(" %-").append(width).append("s |");
        }
        rowFormatBuilder.append("%n");

        String separator = separatorBuilder.toString();
        String rowFormat = rowFormatBuilder.toString();

        System.out.println(separator);
        System.out.printf(rowFormat, (Object[]) headers);
        System.out.println(separator);

        for (Parcel parcel : parcels) {
            String weightStr = String.format("%.2f kg", parcel.calculateChargeableWeight());
            String feeStr = String.format("Php %.2f", parcel.calculateDeliveryFee());

            System.out.printf(rowFormat,
                    parcel.getTrackingNumber(),
                    parcel.getParcelType(),
                    parcel.getReceiverName(),
                    weightStr,
                    parcel.getDeliveryService().getServiceName(),
                    feeStr,
                    parcel.getStatus().getDisplayName());
        }
        System.out.println(separator);
    }

    private static void handleChangeDeliveryService() {
        System.out.println("--- Change Delivery Service ---");
        System.out.print("Enter Tracking Number: ");
        String trackingNumber = scanner.nextLine().trim();

        Parcel parcel;
        try {
            parcel = parcelManager.getParcelForServiceChange(trackingNumber);
        } catch (ParcelNotFoundException | ParcelAlreadyDeliveredException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println();
        DeliveryServiceStrategy newStrategy = promptDeliveryServiceStrategy();
        try {
            parcel.setDeliveryService(newStrategy);
            System.out.println();
            System.out.printf("Delivery service changed! New fee: Php %.2f%n", parcel.calculateDeliveryFee());
        } catch (InvalidDeliveryServiceException | InvalidParcelDataException e) {
            // The parcel keeps its old service
            System.out.println();
            System.out.println(e.getMessage());
        }
    }

    private static void handleMarkAsDelivered() {
        System.out.println("--- Mark as Delivered ---");
        System.out.print("Enter Tracking Number: ");
        String trackingNumber = scanner.nextLine().trim();

        try {
            parcelManager.markAsDelivered(trackingNumber);
            System.out.println("Parcel delivered successfully!");
        } catch (ParcelNotFoundException | ParcelAlreadyDeliveredException e) {
            System.out.println(e.getMessage());
        }
    }

    private static int promptParcelType() {
        while (true) {
            System.out.println("Select Parcel Type:");
            System.out.println("  1. Document");
            System.out.println("  2. Box");
            System.out.println("  3. Fragile");
            int choice = promptInt("Enter parcel type (1-3): ");
            if (choice >= 1 && choice <= 3) {
                return choice;
            }
            System.out.println("Invalid parcel type! Please select 1, 2, or 3.");
            System.out.println();
        }
    }

    private static DeliveryServiceStrategy promptDeliveryServiceStrategy() {
        while (true) {
            System.out.println("Select Delivery Service:");
            System.out.println("  1. Standard");
            System.out.println("  2. Express");
            System.out.println("  3. Same Day");
            int choice = promptInt("Enter delivery service (1-3): ");
            switch (choice) {
                case 1 -> {
                    return new StandardDeliveryStrategy();
                }
                case 2 -> {
                    return new ExpressDeliveryStrategy();
                }
                case 3 -> {
                    return new SameDayDeliveryStrategy();
                }
                default -> {
                    System.out.println("Invalid delivery service! Please select 1, 2, or 3.");
                    System.out.println();
                }
            }
        }
    }

    private static String promptValidTrackingNumber() {
        while (true) {
            System.out.print("Enter Tracking Number (e.g., ABC0001): ");
            String input = scanner.nextLine().trim();
            try {
                parcelManager.validateUniqueTrackingNumber(input);
                return input;
            } catch (InvalidParcelDataException | DuplicateTrackingNumberException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static String promptValidReceiverName() {
        while (true) {
            System.out.print("Enter Receiver Name: ");
            String input = scanner.nextLine();
            try {
                Parcel.validateReceiverName(input);
                return input.trim();
            } catch (InvalidParcelDataException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static double promptValidWeight(double maxWeight) {
        while (true) {
            double weight = promptDouble(String.format("Enter Weight in kg (max %.1f kg): ", maxWeight));
            try {
                Parcel.validateWeight(weight, maxWeight);
                return weight;
            } catch (InvalidParcelDataException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static int promptValidPages() {
        while (true) {
            int pages = promptInt("Enter Number of Pages (1-100): ");
            try {
                DocumentParcel.validateNumberOfPages(pages);
                return pages;
            } catch (InvalidParcelDataException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static int promptValidDimension(String prompt, String dimensionName) {
        while (true) {
            int value = promptInt(prompt);
            try {
                BoxParcel.validateDimension(value, dimensionName);
                return value;
            } catch (InvalidParcelDataException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static String promptValidHandlingNote() {
        while (true) {
            System.out.print("Enter Handling Note: ");
            String input = scanner.nextLine();
            try {
                FragileParcel.validateHandlingNote(input);
                return input.trim();
            } catch (InvalidParcelDataException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static int promptInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a whole number.");
            }
        }
    }

    private static double promptDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid decimal number.");
            }
        }
    }
}
