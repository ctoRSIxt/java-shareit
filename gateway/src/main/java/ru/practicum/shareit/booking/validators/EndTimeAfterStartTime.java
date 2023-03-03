package ru.practicum.shareit.booking.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EndTimeAfterStartTimeValidator.class)
public @interface EndTimeAfterStartTime {

    //error message
    String message() default "End time of booking can not be earlier than start time of booking";

    //represents group of constraints
    Class<?>[] groups() default {};

    //represents additional information about annotation
    Class<? extends Payload>[] payload() default {};

    String startTime();

    String endTime();

}
