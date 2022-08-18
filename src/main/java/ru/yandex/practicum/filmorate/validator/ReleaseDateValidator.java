package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReleaseDateValidator implements ConstraintValidator<ValidReleaseDate, LocalDate> {

    private final String pattern = "dd.MM.yyyy";
    private LocalDate boundReleaseDate;

    @Override
    public void initialize(ValidReleaseDate constraintAnnotation) {
        this.boundReleaseDate = LocalDate.parse(constraintAnnotation.value(), DateTimeFormatter.ofPattern(pattern));
    }

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext constraintValidatorContext) {
        if (releaseDate == null) return true;
        return releaseDate.isAfter(boundReleaseDate) || releaseDate.isEqual(boundReleaseDate);
    }
}
