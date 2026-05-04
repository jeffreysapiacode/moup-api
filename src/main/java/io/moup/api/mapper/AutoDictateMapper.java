package io.moup.api.mapper;

import io.moup.api.entity.AutoDictate;
import io.moup.api.model.Word;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AutoDictateMapper {

    AutoDictate wordToAutoDictate(Word word);

}
