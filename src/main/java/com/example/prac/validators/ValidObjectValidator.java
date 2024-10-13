package com.example.prac.validators;

import com.example.prac.DTO.data.wrappers.AlbumWrapper;
import com.example.prac.DTO.data.wrappers.CoordinatesWrapper;
import com.example.prac.DTO.data.wrappers.LabelWrapper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidObjectValidator implements ConstraintValidator<ValidObject, Object> {

    @Override
    public void initialize(ValidObject constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; 
        }

        if (value instanceof CoordinatesWrapper) {
            CoordinatesWrapper wrapper = (CoordinatesWrapper) value;
            return validateWrapper(wrapper.getCoordinatesId(), wrapper.getCoordinates(), context, "Coordinates");
        }

        if (value instanceof AlbumWrapper) {
            AlbumWrapper wrapper = (AlbumWrapper) value;
            return validateWrapper(wrapper.getBestAlbumId(), wrapper.getBestAlbum(), context, "Album");
        }

        if (value instanceof LabelWrapper) {
            LabelWrapper wrapper = (LabelWrapper) value;
            return validateWrapper(wrapper.getLabelId(), wrapper.getLabel(), context, "Label");
        }

        return false;
    }

    private boolean validateWrapper(Long id, Object obj, ConstraintValidatorContext context, String objectName) {
        if (id != null) {
            return true; 
        } else if (obj != null) {
            
            return true; 
        } else {
            
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(objectName + " cannot be null when no ID is provided")
                    .addConstraintViolation();
            return false;
        }
    }
}
