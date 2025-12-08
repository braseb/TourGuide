package com.openclassrooms.tourguide.dto;

import java.util.List;

public record NearbyAttractionDto(UserLocalisationDto userLocalisation,
                                  List<AttractionDto> attractions) {

}
