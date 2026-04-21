package io.moup.api.mapper;

import io.moup.api.entity.Content;
import io.moup.api.view.ContentView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContentMapper {

    ContentView contentToContentView(Content content);

}
