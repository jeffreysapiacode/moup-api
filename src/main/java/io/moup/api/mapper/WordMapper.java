package io.moup.api.mapper;

import io.moup.api.entity.Word;
import io.moup.api.view.WordView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WordMapper {
    WordView WordToWordView(Word word);
}
