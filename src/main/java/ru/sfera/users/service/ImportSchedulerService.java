package ru.sfera.users.service;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;


@Slf4j
@RequiredArgsConstructor
public class ImportSchedulerService {

    private final ImportService importService;

    @Scheduled(
        fixedDelayString = "${sfera.import.users.scheduler.fixed-delay}",
        initialDelayString = "${sfera.import.users.scheduler.initial-delay}"
    )
    @SchedulerLock(
        name = "${sfera.import.users.shedlock.name:sfera_dir_users_import}",
        lockAtMostFor = "${sfera.import.users.shedlock.lock-at-most-for:PT2H}",
        lockAtLeastFor = "${sfera.import.users.shedlock.lock-at-least-for:PT1M}"
    )
    public void doImport() {
        log.info("Start users import");
        try {
            Stopwatch sw = Stopwatch.createStarted();
            importService.importUsersFromDir();
            log.info("Users import took {}ms", sw.stop().elapsed(TimeUnit.MILLISECONDS));
        } catch (Exception ex) {
            log.error("Users import failed", ex);
        }
    }

}
