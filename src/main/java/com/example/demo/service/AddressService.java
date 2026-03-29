package com.example.demo.service;

import com.example.demo.dto.request.AddressRequest;
import com.example.demo.dto.response.AddressResponse;
import com.example.demo.entity.Address;
import com.example.demo.entity.User;
import com.example.demo.mapper.AddressMapper;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // Tự tạo constructor để inject Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // Tự thêm private final cho các field
public class AddressService {
    AddressRepository addressRepository;
    UserRepository userRepository;
    AddressMapper addressMapper;
    // 1. Lấy tất cả địa chỉ của User
    public List<AddressResponse> getMyAddresses(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        List<Address> addresses = addressRepository.findByUser_UserID(user.getUserID());
        // Dùng stream để map sang Response cho chuyên nghiệp
        return addresses.stream()
                .map(addressMapper::toResponse)
                .toList();
    }
    // 2. Thêm địa chỉ mới
    @Transactional
    public AddressResponse createAddress(String username, AddressRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        Address address = Address.builder()
                .street(request.getStreet())
                .district(request.getDistrict())
                .city(request.getCity())
                .isDefault(request.getIsDefault())
                .user(user)
                .build();
        Address savedAddress = addressRepository.save(address);
        // MỚI: Nếu địa chỉ mới tạo là mặc định, phải xử lý các địa chỉ cũ
        if (request.getIsDefault()) {
            handleDefaultAddress(savedAddress.getAddressID(), user.getUserID());
            savedAddress.setIsDefault(true);
        }
        return addressMapper.toResponse(savedAddress);
    }

    // 3. Xóa địa chỉ
    @Transactional
    public void deleteAddress(Integer id, String username) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại hoặc đã bị xóa!"));

        // CHECK CHÍNH CHỦ: Đảm bảo địa chỉ thuộc về user đang đăng nhập
        if (!address.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Bạn không có quyền xóa địa chỉ này!");
        }
        // Xóa (Dùng luôn object address vừa tìm được cho chắc)
        addressRepository.delete(address);
    }
    @Transactional
    public AddressResponse updateAddress(Integer id, AddressRequest request, String username) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại!"));

        // Check chính chủ
        if (!address.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Bạn không có quyền sửa địa chỉ này!");
        }

        // Dùng mapper để update các field từ request vào address
        addressMapper.updateAddress(address, request);

        // Xử lý logic mặc định
        if (request.getIsDefault()) {
            handleDefaultAddress(address.getAddressID(), address.getUser().getUserID());
        }

        return addressMapper.toResponse(addressRepository.save(address));
    }

    // 5. ĐẶT MẶC ĐỊNH (Hàm bổ trợ)
    @Transactional
    public void setDefaultAddress(Integer id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        handleDefaultAddress(id, user.getUserID());
    }
    private void handleDefaultAddress(Integer defaultId, Integer userId) {
        List<Address> addresses = addressRepository.findByUser_UserID(userId);
        addresses.forEach(addr -> {
            // So sánh addressID của từng cái với cái ID khách chọn làm mặc định
            boolean isCurrentDefault = addr.getAddressID().equals(defaultId);
            addr.setIsDefault(isCurrentDefault);
        });
        addressRepository.saveAll(addresses);
    }
}