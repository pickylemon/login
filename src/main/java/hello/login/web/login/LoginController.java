package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.session.SessionManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute LoginForm loginForm) {
        return "/login/loginForm";
    }

    //@PostMapping("/login")

    /**
     * v1 : 쿠키를 사용한 로그인 (보안에 취약)
     */
    public String loginV1(@Valid @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletResponse response) {
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

    /**
     * v2 : 직접 만든 세션 매니저를 활용한 로그인
     */
    @PostMapping("/login")
    public String loginV2(@Valid @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletResponse response) {
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

        //세션 관리자를 통해 세션을 생성하고, 회원 데이터를 보관
        sessionManager.createSession(loginMember, response);
        return "redirect:/";
    }


    @PostMapping("/logout")
    public String logoutV2(HttpServletRequest request){
        sessionManager.expire(request);
        return "redirect:/";
    }

    //@PostMapping("/logout") //쿠키를 덮어쓰는 로그아웃
    public String logoutV1(HttpServletResponse response){
        //쿠키를 지우는 방법은, 쿠키를 찾아내서 setMaxAge = 0 하는게 아니라
        //새로 덮어 씌워버리고(map) setMaxAge = 0.
        //즉 그 쿠키를 찾는 과정이 없음.
        expireCookie(response, "memberId");
        return "redirect:/";
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie newCookie = new Cookie("memberId", null);
        newCookie.setMaxAge(0);
        response.addCookie(newCookie);
    }


}
