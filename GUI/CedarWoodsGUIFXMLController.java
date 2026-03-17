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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

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
    @FXML
    private ChoiceBox<Area> cbArea;
    @FXML
    private TextArea Accomm_Description;
    @FXML
    private TextField txtNumBreakfasts;
    @FXML
    private TextField txtNumRequireCleaning;
    @FXML
    private TextArea txtAreaDescription;

    /* =========================
       TABLE
       ========================= */
    @FXML
    private TableView<AccommodationRow> tblAccommodations;

    // FXML-injected column references — wired to the fx:id attributes in the FXML file
    @FXML
    private TableColumn<AccommodationRow, String> colAccomNum;
    @FXML
    private TableColumn<AccommodationRow, String> colAccomType;
    @FXML
    private TableColumn<AccommodationRow, String> colOccupancy;
    @FXML
    private TableColumn<AccommodationRow, String> colAvailability;
    @FXML
    private TableColumn<AccommodationRow, String> colCleaning;
    @FXML
    private TableColumn<AccommodationRow, String> colGuests;
    @FXML
    private TableColumn<AccommodationRow, String> colBreakfast;

    /* =========================
       ACCOMMODATION INFO
       ========================= */
    @FXML
    private TextField AccommType;
    @FXML
    private TextField AccommNum;
    @FXML
    private TextField Accommodates;
    @FXML
    private TextField PricePerNight;

    /* =========================
       CLEANING / MAINTENANCE
       ========================= */
    @FXML
    private ChoiceBox<String> cbCleaningStatus;

    /* =========================
       RECEPTION DETAILS
       ========================= */
    @FXML
    private TextField First_Name;
    @FXML
    private TextField Last_Name;
    @FXML
    private TextField Tel_Num;
    // Changed from TextField to DatePicker so the user selects a date
    // from a calendar popup instead of typing it manually
    @FXML
    private DatePicker CheckInDate;
    @FXML
    private TextField NoOfGuests;
    @FXML
    private TextField Num_Nights;

    @FXML
    private CheckBox CheckBox_Breakfast;

    /* =========================
       BUTTONS
       ========================= */
    @FXML
    private Button btnCheckIn;
    @FXML
    private Button btnCheckOut;

    /* =========================
       INITIALIZATION
       ========================= */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Accomm_Description.setWrapText(true);

        // Disable manual typing in the DatePicker
        CheckInDate.setEditable(false);

        // //* Cleaning Status Choice Box Options *//
        cbCleaningStatus.getItems().addAll(
                "Clean",
                "Dirty",
                "Maintenance"
        );

        // //* Table Columns — wire up FXML columns directly *//
        colAccomNum.setCellValueFactory(new PropertyValueFactory<>("accommodationNumber"));
        colAccomType.setCellValueFactory(new PropertyValueFactory<>("accommodationType"));
        colOccupancy.setCellValueFactory(new PropertyValueFactory<>("accommodationOccupancy"));
        colAvailability.setCellValueFactory(new PropertyValueFactory<>("accommodationAvailability"));
        colCleaning.setCellValueFactory(new PropertyValueFactory<>("cleaningStatus"));
        colGuests.setCellValueFactory(new PropertyValueFactory<>("numOfGuests"));
        colBreakfast.setCellValueFactory(new PropertyValueFactory<>("breakfastRequired"));

        tblAccommodations.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblAccommodations.setItems(tableData);

        // Bind table height to number of rows to prevent phantom empty rows
        tblAccommodations.setFixedCellSize(28);
        tblAccommodations.prefHeightProperty().bind(
                javafx.beans.binding.Bindings.size(tblAccommodations.getItems())
                        .multiply(28)
                        .add(42)
        );

        // //* Area Choice Box - Load real Area objects from CedarWoodsSystem *//
        CedarWoodsSystem system = CedarWoodsSystem.getInstance();
        ObservableList<Area> areaData = FXCollections.observableArrayList();
        for (Area area : system.getAreas()) {
            areaData.add(area);
        }

        cbArea.setItems(areaData);
        cbArea.setValue(areaData.get(0));
        cbArea.setOnAction(this::cbAreaOnAction);
        cbCleaningStatus.setOnAction(this::cbCleaningStatusOnAction);

        showAreaData(areaData.get(0));

        // //* Table Row Selection Listener *//
        tblAccommodations.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        accommodationSelected(newSelection);
                    }
                });

        // Both buttons start enabled on startup.
        // If clicked without a row selected, the validation inside each
        // handler will show an appropriate error message to the user.
    }

    /* =========================
       AREA CHOICE BOX EVENT HANDLER
       ========================= */
    private void cbAreaOnAction(ActionEvent event) {
        Area area = cbArea.getValue();
        if (area == null) {
            return;
        }

        showAreaData(area);

        // Clear reception fields when switching area so no stale
        // guest data from the previously viewed area remains visible
        clearCheckinDetails();

        // Disable both buttons when switching area since no row will be selected
        btnCheckIn.setDisable(true);
        btnCheckOut.setDisable(true);
    }

    /* =========================
       CLEANING STATUS CHOICE BOX EVENT HANDLER
       ========================= */
    private void cbCleaningStatusOnAction(ActionEvent event) {

        // If no row is selected, do nothing
        AccommodationRow selectedRow = tblAccommodations.getSelectionModel().getSelectedItem();
        if (selectedRow == null) {
            return;
        }

        String selectedStatus = cbCleaningStatus.getValue();
        if (selectedStatus == null) {
            return;
        }

        // Convert the string to the CleaningStatus enum value
        CleaningStatus newStatus;
        switch (selectedStatus) {
            case "Clean":
                newStatus = CleaningStatus.CLEAN;
                break;
            case "Dirty":
                newStatus = CleaningStatus.DIRTY;
                break;
            case "Maintenance":
                newStatus = CleaningStatus.MAINTENANCE;
                break;
            default:
                return;
        }

        // Update the actual accommodation object with the new status
        Accomodation accommodation = selectedRow.getAccommodation();
        accommodation.setCleaningStatus(newStatus);

        // Remember the selected index so we can re-select after refresh
        int selectedIndex = tblAccommodations.getSelectionModel().getSelectedIndex();

        // Refresh the table and statistics to reflect the change
        Area area = cbArea.getValue();
        showAreaData(area);

        // Re-select the same row so the info panels stay populated
        tblAccommodations.getSelectionModel().select(selectedIndex);

        // Check In is always enabled when a row is selected.
        // Check Out is only enabled when the accommodation is occupied.
        AccommodationRow reselectedRow = tblAccommodations.getSelectionModel().getSelectedItem();
        if (reselectedRow != null) {
            Accomodation reselectedAcc = reselectedRow.getAccommodation();
            boolean isOccupied = reselectedAcc.getBooking() != null;
            btnCheckIn.setDisable(false);
            btnCheckOut.setDisable(!isOccupied);
        }
    }

    /* =========================
       AREA DATA HELPER
       ========================= */
    private void showAreaData(Area area) {

        tableData.clear();

        txtAreaDescription.setText(area.getAreaDescription());
        txtNumBreakfasts.setText(Integer.toString(area.getTotalBreakfastsRequired()));
        txtNumRequireCleaning.setText(Integer.toString(area.calculateNumOfCleaning()));

        for (Accomodation acc : area.getAccomodations()) {

            boolean occupied = acc.getBooking() != null;
            String occupancy = occupied ? "Occupied" : "Unoccupied";

            boolean dirty = acc.getCleaningStatus() == CleaningStatus.DIRTY
                    || acc.getCleaningStatus() == CleaningStatus.MAINTENANCE;
            String availability = (occupied || dirty) ? "Unavailable" : "Available";

            String cleaning = acc.getCleaningStatus().toString();

            int numGuests = occupied ? acc.getBooking().getNumberOfGuests() : 0;

            String breakfast = "No";
            if (occupied && acc.getBooking().getIsBreakfastRequired()) {
                breakfast = "Yes";
            }

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
    private void accommodationSelected(AccommodationRow newSelection) {

        Accomodation accommodation = newSelection.getAccommodation();

        AccommType.setText(accommodation.getAccomodationType().toString());
        AccommNum.setText(Integer.toString(accommodation.getAccomodationID()));
        PricePerNight.setText(Float.toString(accommodation.getPricePerNight()));

        int capacity = getCapacityForType(accommodation.getAccomodationType());
        Accommodates.setText(Integer.toString(capacity));

        setAccommDescription(accommodation.getDescription());

        cbCleaningStatus.setValue(accommodation.getCleaningStatus().toString());

        if (accommodation.getBooking() != null) {
            displayCheckinDetails(accommodation.getBooking());
        } else {
            clearCheckinDetails();
        }

        // Check In is always enabled when a row is selected so the owner
        // can update guest details on an already occupied accommodation.
        // Check Out is only enabled when the accommodation is occupied.
        boolean isOccupied = accommodation.getBooking() != null;
        btnCheckIn.setDisable(false);
        btnCheckOut.setDisable(!isOccupied);
    }

    /* =========================
       ACCOMMODATION CAPACITY HELPER
       ========================= */
    private int getCapacityForType(AccomodationType type) {
        switch (type) {
            case CABIN:
                return 4;
            case GEODESICDOME:
                return 2;
            case YURT:
                return 2;
            case AIRSTREAM:
                return 4;
            default:
                return 0;
        }
    }

    /* =========================
       RECEPTION DISPLAY HELPERS
       ========================= */
    private void displayCheckinDetails(Booking booking) {
        First_Name.setText(booking.getGuest().getFirstName());
        Last_Name.setText(booking.getGuest().getLastName());
        Tel_Num.setText(booking.getGuest().getTelephoneNumber());
        NoOfGuests.setText(Integer.toString(booking.getNumberOfGuests()));
        Num_Nights.setText(Integer.toString(booking.getNumberOfNights()));
        CheckBox_Breakfast.setSelected(booking.getIsBreakfastRequired());

    // FIXED: use java.sql.Date.toLocalDate() instead of toInstant()
    java.sql.Date sqlDate = (java.sql.Date) booking.getCheckInDate();
    if (sqlDate != null) {
        CheckInDate.setValue(sqlDate.toLocalDate());
    } else {
        CheckInDate.setValue(null);
    }
    }

    // Clears all reception fields when an unoccupied accommodation is selected.
    // This ensures no stale guest data is shown from a previously selected row.
    private void clearCheckinDetails() {
        First_Name.setText("");
        Last_Name.setText("");
        Tel_Num.setText("");
        NoOfGuests.setText("");
        // Set DatePicker to null to clear the selected date
        CheckInDate.setValue(null);
        Num_Nights.setText("");
        CheckBox_Breakfast.setSelected(false);
    }

    /* =========================
       BUTTON ACTIONS
       ========================= */
    @FXML
    private void checkinClicked(ActionEvent event) {

        // Step 1 — Validate a row is selected
        AccommodationRow selectedRow = tblAccommodations.getSelectionModel().getSelectedItem();
        if (selectedRow == null) {
            showAlert("Check In Error", "Please select an accommodation from the table first.");
            return;
        }

        Accomodation accommodation = selectedRow.getAccommodation();

        // NEW: Prevent check-in if accommodation is already occupied
        if (accommodation.getBooking() != null) {
            showAlert("Check In Error", "This accommodation is already occupied.");
            return;
        }

        // Step 2 — Validate the accommodation is not dirty or under maintenance.
        // We no longer block check in on occupied accommodations —
        // if already occupied, check in acts as an update to the existing booking.
        if (accommodation.getCleaningStatus() != CleaningStatus.CLEAN) {
            showAlert("Check In Error", "This accommodation is not available. It must be Clean before a guest can check in.");
            return;
        }

        // Step 3 — Read and validate all reception form fields
        String firstName = First_Name.getText().trim();
        String lastName = Last_Name.getText().trim();
        String telNum = Tel_Num.getText().trim();
        // Get the date directly from the DatePicker — no parsing needed
        LocalDate checkInDate = CheckInDate.getValue();
        String numGuestsStr = NoOfGuests.getText().trim();
        String numNightsStr = Num_Nights.getText().trim();
        boolean breakfastRequired = CheckBox_Breakfast.isSelected();

        // Prevent past dates
        if (checkInDate != null && checkInDate.isBefore(LocalDate.now())) {
            showAlert("Check In Error", "Check-in date cannot be in the past.");
            return;
        }

        if (firstName.isEmpty() || lastName.isEmpty() || telNum.isEmpty()
                || checkInDate == null || numGuestsStr.isEmpty() || numNightsStr.isEmpty()) {
            showAlert("Check In Error", "Please fill in all guest details before checking in.");
            return;
        }

        // Step 4 — Validate first and last name contain only letters, spaces, and hyphens
        if (!firstName.matches("[a-zA-Z\\s\\-]+")) {
            showAlert("Check In Error", "First name must contain letters only.");
            return;
        }
        if (!lastName.matches("[a-zA-Z\\s\\-]+")) {
            showAlert("Check In Error", "Last name must contain letters only.");
            return;
        }

        // Step 5 — Validate telephone contains only digits, spaces, + and -
        if (!telNum.matches("[0-9\\s\\+\\-]+")) {
            showAlert("Check In Error", "Telephone number must contain numbers only.");
            return;
        }

        // Step 6 — Parse number of guests and number of nights
        int numGuests;
        int numNights;
        try {
            numGuests = Integer.parseInt(numGuestsStr);
            numNights = Integer.parseInt(numNightsStr);
        } catch (NumberFormatException e) {
            showAlert("Check In Error", "Number of Guests and Number of Nights must be valid numbers.");
            return;
        }

        // Step 7 — Validate number of guests does not exceed the accommodation capacity
        int capacity = getCapacityForType(accommodation.getAccomodationType());
        if (numGuests > capacity) {
            showAlert("Check In Error", "The number of guests (" + numGuests + ") exceeds the maximum capacity of this accommodation (" + capacity + ").");
            return;
        }

        // Step 8 — Validate number of nights and guests are not negative or zero
        if (numNights < 1) {
            showAlert("Check In Error", "The number of nights must be at least 1.");
            return;
        }
        if (numGuests < 1) {
            showAlert("Check In Error", "The number of guests must be at least 1.");
            return;
        }

        // Step 9 — Construct the Guest and Booking objects
        Guest guest = new Guest(firstName, lastName, telNum);
        Booking booking = new Booking(guest,
                java.sql.Date.valueOf(checkInDate),
                numGuests,
                breakfastRequired,
                numNights);

        // Step 10 — Call checkin() on the accommodation.
        // If already occupied this acts as an update, replacing
        // the existing booking with the new details entered by the owner.
        accommodation.checkin(booking);

        // Step 11 — Remember the selected row index so we can re-select after refresh
        int selectedIndex = tblAccommodations.getSelectionModel().getSelectedIndex();

        // Step 12 — Refresh the table and statistics
        Area area = cbArea.getValue();
        showAreaData(area);

        // Step 13 — Re-select the same row so the info panels stay populated
        tblAccommodations.getSelectionModel().select(selectedIndex);

        // Step 14 — After check in, enable Check Out since accommodation is now occupied.
        // Check In stays enabled in case the owner needs to update details again.
        btnCheckIn.setDisable(false);
        btnCheckOut.setDisable(false);
    }

    @FXML
    private void checkoutClicekd(ActionEvent event) {

        // Step 1 — Validate a row is selected
        AccommodationRow selectedRow = tblAccommodations.getSelectionModel().getSelectedItem();
        if (selectedRow == null) {
            showAlert("Check Out Error", "Please select an accommodation from the table first.");
            return;
        }

        Accomodation accommodation = selectedRow.getAccommodation();

        // Step 2 — Validate the accommodation is occupied before checking out
        if (accommodation.getBooking() == null) {
            showAlert("Check Out Error", "This accommodation has no guest to check out.");
            return;
        }

        // Step 3 — Call checkout() to remove the booking
        accommodation.checkout();

        // Step 4 — Set status to Dirty after checkout as required by the spec
        accommodation.setCleaningStatus(CleaningStatus.DIRTY);

        // Step 5 — Clear the reception fields
        clearCheckinDetails();

        // Step 6 — Refresh the table and statistics
        Area area = cbArea.getValue();
        showAreaData(area);

        // Step 7 — After check out the accommodation is Dirty so disable Check Out.
        // Check In stays enabled but will be blocked by the dirty status validation.
        btnCheckIn.setDisable(false);
        btnCheckOut.setDisable(true);
    }

    /* =========================
       ERROR ALERT HELPER
       ========================= */
    private void showAlert(String title, String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Cedar Woods System");
                alert.setHeaderText(title);
                alert.setContentText(message);
                // Replace the clashing red X with a brown-toned one to match the theme
                javafx.scene.control.Label icon = new javafx.scene.control.Label("✖");
                icon.setStyle(
                        "-fx-text-fill: #8B5E20;"
                        + "-fx-font-size: 28px;"
                        + "-fx-font-weight: bold;"
                );
                alert.setGraphic(icon);
                // Apply the same stylesheet so the dialog matches the app theme
                alert.getDialogPane().getStylesheets().add(
                        getClass().getResource("app.css").toExternalForm()
                );
                alert.showAndWait();
            }
        });
    }

    /* =========================
       ACCOMM DESCRIPTION HELPER
       ========================= */
    private void setAccommDescription(String text) {
        Accomm_Description.setText(text);
    }
}
