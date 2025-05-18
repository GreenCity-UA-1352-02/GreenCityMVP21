package greencity.service;

import greencity.dto.event.AddressDto;

public interface AddressService {
    /**
     * Saves an address.
     *
     * @param addressDto the address to save
     * @return the saved address
     */
    AddressDto save(AddressDto addressDto);

    /**
     * Updates an address.
     *
     * @param id the id of the address to update
     * @param addressDto the updated address data
     * @return the updated address
     */
    AddressDto update(Long id, AddressDto addressDto);

    /**
     * Deletes an address.
     *
     * @param id the id of the address to delete
     */
    void delete(Long id);
}