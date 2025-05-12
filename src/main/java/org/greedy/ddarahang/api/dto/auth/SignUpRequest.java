package org.greedy.ddarahang.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest (
        @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
        String nickname,

        @Email
        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        String email,

        @NotBlank(message = "비밀번호은 필수 입력 항목입니다.")
        String password
){
}
