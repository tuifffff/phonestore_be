package com.example.demo.service;

import com.example.demo.dto.response.PageResponse;
import com.example.demo.mapper.PageMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.demo.dto.request.ChangePasswordRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.request.UpdateMyInfoRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class UserService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    EmailService emailService;
    UserMapper userMapper;
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + username));
    }
    public User getCurrentUserEntity() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        return findByUsername(name);
    }
    public UserResponse getMyInfo() {
        return userMapper.toUserResponse(getCurrentUserEntity());
    }

    @Transactional
    public UserResponse registerNewUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng!");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role defaultRole = roleRepository.findById("USER")
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy quyền mặc định USER!"));
        user.setRole(defaultRole);

        return userMapper.toUserResponse(userRepository.save(user));
    }


    @Transactional
    public UserResponse updateMyInfo(UpdateMyInfoRequest request) {
        User user = getCurrentUserEntity();
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setFullName(request.getFullName());
        user.setAvatar(request.getAvatar());
        user.setGender(request.getGender());
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUserEntity();
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không chính xác!");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void sendOtpForgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email này chưa đăng ký tài khoản!"));

        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setResetToken(otp);
        user.setTokenExpiry(java.time.LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        emailService.sendEmail(email, "Mã xác thực (OTP)", "Mã của bạn là: " + otp + ". Mã này có hiệu lực trong 5 phút."+"\nVui lòng không chia sẻ mã này với bất kì ai."+"\nCảm ơn, siêu đẹp trai.");
    }

    @Transactional
    public void resetPasswordWithOtp(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại!"));

        if (user.getResetToken() == null || !user.getResetToken().equals(otp)) {
            throw new RuntimeException("Mã OTP không chính xác!");
        }

        if (user.getTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Mã OTP đã hết hạn!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);
    }
    @Transactional
    public UserResponse updateUserRole(String username, String roleName) {
        // 1. Tìm User
        User user = findByUsername(username);

        // 2. Tìm Role trong DB xem có tồn tại không (để tránh gán bừa)
        Role role = roleRepository.findById(roleName)
                .orElseThrow(() -> new RuntimeException("Role không hợp lệ"));

        // 3. Gán Role mới và lưu lại
        user.setRole(role);
        return userMapper.toUserResponse(userRepository.save(user));
    }
    @Transactional
    public UserResponse revokeRole(String username) {
        // 1. Tìm ông user cần bị "xử lý"
        User user = findByUsername(username);

        // 2. Tìm lại cái Role "USER" thần thánh
        Role defaultRole = roleRepository.findById("USER")
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy quyền mặc định USER!"));

        // 3. Giáng chức xuống làm dân thường và lưu lại
        user.setRole(defaultRole);
        return userMapper.toUserResponse(userRepository.save(user));
    }
    public PageResponse<UserResponse> searchAndFilterUsers(String keyword, String roleName, Pageable pageable) {
        // Lấy thẳng Username từ Token (Không cần Query DB)
        String currentAdminUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<User> userPage;
        if (roleName != null && !roleName.isEmpty()) {
            // Lọc theo Role và ẩn chính mình
            userPage = userRepository.findByRole_NameAndUsernameNot(roleName, currentAdminUsername, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            // Tìm theo keyword và ẩn chính mình
            userPage = userRepository.searchUsersExcludeSelf(keyword, currentAdminUsername, pageable);
        } else {
            // Mặc định lấy tất cả ngoại trừ chính mình
            userPage = userRepository.findByUsernameNot(currentAdminUsername, pageable);
        }

        return PageMapper.toPageResponse(userPage, userMapper::toUserResponse);
    }
}