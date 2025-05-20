package greencity.mapping;

import greencity.dto.event.AddressDto;
import greencity.entity.event.Address;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper extends AbstractConverter<Address, AddressDto> {
    @Override
    protected AddressDto convert(Address source) {
        return AddressDto.builder()
            .id(source.getId())
            .latitude(source.getLatitude())
            .longitude(source.getLongitude())
            .build();
    }
}
