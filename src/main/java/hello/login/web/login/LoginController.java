package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute LoginForm loginForm) {
        return "/login/loginForm";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletResponse response) {
        if(bindingResult.hasErrors()) {
            return "login/loginForm";
        } //유효성 검증 실패시

        //유효성 검사 통과 + 로그인 검사 실시
        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
        //return loginMember == null? "redirect:/login" : "redirect:/";
        if(loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다."); //reject는 global오류
            return "login/loginForm";
        }

        //로그인 성공 처리

        //쿠키에 시간 정보를 주지 않으면 세션 쿠키(브라우저 종료시 삭제),
        //쿠키에 시간 정보를 설정하면 영속 쿠키
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));

        log.info("\n idCookie.getMaxAge()= {}\n", idCookie.getMaxAge());
        response.addCookie(idCookie);

        return "redirect:/";
    }


}
