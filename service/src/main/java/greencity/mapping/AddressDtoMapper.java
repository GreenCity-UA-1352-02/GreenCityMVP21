package greencity.mapping;

import greencity.dto.event.AddressDto;
import greencity.entity.event.Address;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class AddressDtoMapper extends AbstractConverter<AddressDto, Address> {
    @Override
    protected Address convert(AddressDto source) {
        return Address.builder()
            .id(source.id())
            .latitude(source.latitude())
            .longitude(source.longitude())
            .build();
    }
}
