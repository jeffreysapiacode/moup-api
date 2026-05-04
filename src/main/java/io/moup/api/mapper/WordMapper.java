package io.moup.api.mapper;

import io.moup.api.entity.Word;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WordMapper {

    Word wordToWord(io.moup.api.model.Word word);

}
