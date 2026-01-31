package com.example.inventoryexample.service;

import com.example.inventoryexample.dto.LoginRequest;
import com.example.inventoryexample.dto.LoginResponse;
import com.example.inventoryexample.dto.UserDto;
import com.example.inventoryexample.entity.User;
import com.example.inventoryexample.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * White Box Testing untuk UserService
 * 
 * Teknik yang digunakan:
 * 1. Statement Coverage - Setiap statement dieksekusi minimal sekali
 * 2. Branch Coverage - Setiap kondisi diuji untuk true dan false
 * 3. Path Coverage - Semua jalur eksekusi diuji
 * 4. Exception Testing - Semua exception path diuji
 * 5. Condition Coverage - Setiap kondisi boolean diuji
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("White Box Testing - UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Setup User entity
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("$2a$10$hashedPassword"); // BCrypt hash
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPhoneNumber("081234567890");
        user.setRole(User.UserRole.STAFF);
        user.setIsActive(true);

        // Setup UserDto
        userDto = new UserDto();
        userDto.setUsername("newuser");
        userDto.setPassword("password123");
        userDto.setFullName("New User");
        userDto.setEmail("new@example.com");
        userDto.setPhoneNumber("081234567891");
        userDto.setRole(User.UserRole.STAFF);
        userDto.setIsActive(true);

        // Setup LoginRequest
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    }

    @Nested
    @DisplayName("Test login() - Complete Branch Coverage")
    class LoginTest {

        @Test
        @DisplayName("Should login successfully with valid credentials")
        void login_WithValidCredentials_ReturnsLoginResponse() {
            // Arrange - Happy path: user found, active, password matches
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);

            // Act
            LoginResponse response = userService.login(loginRequest);

            // Assert
            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals("testuser", response.getUsername());
            assertEquals("Test User", response.getFullName());
            assertEquals("Login berhasil", response.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void login_WhenUserNotFound_ThrowsException() {
            // Arrange - Path: user not found
            when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.login(loginRequest)
            );
            assertEquals("Username atau password salah", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when user is inactive")
        void login_WhenUserInactive_ThrowsException() {
            // Arrange - Path: user found but inactive
            user.setIsActive(false);
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.login(loginRequest)
            );
            assertEquals("Akun Anda tidak aktif. Hubungi administrator", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when password is wrong")
        void login_WhenPasswordWrong_ThrowsException() {
            // Arrange - Path: user found, active, but password doesn't match
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.login(loginRequest)
            );
            assertEquals("Username atau password salah", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Test getUserById() - Branch Coverage")
    class GetUserByIdTest {

        @Test
        @DisplayName("Should return user when found")
        void getUserById_WhenUserExists_ReturnsUserDto() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            // Act
            UserDto result = userService.getUserById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("testuser", result.getUsername());
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void getUserById_WhenUserNotFound_ThrowsException() {
            // Arrange
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.getUserById(999L)
            );
            assertTrue(exception.getMessage().contains("tidak ditemukan"));
        }
    }

    @Nested
    @DisplayName("Test getAllUsers() - Statement Coverage")
    class GetAllUsersTest {

        @Test
        @DisplayName("Should return list of users")
        void getAllUsers_WhenUsersExist_ReturnsUserDtoList() {
            // Arrange
            when(userRepository.findAll()).thenReturn(Arrays.asList(user));

            // Act
            List<UserDto> result = userService.getAllUsers();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should return empty list when no users")
        void getAllUsers_WhenNoUsers_ReturnsEmptyList() {
            // Arrange
            when(userRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<UserDto> result = userService.getAllUsers();

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Test getUsersByRoleString() - Branch & Exception Coverage")
    class GetUsersByRoleStringTest {

        @Test
        @DisplayName("Should return users when role is valid (ADMIN)")
        void getUsersByRoleString_WhenValidAdmin_ReturnsUsers() {
            // Arrange
            user.setRole(User.UserRole.ADMIN);
            when(userRepository.findByRole(User.UserRole.ADMIN)).thenReturn(Arrays.asList(user));

            // Act
            List<UserDto> result = userService.getUsersByRoleString("ADMIN");

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(User.UserRole.ADMIN, result.get(0).getRole());
        }

        @Test
        @DisplayName("Should return users when role is valid (lowercase)")
        void getUsersByRoleString_WhenLowercaseRole_ReturnsUsers() {
            // Arrange - Testing case insensitivity
            when(userRepository.findByRole(User.UserRole.STAFF)).thenReturn(Arrays.asList(user));

            // Act
            List<UserDto> result = userService.getUsersByRoleString("staff");

            // Assert
            assertNotNull(result);
        }

        @Test
        @DisplayName("Should throw exception when role is invalid")
        void getUsersByRoleString_WhenInvalidRole_ThrowsException() {
            // Arrange - Path: invalid role string

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.getUsersByRoleString("INVALID_ROLE")
            );
            assertTrue(exception.getMessage().contains("tidak valid"));
        }
    }

    @Nested
    @DisplayName("Test createUser() - Branch Coverage for validation")
    class CreateUserTest {

        @Test
        @DisplayName("Should create user when username and email are unique")
        void createUser_WhenValid_CreatesUser() {
            // Arrange
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);

            // Act
            UserDto result = userService.createUser(user);

            // Assert
            assertNotNull(result);
            verify(passwordEncoder, times(1)).encode(anyString());
        }

        @Test
        @DisplayName("Should throw exception when username exists")
        void createUser_WhenUsernameExists_ThrowsException() {
            // Arrange - Path: username already exists
            when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.createUser(user)
            );
            assertEquals("Username sudah digunakan", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when email exists")
        void createUser_WhenEmailExists_ThrowsException() {
            // Arrange - Path: email already exists
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.createUser(user)
            );
            assertEquals("Email sudah terdaftar", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Test createUserFromDto() - Complete Path Coverage")
    class CreateUserFromDtoTest {

        @Test
        @DisplayName("Should create user with default values when null")
        void createUserFromDto_WhenRoleNull_UsesDefaultStaff() {
            // Arrange - Branch: role == null
            userDto.setRole(null);
            userDto.setIsActive(null);
            
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User savedUser = invocation.getArgument(0);
                savedUser.setId(1L);
                return savedUser;
            });

            // Act
            UserDto result = userService.createUserFromDto(userDto);

            // Assert
            verify(userRepository).save(argThat(u -> 
                u.getRole() == User.UserRole.STAFF && u.getIsActive() == true
            ));
        }

        @Test
        @DisplayName("Should create user with provided role when not null")
        void createUserFromDto_WhenRoleProvided_UsesProvidedRole() {
            // Arrange - Branch: role != null
            userDto.setRole(User.UserRole.ADMIN);
            userDto.setIsActive(false);
            
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User savedUser = invocation.getArgument(0);
                savedUser.setId(1L);
                return savedUser;
            });

            // Act
            userService.createUserFromDto(userDto);

            // Assert
            verify(userRepository).save(argThat(u -> 
                u.getRole() == User.UserRole.ADMIN && u.getIsActive() == false
            ));
        }

        @Test
        @DisplayName("Should throw exception when username already exists")
        void createUserFromDto_WhenUsernameExists_ThrowsException() {
            // Arrange
            when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(true);

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.createUserFromDto(userDto)
            );
            assertTrue(exception.getMessage().contains("sudah digunakan"));
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void createUserFromDto_WhenEmailExists_ThrowsException() {
            // Arrange
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.createUserFromDto(userDto)
            );
            assertTrue(exception.getMessage().contains("sudah digunakan"));
        }
    }

    @Nested
    @DisplayName("Test getUsersByRole() - Statement Coverage")
    class GetUsersByRoleTest {

        @Test
        @DisplayName("Should return users by role enum")
        void getUsersByRole_WhenCalled_ReturnsFilteredUsers() {
            // Arrange
            when(userRepository.findByRole(User.UserRole.STAFF)).thenReturn(Arrays.asList(user));

            // Act
            List<UserDto> result = userService.getUsersByRole(User.UserRole.STAFF);

            // Assert
            assertEquals(1, result.size());
            assertEquals(User.UserRole.STAFF, result.get(0).getRole());
        }

        @Test
        @DisplayName("Should return empty list when no users with role")
        void getUsersByRole_WhenNoUsersWithRole_ReturnsEmptyList() {
            // Arrange
            when(userRepository.findByRole(User.UserRole.ADMIN)).thenReturn(Collections.emptyList());

            // Act
            List<UserDto> result = userService.getUsersByRole(User.UserRole.ADMIN);

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Test updateUser() - Path Coverage")
    class UpdateUserTest {

        @Test
        @DisplayName("Should update user when found")
        void updateUser_WhenUserExists_UpdatesSuccessfully() {
            // Arrange
            User updatedDetails = new User();
            updatedDetails.setFullName("Updated Name");
            updatedDetails.setEmail("updated@example.com");
            updatedDetails.setRole(User.UserRole.ADMIN);
            updatedDetails.setIsActive(false);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            // Act
            UserDto result = userService.updateUser(1L, updatedDetails);

            // Assert
            assertNotNull(result);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void updateUser_WhenUserNotFound_ThrowsException() {
            // Arrange
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RuntimeException.class, 
                () -> userService.updateUser(999L, user));
        }
    }
}
