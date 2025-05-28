import com.tongji.wordtrail.dto.*;
import com.tongji.wordtrail.exception.InvalidCredentialsException;
import com.tongji.wordtrail.model.Administer;
import com.tongji.wordtrail.repository.AdminRepository;
import com.tongji.wordtrail.service.AdminService;
import com.tongji.wordtrail.util.JwtUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private Administer mockAdmin;

    @Before
    public void setUp() {
        mockAdmin = new Administer();
        mockAdmin.setUserId(String.valueOf(1L));
        mockAdmin.setUsername("admin");
        mockAdmin.setEmail("admin@example.com");
        mockAdmin.setPassword("encodedPassword");
        mockAdmin.setAdminKey("key123");
        mockAdmin.setAvatarUrl("avatar.png");
    }

    @Test
    public void testLogin_Success() {
        LoginRequest request = new LoginRequest("admin", "123456");
        request.setUsername("admin");
        request.setPassword("123456");

        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(mockAdmin));
        when(passwordEncoder.matches("123456", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(String.valueOf(1L), "admin")).thenReturn("mockedToken");

        AdminResponse response = adminService.login(request);

        assertEquals("mockedToken", response.getToken());
        assertEquals("avatar.png", response.getAvatarUrl());
    }

    @Test(expected = RuntimeException.class)
    public void testLogin_WrongPassword() {
        LoginRequest request = new LoginRequest("admin", "wrong");

        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(mockAdmin));
        when(passwordEncoder.matches("wrong", "encodedPassword")).thenReturn(false);

        adminService.login(request);
    }

    @Test
    public void testEmailLogin_Success() {
        EmailLoginRequest request = new EmailLoginRequest("admin@example.com", "123456");

        when(adminRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(mockAdmin));
        when(passwordEncoder.matches("123456", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(String.valueOf(1L), "admin@example.com")).thenReturn("emailToken");

        AdminResponse response = adminService.EmailLogin(request);

        assertEquals("emailToken", response.getToken());
        assertEquals("avatar.png", response.getAvatarUrl());
    }

    @Test
    public void testResetPassword_Success() {
        AdminResetPasswordRequest request = new AdminResetPasswordRequest("key123", "newpass");

        when(adminRepository.findByAdminKey("key123")).thenReturn(Optional.of(mockAdmin));
        when(passwordEncoder.encode("newpass")).thenReturn("newEncodedPassword");
        when(jwtUtil.generateToken(String.valueOf(1L), "admin@example.com")).thenReturn("resetToken");

        AuthResponse response = adminService.ResetPassword(request);

        verify(adminRepository).save(mockAdmin); // 确保 save 被调用
        assertEquals("resetToken", response.getToken());
        assertEquals(Long.valueOf(1), response.getUserId());
    }

    @Test
    public void testGetAdminInfo_Success() {
        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(mockAdmin));

        AdminDetailsResponse response = adminService.GetAdminInfo("admin");

        assertEquals("admin", response.getUsername());
        assertEquals("admin@example.com", response.getEmail());
        assertEquals("avatar.png", response.getAvatarUrl());
    }

    @Test
    public void testSetUserAvatar_Success() {
        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(mockAdmin));

        boolean result = adminService.SetUserAvatar("admin", "new-avatar.png");

        assertTrue(result);
        assertEquals("new-avatar.png", mockAdmin.getAvatarUrl());
        verify(adminRepository).save(mockAdmin);
    }
}
