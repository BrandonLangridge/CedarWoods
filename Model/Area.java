package model;

/**
 *
 * @author ahmedgonnah
 */
import java.util.ArrayList;
import java.util.List;

public class Area {

    // Attributes (private because UML uses '-')
    private String areaName;
    private ArrayList<Accomodation> accommodations;
    private String areaDesciption;

    // Constructor (matches UML exactly)
    public Area(String areaName, String areaDesciption) {
        this.areaName = areaName;
        this.areaDesciption = areaDesciption;
        this.accommodations = new ArrayList<>();
    }

    // Methods (all exactly as UML says)

    public void addAccomodation(Accomodation accomodation) {
        accommodations.add(accomodation);
    }

    public ArrayList<Accomodation> getAccomodations() {
        return accommodations;
    }

    // Counts accommodations that are Dirty or under Maintenance,
    // as these require cleaning before they can be used again
    public int calculateNumOfCleaning() {
        int count = 0;
        for (Accomodation a : accommodations) {
            if (a.getCleaningStatus() == CleaningStatus.DIRTY
                    || a.getCleaningStatus() == CleaningStatus.MAINTENANCE) {
                count++;
            }
        }
        return count;
    }

    // Calculates total breakfasts needed across all occupied accommodations.
    // Per the spec, the count is based on the number of guests (not just bookings),
    // so we sum numberOfGuests for each booking that has breakfast required.
    public int getTotalBreakfastsRequired() {
        int count = 0;
        for (Accomodation a : accommodations) {
            if (a.getBooking() != null && a.getBooking().getIsBreakfastRequired()) {
                count += a.getBooking().getNumberOfGuests();
            }
        }
        return count;
    }

    // Returns a list of accommodations that currently need cleaning
    public List<Accomodation> getAccommodationsNeedingCleaning() {
        List<Accomodation> needCleaning = new ArrayList<>();
        for (Accomodation a : accommodations) {
            if (a.getCleaningStatus() == CleaningStatus.DIRTY
                    || a.getCleaningStatus() == CleaningStatus.MAINTENANCE) {
                needCleaning.add(a);
            }
        }
        return needCleaning;
    }

    // Placeholder for any future statistics refresh logic
    public void updateStatistics() {
        // UML says this method exists, but gives no detail
    }

    public String getAreaDescription() {
        return areaDesciption;
    }

    public String getAreaName() {
        return areaName;
    }

    // Returns accommodations that are available for check-in:
    // must be Clean and not currently occupied
    public List<Accomodation> getAvailableAccomodations() {
        List<Accomodation> available = new ArrayList<>();
        for (Accomodation a : accommodations) {
            if (a.getBooking() == null && a.getCleaningStatus() == CleaningStatus.CLEAN) {
                available.add(a);
            }
        }
        return available;
    }

    // toString returns the area name so it displays correctly in the ChoiceBox
    @Override
    public String toString() {
        return areaName;
    }
}