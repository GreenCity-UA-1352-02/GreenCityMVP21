package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.event.AddressDto;
import greencity.dto.event.EventDateLocationDto;
import greencity.entity.event.Address;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.enums.EventType;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EventDateLocationRepo;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventDateLocationServiceImpl implements EventDateLocationService {
    private final EventDateLocationRepo eventDateLocationRepo;
    private final AddressService addressService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public EventDateLocationDto save(EventDateLocationDto eventDateLocationDto, long eventId) {
        boolean isOnline = eventDateLocationDto.onlineLink() != null;
        boolean isOffline = eventDateLocationDto.coordinates() != null;

        EventDateLocation.EventDateLocationBuilder builder = EventDateLocation.builder()
            .eventType(parseEventType(isOnline, isOffline))
            .event(Event.builder().id(eventId).build())
            .startTime(eventDateLocationDto.startDate())
            .endTime(eventDateLocationDto.finishDate())
            .onlineLink(isOnline ? eventDateLocationDto.onlineLink() : null);

        if (isOffline) {
            AddressDto savedAddressDto = addressService.save(eventDateLocationDto.coordinates());
            Address address = modelMapper.map(savedAddressDto, Address.class);
            builder.address(address);
        }

        EventDateLocation eventDateLocation = builder.build();
        EventDateLocation savedEventDateLocation = eventDateLocationRepo.save(eventDateLocation);

        return mapToDto(savedEventDateLocation);
    }

    @NotNull
    private EventType parseEventType(boolean isOnline, boolean isOffline) {
        if (isOffline && isOnline) {
            return EventType.ONLINE_OFFLINE;
        } else if (isOffline) {
            return EventType.OFFLINE;
        } else {
            return EventType.ONLINE;
        }
    }

    private EventDateLocationDto mapToDto(EventDateLocation eventDateLocation) {
        return EventDateLocationDto.builder()
            .id(eventDateLocation.getId())
            .startDate(eventDateLocation.getStartTime())
            .finishDate(eventDateLocation.getEndTime())
            .coordinates(eventDateLocation.getAddress() == null ? null
                : modelMapper.map(eventDateLocation.getAddress(), AddressDto.class))
            .onlineLink(eventDateLocation.getOnlineLink())
            .build();
    }

    @Override
    @Transactional
    public EventDateLocationDto update(EventDateLocationDto eventDateLocationDto) {
        EventDateLocation eventDateLocation = eventDateLocationRepo.findById(eventDateLocationDto.id())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.WRONG_EVENT_ID));

        final EventType previousType = eventDateLocation.getEventType();
        boolean isOnline = eventDateLocationDto.onlineLink() != null;
        boolean isOffline = eventDateLocationDto.coordinates() != null;

        eventDateLocation.setEventType(parseEventType(isOnline, isOffline));
        eventDateLocation.setStartTime(eventDateLocationDto.startDate());
        eventDateLocation.setEndTime(eventDateLocationDto.finishDate());
        eventDateLocation.setOnlineLink(isOnline ? eventDateLocationDto.onlineLink() : null);

        updateAddress(eventDateLocationDto, previousType, eventDateLocation);

        EventDateLocation updatedEventDateLocation = eventDateLocationRepo.save(eventDateLocation);
        return mapToDto(updatedEventDateLocation);
    }

    private void updateAddress(EventDateLocationDto eventDateLocationDto,
        EventType previousType,
        EventDateLocation eventDateLocation) {
        EventType newType = eventDateLocation.getEventType();
        boolean addressRequired = newType != EventType.ONLINE;
        if (addressRequired) {
            if (previousType == EventType.OFFLINE) {
                addressService.update(eventDateLocation.getAddress().getId(), eventDateLocationDto.coordinates());
            } else {
                AddressDto savedAddressDto = addressService.save(eventDateLocationDto.coordinates());
                Address address = modelMapper.map(savedAddressDto, Address.class);
                eventDateLocation.setAddress(address);
            }
        } else if (previousType != EventType.ONLINE) {
            addressService.delete(eventDateLocation.getAddress().getId());
            eventDateLocation.setAddress(null);
        }
    }

    private boolean wasOffline(EventDateLocation eventDateLocation) {
        return !eventDateLocation.getEventType().equals(EventType.ONLINE);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        EventDateLocation eventDateLocation = eventDateLocationRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.WRONG_EVENT_ID));

        if (wasOffline(eventDateLocation)) {
            addressService.delete(eventDateLocation.getAddress().getId());
        }

        eventDateLocationRepo.delete(eventDateLocation);
    }
}
