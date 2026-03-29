package com.example.demo.dto.response;
import lombok.*;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    private List<T> content;      // Danh sách dữ liệu (ví dụ: List User)
    private int page;             // Trang hiện tại
    private int size;             // Số phần tử mỗi trang
    private long totalElements;   // Tổng số bản ghi trong DB
    private int totalPages;       // Tổng số trang
}
