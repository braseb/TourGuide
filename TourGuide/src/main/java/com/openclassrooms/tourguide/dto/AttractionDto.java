package com.openclassrooms.tourguide.dto;

public record AttractionDto(String name, 
                            Double longitude,
                            Double latitude,
                            Double distance,
                            Integer rewardPoints) {
    
}
