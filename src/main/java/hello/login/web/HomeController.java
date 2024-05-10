package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.session.SessionManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.stream.Stream;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

//    @GetMapping("/")
    public String home(){return "home";}

    //@GetMapping("/") 쿠키를 이용한 v1
    public String homeLoginV1(@CookieValue(name = "memberId", required = false) Long memberId, Model model){
        if(memberId==null) {
            return "home";
        }

        Member loginMember = memberRepository.findById(memberId);
        if(loginMember == null) {
            return "home";
        }

        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    @GetMapping("/") //세션을 이용하는 v2
    public String homeLoginV2(HttpServletRequest request, Model model){
        //세션 관리자에 저장된 회원 정보를 조회.
        Member sessionMember = (Member) sessionManager.getSession(request);
        if(sessionMember == null) {
            return "home";
        }
//        Member loginMember = memberRepository.findById(sessionMember.getId());
//        if(loginMember == null) {
//            return "home";
//        } //이 로직마저 필요 없음.
        model.addAttribute("member", sessionMember);
        return "loginHome";
    }
}
