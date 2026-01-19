package com.openclassrooms.tourguide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;

import com.openclassrooms.tourguide.dto.AttractionDto;
import com.openclassrooms.tourguide.dto.NearbyAttractionDto;
import com.openclassrooms.tourguide.dto.UserLocalisationDto;
import com.openclassrooms.tourguide.entity.AttractionInfos;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

import tripPricer.Provider;

@RestController
public class TourGuideController {

	@Autowired
	TourGuideService tourGuideService;
	
	@Autowired
	RewardCentral rewardCentral;
	
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public VisitedLocation getLocation(@RequestParam String userName) throws InterruptedException, ExecutionException {
    	return tourGuideService.getUserLocation(getUser(userName));
    }
    
    //  TODO: Change this method to no longer return a List of Attractions.
 	//  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
 	//  Return a new JSON object that contains:
    	// Name of Tourist attraction, 
        // Tourist attractions lat/long, 
        // The user's location lat/long, 
        // The distance in miles between the user's location and each of the attractions.
        // The reward points for visiting each Attraction.
        //    Note: Attraction reward points can be gathered from RewardsCentral
    @RequestMapping("/getNearbyAttractions") 
    public ResponseEntity<?> getNearbyAttractions(@RequestParam String userName) throws InterruptedException, ExecutionException {
    	
        User user =  getUser(userName);
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
    	List<AttractionInfos> attractions = tourGuideService.getNearByAttractionsWithInfos(visitedLocation);
    	    	
    	List<AttractionDto> listAttractions = new ArrayList<AttractionDto>();
    	UserLocalisationDto userLocalisationDto = new UserLocalisationDto(visitedLocation.location.longitude, 
    	                                                                    visitedLocation.location.latitude);
    	
    	attractions.stream().forEach(a -> listAttractions.add(new AttractionDto(
    	                                            a.getAttraction().attractionName, 
                                                    a.getAttraction().longitude, 
                                                    a.getAttraction().latitude, 
                                                    a.getDistance(), 
                                                    a.getRewardPoints())));
    	    	
    	return ResponseEntity.ok(new NearbyAttractionDto(userLocalisationDto, listAttractions));
    	
    	
    }
    
    @RequestMapping("/getRewards") 
    public List<UserReward> getRewards(@RequestParam String userName) {
    	return tourGuideService.getUserRewards(getUser(userName));
    }
       
    @RequestMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
    	return tourGuideService.getTripDeals(getUser(userName));
    }
    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }
   

}