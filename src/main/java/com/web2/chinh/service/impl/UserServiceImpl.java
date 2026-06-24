package com.web2.chinh.service.impl;

import com.web2.chinh.dto.UserRequest;
import com.web2.chinh.dto.UserResponse;
import com.web2.chinh.entity.User;
import com.web2.chinh.exception.ResourceNotFoundException;
import com.web2.chinh.repository.UserRepository;
import com.web2.chinh.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // Hàm hash password đơn giản (chỉ để demo, production nên dùng BCrypt)
    private String hashPassword(String raw) {
        // TODO: Thay bằng BCryptPasswordEncoder khi tích hợp Spring Security đầy đủ
        return "HASHED_" + raw;
    }

    @Override
    public UserResponse create(UserRequest request) {
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password không được để trống");
        }
        if (request.getPassword().length() < 6 || request.getPassword().length() > 100) {
            throw new IllegalArgumentException("Password từ 6 đến 100 ký tự");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại: " + request.getEmail());
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(hashPassword(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .avatar(request.getAvatar())
                .role(request.getRole() != null ? request.getRole() : User.Role.USER)
                .enabled(true)
                .build();
        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Override
    public UserResponse update(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user id=" + id));

        if (request.getUsername() != null) {
            userRepository.findByUsername(request.getUsername())
                    .filter(u -> !u.getId().equals(id))
                    .ifPresent(u -> {
                        throw new IllegalArgumentException("Username đã được sử dụng");
                    });
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null) {
            userRepository.findByEmail(request.getEmail())
                    .filter(u -> !u.getId().equals(id))
                    .ifPresent(u -> {
                        throw new IllegalArgumentException("Email đã được sử dụng");
                    });
            user.setEmail(request.getEmail());
        }

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy user id=" + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user id=" + id));
        return UserResponse.fromEntity(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getByRole(User.Role role) {
        return userRepository.findByRole(role).stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getActiveUsers() {
        return userRepository.findByEnabledTrue().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user id=" + id));
        if (!user.getPassword().equals(hashPassword(oldPassword))) {
            throw new IllegalArgumentException("Mật khẩu cũ không đúng");
        }
        user.setPassword(hashPassword(newPassword));
        userRepository.save(user);
    }

    @Override
    public void toggleEnabled(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user id=" + id));
        user.setEnabled(!user.getEnabled());
        userRepository.save(user);
    }
}
