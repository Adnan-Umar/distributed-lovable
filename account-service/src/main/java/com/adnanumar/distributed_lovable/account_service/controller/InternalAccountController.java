package com.adnanumar.distributed_lovable.account_service.controller;

import com.adnanumar.distributed_lovable.account_service.mapper.UserMapper;
import com.adnanumar.distributed_lovable.account_service.repository.UserRepository;
import com.adnanumar.distributed_lovable.account_service.service.SubscriptionService;
import com.adnanumar.distributed_lovable.common_lib.dto.PlanDto;
import com.adnanumar.distributed_lovable.common_lib.dto.UserDto;
import com.adnanumar.distributed_lovable.common_lib.error.ResourceNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/internal/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalAccountController {

    UserRepository userRepository;
    UserMapper userMapper;
    SubscriptionService subscriptionService;

    @GetMapping("/users/{id}")
    public UserDto getUserById(@PathVariable Long id){
        return userRepository.findById(id)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
    }

    @GetMapping("/users/by-email")
    public Optional<UserDto> getUserByEmail(@RequestParam String email){
        return userRepository.findByUsernameIgnoreCase(email)
                .map(userMapper::toUserDto);
    }

    @GetMapping("/billing/current-plan")
    public PlanDto getCurrentSubscribedPlan() {
        return subscriptionService.getCurrentSubscribedPlanByUser();
    }

}
