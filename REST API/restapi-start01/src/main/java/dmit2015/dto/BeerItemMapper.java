package dmit2015.dto;

import dmit2015.entity.BeerItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BeerItemMapper {

    BeerItemMapper INSTANCE = Mappers.getMapper(BeerItemMapper.class);

    @Mappings({
        @Mapping(target = "id", source = "id"),
        @Mapping(target = "name", source = "name"),
            @Mapping(target ="style", source ="style"),
            @Mapping(target="brand", source="brand"),
        @Mapping(target = "complete", source = "done"),
        @Mapping(target = "version", source = "version"),
    })
    BeerItemDto toDto(BeerItem entity);

    @Mappings({
        @Mapping(target = "id", source = "id"),
        @Mapping(target = "name", source = "name"),
            @Mapping(target ="style", source ="style"),
            @Mapping(target="brand", source="brand"),
        @Mapping(target = "done", source = "complete"),
        @Mapping(target = "version", source = "version"),
    })
    BeerItem toEntity(BeerItemDto dto);

}