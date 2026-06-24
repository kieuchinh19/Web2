package com.web2.chinh.service;

import com.web2.chinh.dto.UserRequest;
import com.web2.chinh.dto.UserResponse;
import com.web2.chinh.entity.User;

import java.util.List;

public interface UserService {
    UserResponse create(UserRequest request);

    UserResponse update(Long id, UserRequest request);

    void delete(Long id);

    UserResponse getById(Long id);

    List<UserResponse> getAll();

    List<UserResponse> getByRole(User.Role role);

    List<UserResponse> getActiveUsers();

    void changePassword(Long id, String oldPassword, String newPassword);

    void toggleEnabled(Long id);
}
