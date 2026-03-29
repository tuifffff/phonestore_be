package com.example.demo.exception;

import com.example.demo.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice // Đánh dấu đây là nơi bắt lỗi toàn hệ thống
public class GlobalExceptionHandler {

    // 1. Bắt tất cả các lỗi RuntimeException (Lỗi do mình tự throw trong Service)
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(9999); // Mã lỗi mặc định cho Runtime
        apiResponse.setMessage(exception.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // 2. Bắt lỗi Validation (Ví dụ: @Valid ở OrderController bị sai định dạng)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception) {
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(1001); // Mã lỗi cho Validation
        apiResponse.setMessage(exception.getFieldError().getDefaultMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // 3. Bắt các lỗi không xác định khác (Lỗi hệ thống, SQL...)
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse> handlingException(Exception exception) {
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(5000);
        apiResponse.setMessage("Lỗi hệ thống: " + exception.getMessage());

        return ResponseEntity.status(500).body(apiResponse);
    }
}