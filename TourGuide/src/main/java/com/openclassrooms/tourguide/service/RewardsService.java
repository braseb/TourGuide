package com.openclassrooms.tourguide.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;
	
	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}
	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}
	
	public CompletableFuture<Void> calculateRewards(User user, Executor executor) {
		
	    Runnable task = (() -> {
                	    List<VisitedLocation> userLocations = user.getVisitedLocations();
                		List<Attraction> attractions = gpsUtil.getAttractions();
                		
                		List<UserReward> UserRewardToAdd = new ArrayList<UserReward>();
                		
                		// Create a Set of attractions already with reward
                        Set<String> alreadyRewarded = user.getUserRewards().stream()
                            .map(r -> r.attraction.attractionName)
                            .collect(Collectors.toSet());
                		
                		for(VisitedLocation visitedLocation : userLocations) {
                			
                		    for(Attraction attraction : attractions) {
                				//if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
                		        if(!alreadyRewarded.contains(attraction.attractionName)) {	
                		            if(nearAttraction(visitedLocation, attraction)) {
                						UserRewardToAdd.add(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
                					    //user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
                						alreadyRewarded.add(attraction.attractionName);
                					}
                				}
                				
                                
                            }
                		}
                		//append the userReward now
                        for (UserReward ur : UserRewardToAdd) {
                            user.addUserReward(ur);
                        }
	    });
	    if (executor != null) {
	       return CompletableFuture.runAsync(task, executor);
	    }else {
            return CompletableFuture.runAsync(task);
        }
	}
	
	public CompletableFuture<Void> calculateRewards(User user) {
	    return calculateRewards(user, null);
	}
	
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}
	
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		double distance = getDistance(attraction, visitedLocation.location); 
		
	    return distance > proximityBuffer ? false : true;
	}
	
	private int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}
	
	public int getRewardPoints(Attraction attraction, UUID userId) {
        return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, userId);
    }
	
	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}

}
