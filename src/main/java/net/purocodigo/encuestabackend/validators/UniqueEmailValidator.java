package net.purocodigo.encuestabackend.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.purocodigo.encuestabackend.annotations.UniqueEmail;
import net.purocodigo.encuestabackend.entities.UserEntity;
import net.purocodigo.encuestabackend.repositories.UserRepository;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        UserEntity user = userRepository.findByEmail(value);
        if (user == null) {
            return true;
        }
        return false;
    }
    
}
