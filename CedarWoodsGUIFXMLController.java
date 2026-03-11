package gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import model.Accomodation;
import model.AccomodationType;
import model.Area;
import model.Booking;
import model.CleaningStatus;
import model.CedarWoodsSystem;
import model.Guest;

public class CedarWoodsGUIFXMLController implements Initializable {

    /* =========================
       AREA SECTION
       ========================= */

    private ObservableList<AccommodationRow> tableData = FXCollections.observableArrayList();

    // Changed from ChoiceBox<String> to ChoiceBox<Area> so that we get real
    // Area objects back from getValue(), allowing us to call area methods directly
    @FXML private ChoiceBox<Area> cbArea;
    @FXML private TextField txtAreaDescription;
    @FXML private TextField txtNumBreakfasts;
    @FXML private TextField txtNumRequireCleaning;

    /* =========================
       TABLE
       ========================= */

    @FXML private TableView<AccommodationRow> tblAccommodations;

    /* =========================
       ACCOMMODATION INFO
       ========================= */

    @FXML private TextField AccommType;
    @FXML private TextField AccommNum;
    @FXML private TextField Accommodates;
    @FXML private TextField PricePerNight;
    // Changed from TextField to TextArea so long descriptions wrap onto
    // the next line rather than continuing on a single line
    @FXML private TextArea Accomm_Description;

    /* =========================
       CLEANING / MAINTENANCE
       ========================= */

    @FXML private ChoiceBox<String> cbCleaningStatus;

    /* =========================
       RECEPTION DETAILS
       ========================= */

    @FXML private TextField First_Name;
    @FXML private TextField Last_Name;
    @FXML private TextField Tel_Num;
    @FXML private TextField CheckInDate;
    @FXML private TextField NoOfGuests;
    @FXML private TextField Num_Nights;

    @FXML private CheckBox CheckBox_Breakfast;

    /* =========================
       BUTTONS
       ========================= */

    @FXML private Button btnCheckIn;
    @FXML private Button btnCheckOut;

    /* =========================
       INITIALIZATION
       ========================= */

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // //* Cleaning Status Choice Box Options *//
        // This remains as Strings since cleaning status is a fixed set of labels
        cbCleaningStatus.getItems().addAll(
                "Clean",
                "Dirty",
                "Maintenance"
        );

        // //* Table Columns — widths updated to match the final GUI layout *//

        // Accommodation Number Column
        TableColumn accommNoCol = new TableColumn("Accommodation Number");
        accommNoCol.setMinWidth(160);

        // Accommodation Type Column
        TableColumn typeCol = new TableColumn("Accommodation Type");
        typeCol.setMinWidth(150);

        // Accommodation Occupancy Column
        TableColumn occupCol = new TableColumn("Occupancy");
        occupCol.setMinWidth(120);

        // Accommodation Availability Column
        TableColumn availCol = new TableColumn("Availability");
        availCol.setMinWidth(100);

        // Cleaning Status Column
        TableColumn statCol = new TableColumn("Cleaning Status");
        statCol.setMinWidth(100);

        // Number of Guests Column
        TableColumn guestsCol = new TableColumn("Guests");
        guestsCol.setMinWidth(50);

        // Breakfast Column
        TableColumn breakfastCol = new TableColumn("Breakfast");
        breakfastCol.setMinWidth(82);

        tblAccommodations.getColumns().addAll(accommNoCol, typeCol, occupCol, availCol, statCol, guestsCol, breakfastCol);

        // Bind each column to the matching property name in AccommodationRow
        accommNoCol.setCellValueFactory(new PropertyValueFactory<AccommodationRow, String>("accommodationNumber"));
        typeCol.setCellValueFactory(new PropertyValueFactory<AccommodationRow, String>("accommodationType"));
        occupCol.setCellValueFactory(new PropertyValueFactory<AccommodationRow, String>("accommodationOccupancy"));
        availCol.setCellValueFactory(new PropertyValueFactory<AccommodationRow, String>("accommodationAvailability"));
        statCol.setCellValueFactory(new PropertyValueFactory<AccommodationRow, String>("cleaningStatus"));
        guestsCol.setCellValueFactory(new PropertyValueFactory<AccommodationRow, String>("numOfGuests"));
        breakfastCol.setCellValueFactory(new PropertyValueFactory<AccommodationRow, String>("breakfastRequired"));

