/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author ahmedgonnah
 */

public enum CleaningStatus 
{
    // These are the 6 states for the order status
    CLEAN("Clean"),
    DIRTY("Dirty"),
    MAINTENANCE("Maintenance");
      
    private final String cleaningStatus;
 
    CleaningStatus(String cleaningStatus) 
    {
        this.cleaningStatus = cleaningStatus;
    }
    
    public String getCleaningStatus() 
    {
        return this.cleaningStatus;
    }
    
    public String getDescription()
    {
        return cleaningStatus;
    }
    
    @Override
    public String toString()
    {
        return getDescription();
    }    
}

