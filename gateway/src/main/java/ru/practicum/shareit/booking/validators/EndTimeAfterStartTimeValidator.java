package ru.practicum.shareit.booking.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;


@Slf4j
public class EndTimeAfterStartTimeValidator implements ConstraintValidator<EndTimeAfterStartTime, Object> {

    private String startField;
    private String endField;

    @Override
    public void initialize(EndTimeAfterStartTime constraintAnnotation) {
        startField = constraintAnnotation.startTime();
        endField = constraintAnnotation.endTime();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        LocalDateTime start = (LocalDateTime) new BeanWrapperImpl(value).getPropertyValue(startField);
        LocalDateTime end = (LocalDateTime) new BeanWrapperImpl(value).getPropertyValue(endField);

        log.info("EndTimeAfterStartTimeValidator start = {}, end  = {}, isValid = {}",
                start, end, end.isAfter(start));

        return end.isAfter(start);
    }
}
