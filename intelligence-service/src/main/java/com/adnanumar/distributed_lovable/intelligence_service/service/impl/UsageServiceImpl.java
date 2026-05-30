package com.adnanumar.distributed_lovable.intelligence_service.service.impl;

import com.adnanumar.distributed_lovable.common_lib.dto.PlanDto;
import com.adnanumar.distributed_lovable.common_lib.security.AuthUtil;
import com.adnanumar.distributed_lovable.intelligence_service.client.AccountClient;
import com.adnanumar.distributed_lovable.intelligence_service.entity.UsageLog;
import com.adnanumar.distributed_lovable.intelligence_service.repository.UsageLogRepository;
import com.adnanumar.distributed_lovable.intelligence_service.service.UsageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsageServiceImpl implements UsageService {

    final UsageLogRepository usageLogRepository;
    final AuthUtil authUtil;
    final AccountClient accountClient;

    @Override
    public void recordTokenUsage(Long userId, int actualTokens) {
        LocalDate today = LocalDate.now();

        UsageLog todayLog = usageLogRepository.findByUserIdAndDate(userId, today)
                .orElseGet(() -> createNewDailyLog(userId, today));

        todayLog.setTokensUsed(todayLog.getTokensUsed() + actualTokens);
        usageLogRepository.save(todayLog);
    }

    @Override
    public void checkDailyTokensUsage() {
        Long userId = authUtil.getCurrentUserId();
        PlanDto plan = accountClient.getCurrentSubscribedPlanByUser();

        LocalDate today = LocalDate.now();

        UsageLog todayLog = usageLogRepository.findByUserIdAndDate(userId, today)
                .orElseGet(() -> createNewDailyLog(userId, today));

        if (plan.unlimitedAi()) return;

        int currentUsage = todayLog.getTokensUsed();
        int limit = plan.maxTokensPerDay();

        if (currentUsage >= limit) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Daily limit reached, Upgrade now");
        }
    }

    private UsageLog createNewDailyLog(Long userId, LocalDate today) {
        UsageLog newLog = UsageLog.builder()
                .userId(userId)
                .date(today)
                .tokensUsed(0)
                .build();
        return usageLogRepository.save(newLog);
    }

}
