package ru.sfera.users.service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.sfera.users.repository.ColorRepository;


@Slf4j
@RequiredArgsConstructor
public class ColorService {

    private static final String USER_ENTITY = "USER";
    private final ColorRepository colorRepository;

    private final Random random = new Random();
    private final LoadingCache<String, List<String>> colors = CacheBuilder.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build(new CacheLoader<>() {
            @Override
            public List<String> load(String key) {
                return colorRepository.findAllNamesByEntity(key);
            }
        });

    public String nextColorForUser() {
        return nextColor(USER_ENTITY);
    }

    public String nextColor(String entity) {
        try {
            var palette = colors.get(entity);
            if (palette.isEmpty()) {
                return null;
            }
            return palette.get(random.nextInt(palette.size()));
        } catch (Exception e) {
            throw new RuntimeException("Cannot load colors for %s".formatted(e));
        }
    }

}
