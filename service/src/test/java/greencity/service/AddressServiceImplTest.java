package greencity.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.event.AddressDto;
import greencity.entity.event.Address;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.AddressRepo;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
public class AddressServiceImplTest {

    @Mock
    private AddressRepo addressRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    private AddressDto addressDto;
    private Address address;

    @BeforeEach
    void setUp() {
        addressDto = ModelUtils.getAddressDto();

        address = ModelUtils.getAddress();
    }

    @Test
    void save() {
        when(modelMapper.map(addressDto, Address.class)).thenReturn(address);
        when(addressRepo.save(address)).thenReturn(address);
        when(modelMapper.map(address, AddressDto.class)).thenReturn(addressDto);

        AddressDto result = addressService.save(addressDto);

        assertNotNull(result);
        assertEquals(addressDto.id(), result.id());
        assertEquals(addressDto.latitude(), result.latitude());
        assertEquals(addressDto.longitude(), result.longitude());

        verify(addressRepo).save(address);
        verify(modelMapper).map(addressDto, Address.class);
        verify(modelMapper).map(address, AddressDto.class);
    }

    @Test
    void update_Success() {
        when(addressRepo.findById(1L)).thenReturn(Optional.of(address));
        when(addressRepo.save(address)).thenReturn(address);
        when(modelMapper.map(address, AddressDto.class)).thenReturn(addressDto);

        AddressDto result = addressService.update(1L, addressDto);

        assertNotNull(result);
        assertEquals(addressDto.latitude(), result.latitude());
        assertEquals(addressDto.longitude(), result.longitude());

        verify(addressRepo).findById(1L);
        verify(addressRepo).save(address);
        verify(modelMapper).map(address, AddressDto.class);
    }

    @Test
    void update_ThrowsNotFoundException() {
        when(addressRepo.findById(999L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
            () -> addressService.update(999L, addressDto));

        assertEquals(ErrorMessage.WRONG_ADDRESS_ID, ex.getMessage());
        verify(addressRepo).findById(999L);
        verify(addressRepo, never()).save(any());
    }

    @Test
    void delete_Success() {
        when(addressRepo.findById(1L)).thenReturn(Optional.of(address));

        addressService.delete(1L);

        verify(addressRepo).findById(1L);
        verify(addressRepo).delete(address);
    }

    @Test
    void delete_ThrowsNotFoundException() {
        when(addressRepo.findById(999L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
            () -> addressService.update(999L, addressDto));

        assertEquals(ErrorMessage.WRONG_ADDRESS_ID, ex.getMessage());
        verify(addressRepo).findById(999L);
        verify(addressRepo, never()).delete(any());
    }
}
