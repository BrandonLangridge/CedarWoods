/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author ahmedgonnah
 */

public enum AccomodationType
{
    // These are the 6 states for the order status
    CABIN("Cabin"),
    YURT("Yurt"),
    GEODESICDOME("GeodesicDome"),
    AIRSTREAM("Airstream");
      
    private final String accommodationType;
 
    AccomodationType(String accommodationType) 
    {
        this.accommodationType = accommodationType;
    }
    
    public String getAccommodationStatus() 
    {
        return this.accommodationType;
    }
    
    public String getDescription()
    {
        return accommodationType;
    }
    
    @Override
    public String toString()
    {
        return getDescription();
    }    
}


