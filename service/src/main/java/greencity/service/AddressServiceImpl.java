package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.event.AddressDto;
import greencity.entity.event.Address;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.AddressRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepo addressRepo;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public AddressDto save(AddressDto addressDto) {
        Address address = modelMapper.map(addressDto, Address.class);
        Address savedAddress = addressRepo.save(address);
        return modelMapper.map(savedAddress, AddressDto.class);
    }

    @Override
    @Transactional
    public AddressDto update(Long id, AddressDto addressDto) {
        Address address = addressRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.WRONG_ADDRESS_ID));
        
        address.setLatitude(addressDto.latitude());
        address.setLongitude(addressDto.longitude());
        
        Address updatedAddress = addressRepo.save(address);
        return modelMapper.map(updatedAddress, AddressDto.class);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Address address = addressRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.WRONG_ADDRESS_ID));
        
        addressRepo.delete(address);
    }
}