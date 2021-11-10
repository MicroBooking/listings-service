package converters;

import classes.Listing;
import entities.ListingEntity;

public class ListingConverter {
    public static Listing toDto(ListingEntity entity) {
        Listing dto = new Listing();
        dto.setListingId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setType(entity.getType());
        dto.setMonthlyPrice(entity.getMonthlyPrice());
        dto.setCreated(entity.getCreated());

        return dto;
    }

    public static ListingEntity toEntity(Listing dto) {
        ListingEntity entity = new ListingEntity();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setType(dto.getType());
        entity.setMonthlyPrice(dto.getMonthlyPrice());
        entity.setCreated(dto.getCreated());

        return entity;
    }


}
