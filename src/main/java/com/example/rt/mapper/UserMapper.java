package com.example.rt.mapper;

import com.example.rt.dto.request.UserRequest;
import com.example.rt.dto.response.UserResponse;
import com.example.rt.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toEntity(UserRequest dto);

    @Mapping(source = "id", target = "id")
    UserResponse toResponse(User user);
}
