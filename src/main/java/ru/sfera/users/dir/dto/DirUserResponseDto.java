package ru.sfera.users.dir.dto;

import java.util.List;
import java.util.UUID;

import lombok.Data;
import lombok.experimental.Accessors;


/**
 * Ответ на запрос на получение пользователей по группе доступа
 */
@Data
@Accessors(chain = true)
public class DirUserResponseDto {

    private UUID id;
    private Integer totalElements;
    private Integer totalPages;
    private Integer size;
    private String code;
    private String title;
    private String systemCode;
    private List<DirUserDto> content;
    private Integer number;
    private List<DirSort> sort;
    private DirPageable pageable;
    private Integer numberOfElements;
    private Boolean first;
    private Boolean last;
    private Boolean empty;

    @Data
    public static class DirUserDto {

        private String upn;
        private String firstName;
        private String lastName;
        private String middleName;
        private String displayName;
        private String email;

    }

    @Data
    private static class DirSort {

        private Boolean empty;
        private Boolean unsorted;
        private Boolean sorted;

    }

    @Data
    private static class DirPageable {

        private Integer offset;
        private Boolean paged;
        private Boolean unpaged;
        private Integer pageNumber;
        private Integer pageSize;
        private List<DirSort> sort;

    }

}
