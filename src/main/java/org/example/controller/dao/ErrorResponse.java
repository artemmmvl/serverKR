package org.example.controller.dao;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    String message;

}
