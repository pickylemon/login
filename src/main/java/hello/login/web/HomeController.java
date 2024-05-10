package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.session.SessionManager;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    //@GetMapping("/") //세션을 이용하는 v2
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

    //@GetMapping("/") //서블릿의 세션을 이용하는 v3
    public String homeLoginV3(HttpServletRequest request, Model model){
        //이미 있는 세션을 구한다.(새로 만들 의도는 x)
        HttpSession session = request.getSession(false);
        if(session == null) {
            return "home";
        }
        Member sessionMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        //세션에 회원데이터가 없으면 일반 home으로
        if(sessionMember == null) {
            return "home";
        }
        model.addAttribute("member", sessionMember);
        return "loginHome";
    }
    @GetMapping("/") //스프링의 세션어트리뷰트 활용(세션에서 attribute를 한번에 꺼낸다.)
    //로그인 하지 않은 회원도 접근할 수 있으므로 required = false
    //@SessionAttribute는 세션 생성 기능은 없음. 세션에서 꺼내기만 가능
    public String homeLoginV3Spring(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member sessionMember,
            Model model){
        //세션에 회원데이터가 없으면 일반 home으로
        if(sessionMember == null) {
            return "home";
        }
        model.addAttribute("member", sessionMember);
        return "loginHome";
    }
}
