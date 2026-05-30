package com.adnanumar.distributed_lovable.account_service.dto.subscription;

public record UsageTodayResponse(
        Integer tokenUsed,
        Integer tokensLimit,
        Integer previewsRunning,
        Integer previewLimit
) {
}