        tblAccommodations.setItems(tableData);

        // //* Area Choice Box - Load real Area objects from CedarWoodsSystem *//
        // We use the Singleton getInstance() to get the one shared system instance,
        // then load all areas into an ObservableList and bind it to the ChoiceBox
        CedarWoodsSystem system = CedarWoodsSystem.getInstance();
        ObservableList<Area> areaData = FXCollections.observableArrayList();
        for (Area area : system.getAreas()) {
            areaData.add(area);
        }

        cbArea.setItems(areaData);

        // Default to the first area in the list on startup
        cbArea.setValue(areaData.get(0));

        // Wire up the event handler for when the user changes the selected area.
        // setOnAction is not available in Scene Builder so it must be set here in code.
        cbArea.setOnAction(this::cbAreaOnAction);

        // Wire up the cleaning status choice box event handler.
        // Fires whenever the cleaning staff changes the status of an accommodation.
        cbCleaningStatus.setOnAction(this::cbCleaningStatusOnAction);

        // Populate the table and statistics for the default area immediately on startup
        showAreaData(areaData.get(0));

        // //* Table Row Selection Listener *//
        // When the user clicks a row in the table, accommodationSelected() is called.
        // The listener fires whenever the selected item property changes.
        // newSelection is the newly clicked AccommodationRow, oldSelection is the previous one.
        tblAccommodations.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    accommodationSelected(newSelection);
                }
            });

        // Disable both buttons on startup since no accommodation is selected yet.
        // They will be enabled/disabled automatically when a row is selected.
        btnCheckIn.setDisable(true);
        btnCheckOut.setDisable(true);
    }

    /* =========================
       AREA CHOICE BOX EVENT HANDLER
       ========================= */

    // Fired whenever the user selects a different area from the ChoiceBox.
    // Retrieves the selected Area object and refreshes the description,
    // statistics, and table to match the chosen area.
    private void cbAreaOnAction(ActionEvent event) {
        Area area = cbArea.getValue();
        if (area == null) return;

        showAreaData(area);

        // Disable both buttons when switching area since no row will be selected
        btnCheckIn.setDisable(true);
        btnCheckOut.setDisable(true);
    }

    /* =========================
       CLEANING STATUS CHOICE BOX EVENT HANDLER
       ========================= */

    // Fired whenever the cleaning staff changes the cleaning status ChoiceBox.
    // Updates the real accommodation object, refreshes the table and statistics.
    private void cbCleaningStatusOnAction(ActionEvent event) {

        // Step 1 — Get the currently selected row in the table.
        // If no row is selected, do nothing — the user may just be
        // browsing the choice box without having selected an accommodation.
        AccommodationRow selectedRow = tblAccommodations.getSelectionModel().getSelectedItem();
        if (selectedRow == null) return;

        // Step 2 — Get the selected cleaning status string from the choice box
        String selectedStatus = cbCleaningStatus.getValue();
        if (selectedStatus == null) return;

        // Step 3 — Convert the string to the CleaningStatus enum value
        CleaningStatus newStatus;
        switch (selectedStatus) {
            case "Clean":       newStatus = CleaningStatus.CLEAN;       break;
            case "Dirty":       newStatus = CleaningStatus.DIRTY;       break;
            case "Maintenance": newStatus = CleaningStatus.MAINTENANCE; break;
            default: return;
        }

        // Step 4 — Update the actual accommodation object with the new status
        Accomodation accommodation = selectedRow.getAccommodation();
        accommodation.setCleaningStatus(newStatus);

        // Step 5 — Remember the selected index so we can re-select
        // the same row after the table refreshes
        int selectedIndex = tblAccommodations.getSelectionModel().getSelectedIndex();

        // Step 6 — Refresh the table and statistics to reflect the change.
        // This updates the Status column and the Number Require Cleaning field.
        Area area = cbArea.getValue();
        showAreaData(area);

        // Step 7 — Re-select the same row so the info panels stay populated
        tblAccommodations.getSelectionModel().select(selectedIndex);

        // Step 8 — Update button states based on the new cleaning status.
        // If the accommodation is now Clean and unoccupied, Check In is enabled.
        // If it is Dirty or Maintenance, both buttons remain disabled.
        AccommodationRow reselectedRow = tblAccommodations.getSelectionModel().getSelectedItem();
        if (reselectedRow != null) {
            Accomodation reselectedAcc = reselectedRow.getAccommodation();
            boolean isAvailable = reselectedAcc.getBooking() == null
                    && reselectedAcc.getCleaningStatus() == CleaningStatus.CLEAN;
            boolean isOccupied = reselectedAcc.getBooking() != null;
            btnCheckIn.setDisable(!isAvailable);
            btnCheckOut.setDisable(!isOccupied);
        }
    }

    /* =========================
       AREA DATA HELPER
       ========================= */

    // Populates the table and updates the area description and statistics
    // text fields based on the given Area object.
    private void showAreaData(Area area) {

        // Clear the existing table rows before repopulating
        tableData.clear();

        // Update area description text field
        txtAreaDescription.setText(area.getAreaDescription());

        // Update area statistics text fields using the implemented Area methods
        txtNumBreakfasts.setText(Integer.toString(area.getTotalBreakfastsRequired()));
        txtNumRequireCleaning.setText(Integer.toString(area.calculateNumOfCleaning()));

        // Loop through every accommodation in the selected area and build a table row.
        // The real Accomodation object is passed as the first argument to AccommodationRow
        // so it can be retrieved later when the row is clicked.
        for (Accomodation acc : area.getAccomodations()) {

            // Occupancy: Occupied if there is an active booking, otherwise Unoccupied
            boolean occupied = acc.getBooking() != null;
            String occupancy = occupied ? "Occupied" : "Unoccupied";

            // Availability: Unavailable if occupied OR if cleaning status is Dirty/Maintenance.
            // Per the spec, accommodation must be Clean and unoccupied to be Available.
            boolean dirty = acc.getCleaningStatus() == CleaningStatus.DIRTY
                    || acc.getCleaningStatus() == CleaningStatus.MAINTENANCE;
            String availability = (occupied || dirty) ? "Unavailable" : "Available";

            // Cleaning status - uses the enum's toString() which returns the display label
            String cleaning = acc.getCleaningStatus().toString();

            // Number of guests is 0 if unoccupied, otherwise taken from the booking
            int numGuests = occupied ? acc.getBooking().getNumberOfGuests() : 0;

            // Breakfast: only Yes if there is a booking and breakfast was requested
            String breakfast = "No";
            if (occupied && acc.getBooking().getIsBreakfastRequired()) {
                breakfast = "Yes";
            }

            // Add a new row — the real Accomodation object is passed first so it
            // can be retrieved in accommodationSelected() when the row is clicked
            tableData.add(new AccommodationRow(
                    acc,
                    acc.getAccomodationID(),
                    acc.getAccomodationType().toString(),
                    occupancy,
                    availability,
                    cleaning,
                    numGuests,
                    breakfast
            ));
        }
    }

    /* =========================
       TABLE ROW SELECTION HANDLER
       ========================= */

    // Fired when the user clicks a row in the accommodation table.
    // Always updates the Accommodation Info fields with the selected accommodation's details.
    // If the accommodation has an active booking, the reception fields are populated.
    // If not, the reception fields are cleared.
    private void accommodationSelected(AccommodationRow newSelection) {

        // Retrieve the real Accomodation object stored inside the selected row
        Accomodation accommodation = newSelection.getAccommodation();

        // --- Accommodation Info section (always shown, read-only) ---
        AccommType.setText(accommodation.getAccomodationType().toString());
        AccommNum.setText(Integer.toString(accommodation.getAccomodationID()));
        PricePerNight.setText(Float.toString(accommodation.getPricePerNight()));

        // Capacity is determined by accommodation type per the system spec (Table 1)
        int capacity = getCapacityForType(accommodation.getAccomodationType());
        Accommodates.setText(Integer.toString(capacity));

        // Display the accommodation description in the Accomm_Description text area.
        // This is retrieved from the description stored in the Accomodation object,
        // which was set when the accommodation was created in CedarWoodsSystem.populate()
        Accomm_Description.setText(accommodation.getDescription());

        // Update the cleaning status choice box to reflect the selected accommodation
        cbCleaningStatus.setValue(accommodation.getCleaningStatus().toString());

        // --- Reception section ---
        // If the accommodation has an active booking, display the guest details.
        // Otherwise clear all reception fields since no guest is checked in.
        if (accommodation.getBooking() != null) {
            displayCheckinDetails(accommodation.getBooking());
        } else {
            clearCheckinDetails();
        }

        // Update button states based on the selected accommodation's status.
        // Check In is only enabled if the accommodation is Available (Clean and Unoccupied).
        // Check Out is only enabled if the accommodation is Occupied.
        boolean isAvailable = accommodation.getBooking() == null
                && accommodation.getCleaningStatus() == CleaningStatus.CLEAN;
        boolean isOccupied = accommodation.getBooking() != null;

        btnCheckIn.setDisable(!isAvailable);
        btnCheckOut.setDisable(!isOccupied);
    }

    /* =========================
       ACCOMMODATION CAPACITY HELPER
       ========================= */

    // Returns the maximum number of guests for a given accommodation type,
    // as defined in Table 1 of the system specification brief.
    // Cabin=4, Geodesic Dome=2, Yurt=2, Airstream=4
    private int getCapacityForType(AccomodationType type) {
        switch (type) {
            case CABIN:        return 4;
            case GEODESICDOME: return 2;
            case YURT:         return 2;
            case AIRSTREAM:    return 4;
            default:           return 0;
        }
    }

    /* =========================
       RECEPTION DISPLAY HELPERS
       ========================= */

    // Populates all reception text fields with details from an existing booking.
    // Called when a row is selected and the accommodation has an active booking.
    private void displayCheckinDetails(Booking booking) {
        First_Name.setText(booking.getGuest().getFirstName());
        Last_Name.setText(booking.getGuest().getLastName());
        Tel_Num.setText(booking.getGuest().getTelephoneNumber());
        NoOfGuests.setText(Integer.toString(booking.getNumberOfGuests()));
        Num_Nights.setText(Integer.toString(booking.getNumberOfNights()));
        CheckBox_Breakfast.setSelected(booking.getIsBreakfastRequired());

        // Format and display the check-in date if one exists on the booking
        if (booking.getCheckInDate() != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yy");
            CheckInDate.setText(sdf.format(booking.getCheckInDate()));
        } else {
            CheckInDate.setText("");
        }
    }

    // Clears all reception fields when an unoccupied accommodation is selected.
    // This ensures no stale guest data is shown from a previously selected row.
    private void clearCheckinDetails() {
        First_Name.setText("");
        Last_Name.setText("");
        Tel_Num.setText("");
        NoOfGuests.setText("");
        CheckInDate.setText("");
        Num_Nights.setText("");
        CheckBox_Breakfast.setSelected(false);
    }

    /* =========================
       BUTTON ACTIONS
       ========================= */

    @FXML
    private void checkinClicked(ActionEvent event) {

        // Step 1 — Validate a row is selected in the table
        AccommodationRow selectedRow = tblAccommodations.getSelectionModel().getSelectedItem();
        if (selectedRow == null) {
            showAlert("Check In Error", "Please select an accommodation from the table first.");
            return;
        }

        // Step 2 — Retrieve the real accommodation object from the selected row
        Accomodation accommodation = selectedRow.getAccommodation();

        // Step 3 — Validate the accommodation is available for check in.
        // Per the spec, it must be Clean and not already Occupied.
        if (accommodation.getBooking() != null) {
            showAlert("Check In Error", "This accommodation is already occupied.");
            return;
        }
        if (accommodation.getCleaningStatus() != CleaningStatus.CLEAN) {
            showAlert("Check In Error", "This accommodation is not available. It must be Clean before a guest can check in.");
            return;
        }

        // Step 4 — Read and validate all reception form fields.
        // Every field must be filled in before proceeding.
        String firstName = First_Name.getText().trim();
        String lastName = Last_Name.getText().trim();
        String telNum = Tel_Num.getText().trim();
        String checkInDateStr = CheckInDate.getText().trim();
        String numGuestsStr = NoOfGuests.getText().trim();
        String numNightsStr = Num_Nights.getText().trim();
        boolean breakfastRequired = CheckBox_Breakfast.isSelected();

        if (firstName.isEmpty() || lastName.isEmpty() || telNum.isEmpty()
                || checkInDateStr.isEmpty() || numGuestsStr.isEmpty() || numNightsStr.isEmpty()) {
            showAlert("Check In Error", "Please fill in all guest details before checking in.");
            return;
        }

        // Step 5 — Parse number of guests and number of nights to integers.
        // Show an error if the user has entered non-numeric values.
        int numGuests;
        int numNights;
        try {
            numGuests = Integer.parseInt(numGuestsStr);
            numNights = Integer.parseInt(numNightsStr);
        } catch (NumberFormatException e) {
            showAlert("Check In Error", "Number of Guests and Number of Nights must be valid numbers.");
            return;
        }

        // Step 6 — Validate number of guests does not exceed the accommodation capacity.
        // Per the spec, an error must be shown if this limit is exceeded.
        int capacity = getCapacityForType(accommodation.getAccomodationType());
        if (numGuests > capacity) {
            showAlert("Check In Error", "The number of guests (" + numGuests + ") exceeds the maximum capacity of this accommodation (" + capacity + ").");
            return;
        }

        // Step 7 — Parse the check in date using the expected format dd-MM-yy.
        // Show an error if the format is incorrect.
        LocalDate checkInDate;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
            checkInDate = LocalDate.parse(checkInDateStr, formatter);
        } catch (DateTimeParseException e) {
            showAlert("Check In Error", "Please enter the check in date in the format dd-MM-yy (e.g. 05-10-24).");
            return;
        }

        // Step 8 — All validation passed. Construct the Guest and Booking objects
        // using the data entered by the owner in the reception fields.
        Guest guest = new Guest(firstName, lastName, telNum);
        Booking booking = new Booking(guest,
                java.sql.Date.valueOf(checkInDate),
                numGuests,
                breakfastRequired,
                numNights);

        // Step 9 — Call checkin() on the accommodation to associate it with the booking
        accommodation.checkin(booking);

        // Step 10 — Remember the selected row index so we can re-select it after refresh
        int selectedIndex = tblAccommodations.getSelectionModel().getSelectedIndex();

        // Step 11 — Refresh the table and statistics to reflect the new booking.
        // The row will now show as Occupied and Unavailable.
        Area area = cbArea.getValue();
        showAreaData(area);

        // Step 12 — Re-select the same row so the info panels stay populated
        tblAccommodations.getSelectionModel().select(selectedIndex);

        // Step 13 — After check in, this accommodation is now Occupied so
        // disable Check In and enable Check Out
        btnCheckIn.setDisable(true);
        btnCheckOut.setDisable(false);
    }

    @FXML
    private void checkoutClicekd(ActionEvent event) {

        // Step 1 — Validate a row is selected in the table
        AccommodationRow selectedRow = tblAccommodations.getSelectionModel().getSelectedItem();
        if (selectedRow == null) {
            showAlert("Check Out Error", "Please select an accommodation from the table first.");
            return;
        }

        // Step 2 — Retrieve the real accommodation object from the selected row
        Accomodation accommodation = selectedRow.getAccommodation();

        // Step 3 — Validate the accommodation is actually occupied before checking out
        if (accommodation.getBooking() == null) {
            showAlert("Check Out Error", "This accommodation has no guest to check out.");
            return;
        }

        // Step 4 — Call checkout() to remove the booking from the accommodation
        accommodation.checkout();

        // Step 5 — Per the spec, after checkout the accommodation must automatically
        // be set to Dirty to indicate it needs cleaning before the next guest
        accommodation.setCleaningStatus(CleaningStatus.DIRTY);

        // Step 6 — Clear the reception fields since the guest has now departed
        clearCheckinDetails();

        // Step 7 — Refresh the table and statistics to reflect the checkout.
        // The row will now show as Unoccupied, Unavailable, and Dirty.
        Area area = cbArea.getValue();
        showAreaData(area);

        // Step 8 — After check out, accommodation is Dirty so both buttons
        // should be disabled until cleaning staff mark it as Clean
        btnCheckIn.setDisable(true);
        btnCheckOut.setDisable(true);
    }

    /* =========================
       ERROR ALERT HELPER
       ========================= */

    // Displays a JavaFX error alert dialog with a given title and message.
    // Platform.runLater() ensures the alert runs on the JavaFX Application Thread
    // which is required for all UI updates in JavaFX.
    private void showAlert(String title, String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Cedar Woods System");
                alert.setHeaderText(title);
                alert.setContentText(message);
                alert.showAndWait();
            }
        });
    }
}