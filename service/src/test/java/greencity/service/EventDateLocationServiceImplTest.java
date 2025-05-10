package greencity.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import greencity.ModelUtils;
import greencity.dto.event.AddressDto;
import greencity.dto.event.EventDateLocationDto;
import greencity.entity.event.Address;
import greencity.entity.event.EventDateLocation;
import greencity.enums.EventType;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EventDateLocationRepo;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class EventDateLocationServiceImplTest {
    @Mock
    private EventDateLocationRepo eventDateLocationRepo;

    @Mock
    private AddressService addressService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EventDateLocationServiceImpl service;

    private final Long eventId = 1L;

    @Test
    void save_OfflineDateLocation_Success() {
        AddressDto addressDto = ModelUtils.getAddressDto();
        Address address = ModelUtils.getAddress();
        EventDateLocationDto dto = ModelUtils.getEventDateLocationOfflineDto();
        EventDateLocation saved = ModelUtils.getEventDateLocationOffline().getFirst();
        EventDateLocationDto expectedDto = ModelUtils.getEventDateLocationOfflineDto();

        when(addressService.save(addressDto)).thenReturn(addressDto);
        when(modelMapper.map(addressDto, Address.class)).thenReturn(address);
        when(eventDateLocationRepo.save(any(EventDateLocation.class))).thenReturn(saved);
        when(modelMapper.map(any(Address.class), eq(AddressDto.class))).thenReturn(addressDto);

        EventDateLocationDto result = service.save(dto, eventId);

        assertNotNull(result);
        assertEquals(expectedDto.id(), result.id());
        assertEquals(expectedDto.coordinates(), result.coordinates());

        verify(addressService).save(addressDto);
        verify(modelMapper).map(addressDto, Address.class);
        verify(eventDateLocationRepo).save(any(EventDateLocation.class));
    }

    @Test
    void save_OnlineDateLocation_Success() {
        EventDateLocationDto dto = ModelUtils.getEventDateLocationOnlineDto();
        EventDateLocation saved = ModelUtils.getEventDateLocationOnline().getFirst();
        EventDateLocationDto expectedDto = ModelUtils.getEventDateLocationOnlineDto();

        when(eventDateLocationRepo.save(any(EventDateLocation.class))).thenReturn(saved);

        EventDateLocationDto result = service.save(dto, eventId);

        assertNotNull(result);
        assertEquals(expectedDto.id(), result.id());
        assertEquals(expectedDto.coordinates(), result.coordinates());

        verify(eventDateLocationRepo).save(any(EventDateLocation.class));
    }

    @Test
    void save_AllDateLocation_Success() {
        AddressDto addressDto = ModelUtils.getAddressDto();
        Address address = ModelUtils.getAddress();
        EventDateLocationDto dto = ModelUtils.getEventDateLocationAllDto();
        EventDateLocation saved = ModelUtils.getEventDateLocationAll().getFirst();
        EventDateLocationDto expectedDto = ModelUtils.getEventDateLocationAllDto();

        when(addressService.save(addressDto)).thenReturn(addressDto);
        when(modelMapper.map(addressDto, Address.class)).thenReturn(address);
        when(eventDateLocationRepo.save(any(EventDateLocation.class))).thenReturn(saved);
        when(modelMapper.map(any(Address.class), eq(AddressDto.class))).thenReturn(addressDto);

        EventDateLocationDto result = service.save(dto, eventId);

        assertNotNull(result);
        assertEquals(expectedDto.id(), result.id());
        assertEquals(expectedDto.coordinates(), result.coordinates());

        verify(addressService).save(addressDto);
        verify(modelMapper).map(addressDto, Address.class);
        verify(eventDateLocationRepo).save(any(EventDateLocation.class));
    }

    @Test
    void update_OfflineDateLocation_Success() {
        EventDateLocationDto dto = ModelUtils.getEventDateLocationOfflineDto();
        EventDateLocation eventDateLocation =
            ModelUtils.getEventDateLocationOffline().get(0); // get(0) замість getFirst()

        when(eventDateLocationRepo.findById(dto.id())).thenReturn(Optional.of(eventDateLocation));
        when(addressService.update(eventDateLocation.getAddress().getId(), dto.coordinates()))
            .thenReturn(dto.coordinates());
        when(modelMapper.map(any(Address.class), eq(AddressDto.class))).thenReturn(dto.coordinates());
        when(eventDateLocationRepo.save(any(EventDateLocation.class))).thenReturn(eventDateLocation);

        EventDateLocationDto result = service.update(dto);

        verify(addressService).update(eventDateLocation.getAddress().getId(), dto.coordinates());

        assertEquals(dto.id(), result.id());
        assertEquals(dto.coordinates(), result.coordinates());
        assertEquals(dto.startDate(), result.startDate());
        assertEquals(dto.finishDate(), result.finishDate());
    }

    @Test
    void update_WasOnline_Success() {
        EventDateLocationDto dto = ModelUtils.getEventDateLocationOnlineDto();
        EventDateLocation eventDateLocation = ModelUtils.getEventDateLocationOffline().get(0);

        Long addressId = eventDateLocation.getAddress().getId();

        when(eventDateLocationRepo.findById(dto.id())).thenReturn(Optional.of(eventDateLocation));
        when(eventDateLocationRepo.save(any(EventDateLocation.class))).thenReturn(eventDateLocation);

        EventDateLocationDto result = service.update(dto);

        verify(addressService).delete(addressId);

        assertEquals(dto.id(), result.id());
        assertEquals(dto.onlineLink(), result.onlineLink());
        assertEquals(dto.startDate(), result.startDate());
        assertEquals(dto.finishDate(), result.finishDate());
    }

    @Test
    void update_WasOffline_Success() {
        EventDateLocationDto dto = ModelUtils.getEventDateLocationOfflineDto();
        EventDateLocation eventDateLocation = ModelUtils.getEventDateLocationOnline().get(0);
        AddressDto savedAddressDto = dto.coordinates();
        Address mappedAddress = ModelUtils.getAddress();

        when(eventDateLocationRepo.findById(dto.id())).thenReturn(Optional.of(eventDateLocation));
        when(eventDateLocationRepo.save(any(EventDateLocation.class))).thenReturn(eventDateLocation);
        when(addressService.save(dto.coordinates())).thenReturn(savedAddressDto);
        when(modelMapper.map(savedAddressDto, Address.class)).thenReturn(mappedAddress);

        EventDateLocationDto result = service.update(dto);

        verify(addressService).save(dto.coordinates());
        verify(modelMapper).map(savedAddressDto, Address.class);

        assertEquals(dto.id(), result.id());
        assertEquals(dto.onlineLink(), result.onlineLink());
        assertEquals(dto.startDate(), result.startDate());
        assertEquals(dto.finishDate(), result.finishDate());
    }

    @Test
    void delete_WasOffline_Success() {
        Long id = 1L;
        EventDateLocation eventDateLocation = ModelUtils.getEventDateLocationOffline().get(0);
        eventDateLocation.setEventType(EventType.OFFLINE);

        when(eventDateLocationRepo.findById(id)).thenReturn(Optional.of(eventDateLocation));

        service.delete(id);

        verify(addressService).delete(eventDateLocation.getAddress().getId());
        verify(eventDateLocationRepo).delete(eventDateLocation);
    }

    @Test
    void delete_WasOnline_Success() {
        Long id = 2L;
        EventDateLocation eventDateLocation = ModelUtils.getEventDateLocationOnline().get(0);

        when(eventDateLocationRepo.findById(id)).thenReturn(Optional.of(eventDateLocation));

        service.delete(id);

        verify(addressService, never()).delete(anyLong());
        verify(eventDateLocationRepo).delete(eventDateLocation);
    }

    @Test
    void delete_EventNotFound_ThrowsException() {
        Long id = 99L;
        when(eventDateLocationRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.delete(id));

        verify(addressService, never()).delete(anyLong());
        verify(eventDateLocationRepo, never()).delete(any());
    }
}
