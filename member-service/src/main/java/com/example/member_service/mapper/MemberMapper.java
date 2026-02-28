package com.example.member_service.mapper;

import com.example.member_service.dto.response.MemberResponse;
import com.example.member_service.entity.EventMember;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    MemberResponse toResponse(EventMember member);
}
