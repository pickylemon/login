package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
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

//    @GetMapping("/")
    public String home(){return "home";}

    @GetMapping("/")
    public String homeLogin(@CookieValue(name = "memberId", required = false) Long memberId, Model model){
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

    @PostMapping("/logout")
    public String logout(HttpServletResponse response){
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
