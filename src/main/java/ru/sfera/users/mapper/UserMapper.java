package ru.sfera.users.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sfera.users.dir.dto.DirUserResponseDto;
import ru.sfera.users.model.ActivityStatus;
import ru.sfera.users.model.User;


@Mapper
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "login", expression = "java(dirUser.getUpn().toLowerCase())")
    @Mapping(target = "patronymic", source = "dirUser.middleName")
    User mapToUser(DirUserResponseDto.DirUserDto dirUser, String color, ActivityStatus activityStatus);

    default User mapToActiveUser(DirUserResponseDto.DirUserDto dirUser, String color) {
        return mapToUser(dirUser, color, ActivityStatus.A);
    }

}
