package ru.sfera.users.dir.dto;

import lombok.Data;
import lombok.experimental.Accessors;


/**
 * Запрос на получение пользователей по группе доступа
 */
@Data
@Accessors(chain = true)
public class DirUsersRequestDto {

    private String systemCode;
    private String groupCode;
    private Integer pageNumber;
    private Integer pageSize;

}
