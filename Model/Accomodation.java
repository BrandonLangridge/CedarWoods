package model;

/**
 *
 * 
 */
public class Accomodation {
    private int accomodationId;
    private float pricePerNight;
    // Description field added to store a short summary of the accommodation,
    // displayed in the Accommodation Info panel when a table row is selected
    private String description;
    private Booking booking;
    private CleaningStatus cleaningStatus;
    private AccomodationType accomodationType;
    
    // Updated constructor — now includes a description parameter so each
    // accommodation can have a meaningful summary set at creation time
    public Accomodation(int accomodationId, float pricePerNight, String description, Booking booking, CleaningStatus cleaningStatus, AccomodationType accomodationType)
    {
        this.accomodationId = accomodationId;
        this.pricePerNight = pricePerNight;
        // Store the description for display in the GUI when this accommodation is selected
        this.description = description;
        this.booking = booking;
        this.cleaningStatus = cleaningStatus;
        this.accomodationType = accomodationType;
    }
    
    public int getAccomodationID()
    {
        return this.accomodationId;
    }

    public float getPricePerNight()
    {
        return this.pricePerNight;
    }

    // Returns the description of this accommodation, used to populate
    // the Accomm_Description text field in the Accommodation Info panel
    public String getDescription()
    {
        return this.description;
    }

    public Booking getBooking()
    {
        return this.booking;
    }

    public CleaningStatus getCleaningStatus()
    {
        return this.cleaningStatus;
    }

    // Setter for cleaning status — needed so the controller can automatically
    // set the accommodation to Dirty after a guest checks out, as required
    // by the system specification
    public void setCleaningStatus(CleaningStatus cleaningStatus)
    {
        this.cleaningStatus = cleaningStatus;
    }

    public AccomodationType getAccomodationType()
    {
        return this.accomodationType;
    }
    
    // Associates the accommodation with a guest booking on check in
    public void checkin(Booking booking)
    {
        this.booking = booking;
    }
    
    // Removes the guest booking from the accommodation on check out.
    // After this is called, the controller will set the cleaning status
    // to Dirty as required by the spec.
    public void checkout()
    {
        this.booking = null;
    }
}