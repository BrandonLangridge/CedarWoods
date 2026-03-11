package model;

/**
 *
 *
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

public class CedarWoodsSystem {
    
    private static CedarWoodsSystem instance = null;

    // Attributes
    private ArrayList<Area> areas;

   
    // Constructor
    protected CedarWoodsSystem() {
        this.areas = new ArrayList<>();
    }
    
    private static void populate()
    {
        // Create the four areas with their updated descriptions based on the brief
        Area areaHilltop = new Area("Hilltop", "Traditional log cabins perched atop the hill, offering rustic comfort and sweeping panoramic views.");
        Area areaWoodland = new Area("Woodland", "Eco-friendly geodesic domes tucked in the serene woodland, blending modern convenience with nature.");
        Area areaMeadow = new Area("Meadow", "Circular yurts set in a peaceful meadow, offering a warm and distinctive closeness to the natural world.");
        Area areaLakeview = new Area("Lakeview", "Retro Airstream trailers along the lake's edge, combining contemporary comfort with tranquil waterfront views.");
        
        // Hilltop — 3 Cabins, £160 per night, accommodates 4
        // Each cabin gets a description that appears in the Accommodation Info panel
        for (int cabinNo = 0; cabinNo < 3; cabinNo++)
        {
            Accomodation accommodation = new Accomodation(cabinNo + 1, 160.0f,
                "A traditional cosy log cabin with small kitchen and toilet. Outdoor space with firepit and barbecue grill.",
                null, CleaningStatus.CLEAN, AccomodationType.CABIN);
            areaHilltop.addAccomodation(accommodation);
        }
        
        // Woodland — 7 Geodesic Domes, £120 per night, accommodates 2
        // Each dome gets a description that appears in the Accommodation Info panel
        for (int geodesicNo = 0; geodesicNo < 7; geodesicNo++)
        {
            Accomodation accommodation = new Accomodation(geodesicNo + 1, 120.0f,
                "A modern canvas geodesic dome with large windows for star gazing. Outdoor space with pizza oven.",
                null, CleaningStatus.CLEAN, AccomodationType.GEODESICDOME);
            areaWoodland.addAccomodation(accommodation);
        }      
     
        // Meadow — 5 Yurts, £110 per night, accommodates 2
        // Each yurt gets a description that appears in the Accommodation Info panel
        for (int yurtNo = 0; yurtNo < 5; yurtNo++)
        {
            Accomodation accommodation = new Accomodation(yurtNo + 1, 110.0f,
                "A circular yurt with a warm and cosy interior. Outdoor decking area with seating and fire pit.",
                null, CleaningStatus.CLEAN, AccomodationType.YURT);
            areaMeadow.addAccomodation(accommodation);
        }
        
        // Lakeview — 5 Airstreams, £180 per night, accommodates 4
        // Each airstream gets a description that appears in the Accommodation Info panel
        for (int airstreamNo = 0; airstreamNo < 5; airstreamNo++)
        {
            Accomodation accommodation = new Accomodation(airstreamNo + 1, 180.0f,
                "A stylish retro Airstream trailer with waterfront views. Outdoor seating area with direct lake access.",
                null, CleaningStatus.CLEAN, AccomodationType.AIRSTREAM);
            areaLakeview.addAccomodation(accommodation);
        }  

        instance.addArea(areaHilltop);
        instance.addArea(areaWoodland);
        instance.addArea(areaMeadow);
        instance.addArea(areaLakeview);
    }
    
    public static CedarWoodsSystem getInstance()
    {
        if (instance == null)
        {
            instance = new CedarWoodsSystem();
            
            populate();
        }
        
        return instance;
    }

    // Methods from UML

    public void addArea(Area newArea) {
        areas.add(newArea);
    }

    public ArrayList<Area> getAreas() {
        return areas;
    }

    public Area getAreaByName(String areaName) {
        return null;   // UML does not specify logic
    }

    public Map<String, Integer> calculateAreaStatistics() {
        return new HashMap<>();  // UML does not specify logic
    }
    
    public static void main(String[] args)
    {
        CedarWoodsSystem cedarWoodsSystem = CedarWoodsSystem.getInstance();
        
        ArrayList<Area> areas = cedarWoodsSystem.getAreas();
        
        for (int areaNo = 0; areaNo < areas.size(); areaNo++)
        {
            System.out.println("Area = " + areas.get(areaNo).getAreaName());
        }
        
        Area areaHilltop = areas.get(0);
        
        ArrayList<Accomodation> accommodations = areaHilltop.getAccomodations();
      
        for (int accommodationNo = 0; accommodationNo < accommodations.size(); accommodationNo++)
        {
            System.out.println("Accommodation ID = " + accommodations.get(accommodationNo).getAccomodationID());
        }
        
        Accomodation accommodation = accommodations.get(0);
        
        Guest guest = new Guest("Bob", "Bobson", "0112233456");
        
        Booking booking = new Booking(guest, null, 2, true, 2);
        
        accommodation.checkin(booking);

        System.out.println("END");
    }
    
}