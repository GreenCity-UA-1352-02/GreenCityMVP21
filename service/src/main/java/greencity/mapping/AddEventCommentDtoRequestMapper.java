package greencity.mapping;

import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.entity.event.EventComment;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class AddEventCommentDtoRequestMapper extends AbstractConverter<AddEventCommentDtoRequest, EventComment> {
    @Override
    protected EventComment convert(AddEventCommentDtoRequest dto) {
        return EventComment.builder()
            .text(dto.text())
            .build();
    }
}
