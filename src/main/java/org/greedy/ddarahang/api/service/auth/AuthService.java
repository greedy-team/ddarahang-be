package org.greedy.ddarahang.api.service.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.auth.LoginRequest;
import org.greedy.ddarahang.api.dto.auth.SignUpRequest;
import org.greedy.ddarahang.api.dto.auth.TokenResponse;
import org.greedy.ddarahang.common.security.JwtTokenProvider;
import org.greedy.ddarahang.db.token.RefreshToken;
import org.greedy.ddarahang.db.token.RefreshTokenRepository;
import org.greedy.ddarahang.db.user.User;
import org.greedy.ddarahang.db.user.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtUtil;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Transactional
    public TokenResponse signUp(SignUpRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("이미 존재하는 이메일입니다");
        }
        User newUser = User.builder()
                .nickname(request.nickname())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .build();

        userRepository.save(newUser);

        String accessToken = jwtUtil.createAccessToken(newUser);
        String refreshToken = jwtUtil.createRefreshToken(newUser);

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .user(newUser)
                        .token(refreshToken)
                        .build()
        );

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("[LOGIN FAILED] email={}, timestamp={}", user.getEmail(), LocalDateTime.now());
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(user.getId())
                .orElseThrow(()-> new IllegalArgumentException("토큰이 존재하지 않습니다,"));

        String accessToken = "";
        String refreshToken = refreshTokenEntity.getToken();

        if (jwtUtil.isValidRefreshToken(refreshToken)) { //refreshtoken 유효성 검증 갱신
            accessToken = jwtUtil.createAccessToken(user);
            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } else {
            refreshToken = jwtUtil.createRefreshToken(user);
            refreshTokenEntity.updateToken(refreshToken);
        }
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
