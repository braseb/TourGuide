package com.openclassrooms.tourguide.entity;

import gpsUtil.location.Attraction;

public class AttractionInfos {
    
    private double distance;
    private int rewardPoints;
    private Attraction attraction;
    
    public AttractionInfos(Attraction attraction, double distance, int rewardPoints) {
        this.distance = distance;
        this.rewardPoints = rewardPoints;
        this.setAttraction(attraction);
        
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public Attraction getAttraction() {
        return attraction;
    }

    public void setAttraction(Attraction attraction) {
        this.attraction = attraction;
    }
    
    

}
