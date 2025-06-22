package com.dev.backend_crm.service;

import java.util.List;
import java.util.stream.Collectors;

import com.dev.backend_crm.dto.RoleDto;
import com.dev.backend_crm.dto.UserDto;
import com.dev.backend_crm.dto.UserListDto;
import com.dev.backend_crm.dto.UserStatsDto;
import com.dev.backend_crm.entity.PageResponse;
import com.dev.backend_crm.entity.Role;
import com.dev.backend_crm.entity.User;
import com.dev.backend_crm.repository.UserRepository;
import com.dev.backend_crm.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final ActivityLogService activityLogService;

    public UserDto getByEmail(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User no found with: " + email));
        return toDto(user);
    }

    public UserDto register(UserDto userDto, String performedBy) {
        User user = fromDto(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(new Date());
        User savedUser = userRepository.save(user);

        activityLogService.log(
                "CREATE_USER",
                "Registró al usuario: " + savedUser.getEmail(),
                "User",
                savedUser.getId(),
                performedBy
        );

        return toDto(savedUser);
    }

    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with: " + email));
        return new CustomUserDetails(user);
    }

    public UserDto toDto(User user){
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getStatus(),
                roleService.roleToDto(user.getRole())
        );
    }


    public User fromDto(UserDto userDto) {
        Role role = roleService.findRoleById(userDto.getRole().getId())
                .map(roleService::roleFromDto)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .role(role)
                .status(userDto.getStatus())
                .password(userDto.getPassword())
                .build();
    }

    public UserListDto toUserListDto(User user) {
        return UserListDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .status(user.getStatus())
                .role(roleService.roleToDto(user.getRole()))
                .build();
    }

    public PageResponse<UserListDto> getAllUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);

        List<UserListDto> content = page.getContent()
                .stream()
                .map(this::toUserListDto)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    public UserListDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toUserListDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserDto updateUser(Long id, UserDto userDto, String performedBy) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setStatus(userDto.getStatus());
        existingUser.setRole(roleService.roleFromDto(userDto.getRole()));

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);

        activityLogService.log(
                "UPDATE_USER",
                "Actualizó al usuario: " + updatedUser.getEmail(),
                "User",
                updatedUser.getId(),
                performedBy
        );

        return toDto(updatedUser);
    }

    public void deleteUser(Long id, String performedBy) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);

        activityLogService.log("DELETE_USER", "Eliminó al usuario con ID: " + id, "User", id, performedBy);
    }

    public UserStatsDto getUserStats() {
        long total = userRepository.count();
        long activos = userRepository.countByStatus(true);
        long inactivos = userRepository.countByStatus(false);
        return new UserStatsDto(total, activos, inactivos);
    }
}
