package ru.sfera.users.config;

import java.util.List;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sfera.users.dir.config.properties.UsersImportProperties;
import ru.sfera.users.dir.service.DirUserService;
import ru.sfera.users.mapper.UserMapper;
import ru.sfera.users.repository.ColorRepository;
import ru.sfera.users.repository.UserRepository;
import ru.sfera.users.service.ColorService;
import ru.sfera.users.service.ImportLifecycleProcessor;
import ru.sfera.users.service.ImportRoleService;
import ru.sfera.users.service.ImportService;


@Configuration
public class UserImportConfig {

    @Bean
    public UserMapper userMapper() {
        return Mappers.getMapper(UserMapper.class);
    }

    @Bean
    public ColorService colorService(ColorRepository colorRepository) {
        return new ColorService(colorRepository);
    }

    @Bean
    public ImportService importService(
        DirUserService dirUserService,
        ImportRoleService importRoleService,
        ColorService colorService,
        UserMapper userMapper,
        UserRepository userRepository,
        UsersImportProperties properties,
        List<ImportLifecycleProcessor> lifecycleProcessors
    ) {
        return new ImportService(
            dirUserService,
            importRoleService,
            colorService,
            userMapper,
            userRepository,
            properties,
            lifecycleProcessors
        );
    }

}
