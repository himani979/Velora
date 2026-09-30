
package com.velora.backend.controller;

import com.velora.backend.dto.FutureLabRequest;
import com.velora.backend.entity.User;
import com.velora.backend.repository.UserRepository;
import com.velora.backend.service.FutureLabService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/future-lab")
public class FutureLabController {

    private final FutureLabService futureLabService;
    private final UserRepository userRepository;

    public FutureLabController(
            FutureLabService futureLabService,
            UserRepository userRepository) {

        this.futureLabService = futureLabService;
        this.userRepository = userRepository;
    }

    @PostMapping("/simulate")
    public ResponseEntity<?> simulate(
            Authentication authentication,
            @RequestBody FutureLabRequest request) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElse(null);

        if (user == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        try {

            Map<String, Object> result =
                    futureLabService.simulate(
                            user,
                            request
                    );

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException exception) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    exception.getMessage()
                            )
                    );
        }
    }
}
