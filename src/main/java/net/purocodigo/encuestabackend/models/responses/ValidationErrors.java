package net.purocodigo.encuestabackend.models.responses;

import java.util.Date;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationErrors {

    private Map<String, String> errors;

    private Date timestamp;
    
}
