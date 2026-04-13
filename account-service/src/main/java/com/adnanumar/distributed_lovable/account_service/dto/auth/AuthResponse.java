package com.adnanumar.distributed_lovable.account_service.dto.auth;

public record AuthResponse(
        String token,
        UserProfileResponse user
) {
}

// dummy: new AuthResponse("", new UserprofileResponse());
