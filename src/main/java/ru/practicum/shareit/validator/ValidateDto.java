package ru.practicum.shareit.validator;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.IncorrectParameterException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

@Slf4j
public class ValidateDto {

    public static void validate(Object objectToValidate, Class classGroup) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(objectToValidate, classGroup);
        if (violations != null) {
            log.info("violations --> {}", violations);
            for (ConstraintViolation<?> violation : violations) {
                throw new IncorrectParameterException(violation.getMessage());
            }
        }
    }

}
