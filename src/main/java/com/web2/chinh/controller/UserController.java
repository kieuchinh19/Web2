package com.web2.chinh.controller;

import com.web2.chinh.dto.ApiResponse;
import com.web2.chinh.dto.ChangePasswordRequest;
import com.web2.chinh.dto.UserRequest;
import com.web2.chinh.dto.UserResponse;
import com.web2.chinh.entity.User;
import com.web2.chinh.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // CREATE
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(userService.create(request)));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công", userService.update(id, request)));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa thành công", null));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getById(id)));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAll()));
    }

    // GET BY ROLE
    @GetMapping("/by-role/{role}")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getByRole(@PathVariable User.Role role) {
        return ResponseEntity.ok(ApiResponse.success(userService.getByRole(role)));
    }

    // GET ACTIVE
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getActive() {
        return ResponseEntity.ok(ApiResponse.success(userService.getActiveUsers()));
    }

    // CHANGE PASSWORD
    @PutMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công", null));
    }

    // TOGGLE ENABLED
    @PatchMapping("/{id}/toggle-enabled")
    public ResponseEntity<ApiResponse<UserResponse>> toggleEnabled(@PathVariable Long id) {
        userService.toggleEnabled(id);
        return ResponseEntity.ok(ApiResponse.success("Đã thay đổi trạng thái", userService.getById(id)));
    }
}
