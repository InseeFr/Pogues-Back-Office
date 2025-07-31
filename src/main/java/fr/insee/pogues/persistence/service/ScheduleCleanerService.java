package fr.insee.pogues.persistence.service;

import fr.insee.pogues.persistence.repository.QuestionnaireVersionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ScheduleCleanerService {

    private QuestionnaireVersionRepository questionnaireVersionRepository;

    public ScheduleCleanerService(QuestionnaireVersionRepository questionnaireVersionRepository){
        this.questionnaireVersionRepository = questionnaireVersionRepository;
    }

    @Scheduled(cron = "${feature.database.rollingBackup.cron}", zone = "${application.timezoneId}")
    public void scheduleQuestionnaireSaveCleaner(){
        log.info("START -- cleaning questionnaire backups");
        log.info("Keep the last backup for a day, and keep 10 last backup regardless of the day");
        questionnaireVersionRepository.cleanVersions();
        log.info("END -- questionnaire backups cleaned");
    }
}
