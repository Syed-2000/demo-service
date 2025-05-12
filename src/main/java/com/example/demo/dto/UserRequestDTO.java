package com.example.demo.dto;


import jakarta.validation.constraints.*;

public record UserRequestDTO(
        @NotNull(message = "Name cannot be null")
        @NotBlank(message = "Name cannot be blank")
        String name,
        @Min(value = 0, message = "Value must be non-negative")
        Integer age,
        @NotNull(message = "Status cannot be null")
        @NotBlank(message = "Status cannot be blank")
        String status) { }
