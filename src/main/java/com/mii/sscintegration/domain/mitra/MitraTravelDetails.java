/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mii.sscintegration.domain.mitra;

/**
 *
 * @author DewiYu
 */
public class MitraTravelDetails {

    public String trNumber,
            employeeId,
            noTrip,
            travelStartDate,
            travelEndDate,
            origin,
            destination1,
            destination2,
            destination3,
            approverEmail,
            remark,
            golper,
            tripPurpose,
            transportationType;
    
    public Long approvalTimestamp;

    public Float estimatedCost, flightEstimatedCost, hotelEstimatedCost, trainEstimatedCost;

}
