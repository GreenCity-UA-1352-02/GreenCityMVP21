package greencity.mapping;

import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentAuthorDto;
import greencity.entity.User;
import greencity.entity.event.EventComment;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class AddEventCommentDtoResponseMapper extends AbstractConverter<EventComment, AddEventCommentDtoResponse> {
    @Override
    protected AddEventCommentDtoResponse convert(EventComment ecoNewsComment) {
        User user = ecoNewsComment.getUser();
        return AddEventCommentDtoResponse.builder()
            .id(ecoNewsComment.getId())
            .modifiedDate(ecoNewsComment.getModifiedDate())
            .text(ecoNewsComment.getText())
            .author(EventCommentAuthorDto.builder()
                .id(user.getId())
                .name(user.getName())
                .userProfilePicturePath(user.getProfilePicturePath())
                .build())
            .build();
    }
}
