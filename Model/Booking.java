/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;

/**
 *
 * @author ahmedgonnah
 */
public class Booking {
    private Guest guest;
    private Date checkInDate;
    private int numberOfGuests;
    private boolean isBreakfastRequired;
    private int numberOfNights;
    
    public Booking(Guest guest, Date checkInDate, int numberOfGuests, boolean isBreakfastRequired, int numberOfNights)
    {
        this.guest = guest;
        this.checkInDate = checkInDate;
        this.isBreakfastRequired = isBreakfastRequired;
        this.numberOfNights = numberOfNights;
        this.numberOfGuests = numberOfGuests;
                
    }
    
    public Date getCheckInDate()
    { 
        return this.checkInDate;
    }
    
    public Guest getGuest()
    {
       return this.guest;
    }
    public int getNumberOfGuests()
    {
        return this.numberOfGuests;
    }
    public boolean getIsBreakfastRequired()
    {
        return this.isBreakfastRequired;
    }
    public int getNumberOfNights()
    {
        return this.numberOfNights;
    }
}

