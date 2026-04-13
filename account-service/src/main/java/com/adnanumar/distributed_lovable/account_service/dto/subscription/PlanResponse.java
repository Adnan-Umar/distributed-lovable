package com.adnanumar.distributed_lovable.account_service.dto.subscription;

public record PlanResponse(
        Long id,
        String name,
        Integer maxProject,
        Integer maxTokensPerDay,
        Boolean unlimitedAi,
        String price
) {
}
