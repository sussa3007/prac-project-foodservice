package com.example.foodserviceapp.auth.oauth2;

import com.example.foodserviceapp.auth.JwtTokenizer;
import com.example.foodserviceapp.auth.utils.Token;
import com.example.foodserviceapp.dto.AuthSuccessResponseDto;
import com.example.foodserviceapp.dto.ResponseDto;
import com.example.foodserviceapp.member.entity.Member;
import com.example.foodserviceapp.member.entity.Point;
import com.example.foodserviceapp.member.service.MemberService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Random;

@RequiredArgsConstructor
public class OAuth2MemberSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenizer jwtTokenizer;

    private final MemberService memberService;

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = String.valueOf(oAuth2User.getAttributes().get("email"));
        /* Member가 최초 로그인이라면 createMember를 해준다 */
        Member member = memberService.optionalMemberByEmail(email)
                .orElseGet(() -> saveMember(email));
        Token token = jwtTokenizer.delegateToken(member);

        responseJson(response,token);
        // TODO Test 용으로 프론트엔드에서 토큰 확인하기 위해 Redirect
        redirect(request, response,token);

    }

    private void redirect(
            HttpServletRequest request,
            HttpServletResponse response,
            Token token
    ) throws IOException {
        String uri = createUri(token).toString();
        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private void responseJson(HttpServletResponse response, Token token) throws IOException {
        Gson gson = new Gson();

        String accessToken = token.getAccessToken();
        String refreshToken = token.getRefreshToken();

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("Refresh", refreshToken);

        ResponseDto responseDto = ResponseDto.of(AuthSuccessResponseDto.of(response));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(gson.toJson(responseDto,ResponseDto.class));
    }

    private Member saveMember(String email) {
        Random random = new Random();
        Member member = Member.builder()
                .email(email)
                .point(new Point())
                .status(Member.MemberStatus.ACTIVE_MEMBER)
                .name("GoogleAuthMember")
                .password(String.valueOf(random.nextInt()))
                .build();
        return memberService.createMember(member);
    }

    private URI createUri(Token token) {
        String accessToken = token.getAccessToken();
        String refreshToken = token.getRefreshToken();
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("access_token", "Bearer " + accessToken);
        queryParams.add("refresh_token", refreshToken);

        return UriComponentsBuilder
                .newInstance()
                .scheme("http")
                .host("localhost")
                .path("/receive-token.html")
                .queryParams(queryParams)
                .build()
                .toUri();
    }
}
