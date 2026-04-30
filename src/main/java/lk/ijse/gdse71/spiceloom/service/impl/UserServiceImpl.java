package lk.ijse.gdse71.spiceloom.service.impl;

import lk.ijse.gdse71.spiceloom.dto.*;
import lk.ijse.gdse71.spiceloom.entity.Otp;
import lk.ijse.gdse71.spiceloom.entity.User;
import lk.ijse.gdse71.spiceloom.enums.Role;
import lk.ijse.gdse71.spiceloom.repository.OtpRepository;
import lk.ijse.gdse71.spiceloom.repository.UserRepository;
import lk.ijse.gdse71.spiceloom.service.UserService;
import lk.ijse.gdse71.spiceloom.util.EmailUtil;
import lk.ijse.gdse71.spiceloom.util.JwtUtil;
import lk.ijse.gdse71.spiceloom.util.OtpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailUtil emailUtil;
    private final OtpUtil otpUtil;

    @Value("${otp.expiration-time}")
    private long otpExpirationMs;

    public UserServiceImpl(UserRepository userRepository,
                           OtpRepository otpRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           EmailUtil emailUtil,
                           OtpUtil otpUtil) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailUtil = emailUtil;
        this.otpUtil = otpUtil;
    }

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAddress(request.getAddress());
        user.setRole(Role.CUSTOMER);
        userRepository.save(user);
    }

    @Override
    public String login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return jwtUtil.generateToken(user.getUsername(), user.getRole().name());
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Email not registered"));
        // Invalidate previous unused OTPs for this email
        otpRepository.findTopByEmailAndUsedFalseOrderByExpirationTimeDesc(request.getEmail())
                .ifPresent(otp -> {
                    otp.setUsed(true);
                    otpRepository.save(otp);
                });

        String otpCode = otpUtil.generateOtp();
        Otp otp = new Otp();
        otp.setEmail(request.getEmail());
        otp.setOtpCode(otpCode);
        otp.setExpirationTime(LocalDateTime.now().plusSeconds(otpExpirationMs / 1000));
        otp.setUsed(false);
        otpRepository.save(otp);

        emailUtil.sendOtpEmail(request.getEmail(), otpCode);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        Otp otp = otpRepository.findTopByEmailAndUsedFalseOrderByExpirationTimeDesc(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No valid OTP found for this email"));

        if (otp.getExpirationTime().isBefore(LocalDateTime.now())) {
            otp.setUsed(true);
            otpRepository.save(otp);
            throw new RuntimeException("OTP has expired");
        }
        if (!otp.getOtpCode().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otp.setUsed(true);
        otpRepository.save(otp);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAddress(user.getAddress());
        dto.setRole(user.getRole());
        return dto;
    }
}