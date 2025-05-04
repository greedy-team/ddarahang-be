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
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtUtil;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public TokenResponse signUp(SignUpRequest request) {
        User newUser = User.builder()
                .nickname(request.nickname())
                .password(request.password()) //.password(new BCryptPasswordEncoder().encode(request.password()))
                .email(request.email())
                .build();

        String accessToken = jwtUtil.createAccessToken(newUser);
        String refreshToken = jwtUtil.createRefreshToken(newUser);

        userRepository.save(newUser);
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

        if (!request.password().equals(user.getPassword())) {
            log.info("비밀번호가 일치하지 않습니다! request_password: " + request.password() + ", user_password: " + user.getPassword());
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(user.getId())
                .orElseThrow(()-> new IllegalArgumentException("토큰이 존재하지 않습니다,"));

        String accessToken = "";
        String refreshToken = refreshTokenEntity.getToken();
        if (jwtUtil.isValidRefreshToken(refreshToken)) {
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

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
