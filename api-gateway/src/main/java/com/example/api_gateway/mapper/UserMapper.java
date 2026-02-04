package com.example.api_gateway.mapper;

import com.example.api_gateway.dto.request.UserRequest;
import com.example.api_gateway.dto.response.UserResponse;
import com.example.api_gateway.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toEntity(UserRequest dto);

    @Mapping(source = "id", target = "id")
    UserResponse toResponse(User user);
}
