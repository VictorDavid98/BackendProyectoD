package net.purocodigo.encuestabackend.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import net.purocodigo.encuestabackend.validators.UniqueEmailValidator;

@Constraint(validatedBy = UniqueEmailValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {
    String message() default "{encuesta.constraints.email.unique.message}";
    
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
