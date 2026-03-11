package gui;

import javafx.beans.property.SimpleStringProperty;
import model.Accomodation;

public class AccommodationRow {
    
    private SimpleStringProperty accommodationNumber;
    private SimpleStringProperty accommodationType;
    private SimpleStringProperty accommodationOccupancy;
    private SimpleStringProperty accommodationAvailability;
    private SimpleStringProperty cleaningStatus;
    private SimpleStringProperty numOfGuests;
    private SimpleStringProperty breakfastRequired;
    
    // Stores the real Accomodation object so that when a row is selected
    // in the table, we can retrieve the full object and call its methods
    // (e.g. getBooking(), getCleaningStatus()) to update the info panels
    private Accomodation accomodation;
    
    // Updated constructor — now accepts the real Accomodation object as the
    // first parameter so it can be stored and retrieved later via getAccommodation()
    AccommodationRow(Accomodation accomodation,
                    int accommodationNumber,
                    String accommodationType,
                    String accommodationOccupancy,
                    String accommodationAvailability,
                    String cleaningStatus,
                    int numOfGuests,
                    String breakfastRequired)
    {
        // Store the real Accomodation object for later retrieval on row selection
        this.accomodation = accomodation;
        
        this.accommodationNumber = new SimpleStringProperty(Integer.toString(accommodationNumber));
        this.accommodationType = new SimpleStringProperty(accommodationType);
        this.accommodationOccupancy = new SimpleStringProperty(accommodationOccupancy);
        this.accommodationAvailability = new SimpleStringProperty(accommodationAvailability);
        this.cleaningStatus = new SimpleStringProperty(cleaningStatus);
        this.numOfGuests = new SimpleStringProperty(Integer.toString(numOfGuests));
        this.breakfastRequired = new SimpleStringProperty(breakfastRequired);
    }


    /* =========================
       PROPERTY METHODS
       ========================= */

    // JavaFX PropertyValueFactory looks for xxxProperty() methods to bind
    // table columns to data. Without these, the columns may not display
    // correctly even if the getter methods are present and correctly named.

    public SimpleStringProperty accommodationNumberProperty() {
        return accommodationNumber;
    }

    public SimpleStringProperty accommodationTypeProperty() {
        return accommodationType;
    }

    // This is the key method that fixes the Occupancy column not displaying.
    // PropertyValueFactory looks for accommodationOccupancyProperty() to bind
    // the column, returning the SimpleStringProperty directly.
    public SimpleStringProperty accommodationOccupancyProperty() {
        return accommodationOccupancy;
    }

    public SimpleStringProperty accommodationAvailabilityProperty() {
        return accommodationAvailability;
    }

    public SimpleStringProperty cleaningStatusProperty() {
        return cleaningStatus;
    }

    public SimpleStringProperty numOfGuestsProperty() {
        return numOfGuests;
    }

    public SimpleStringProperty breakfastRequiredProperty() {
        return breakfastRequired;
    }


    /* =========================
       GETTERS
       ========================= */

    // Getting the real Accomodation object back from a selected table row.
    // This is used in accommodationSelected() in the controller to update
    // the Accommodation Info and Reception panels when a row is clicked.
    public Accomodation getAccommodation()
    {
        return accomodation;
    }
    
    // Getting Accommodation Number
    public String getAccommodationNumber()
    {
        return accommodationNumber.get();
    }
    
    // Getting Accommodation Type
    public String getAccommodationType()
    {
        return accommodationType.get();
    }
    
    // Getting Accommodation Occupancy
    public String getAccommodationOccupancy()
    {
        return accommodationOccupancy.get();
    }
    
    // Getting Accommodation Availability
    public String getAccommodationAvailability()
    {
        return accommodationAvailability.get();
    }
    
    // Getting Cleaning Status
    public String getCleaningStatus()
    {
        return cleaningStatus.get();
    }
    
    // Getting Number of Guests
    public String getNumOfGuests()
    {
        return numOfGuests.get();
    }
    
    // Getting Breakfast Required
    public String getBreakfastRequired()
    {
        return breakfastRequired.get();
    }
}