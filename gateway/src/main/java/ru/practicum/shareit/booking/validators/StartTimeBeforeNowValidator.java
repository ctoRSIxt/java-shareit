package ru.practicum.shareit.booking.validators;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

@Slf4j
public class StartTimeBeforeNowValidator implements ConstraintValidator<StartTimeBeforeNow, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime startTime, ConstraintValidatorContext context) {

        log.info("StartTimeBeforeNowValidator start = {}, now = {}, isValid = {}",
                startTime, LocalDateTime.now(), startTime.isAfter(LocalDateTime.now()));
        return startTime.isAfter(LocalDateTime.now());
    }
}