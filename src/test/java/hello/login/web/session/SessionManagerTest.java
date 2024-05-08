package hello.login.web.session;

import hello.login.domain.member.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {
    SessionManager sessionManager = new SessionManager();

    @Test
    void sessionTest(){
        //HttpServletResponse는 인터페이스라서
        //테스트하기 애매
        //-> spring.mock라이브러리가 테스트용 가짜 객체 제공
        MockHttpServletResponse response = new MockHttpServletResponse();
        //세션 생성
        Member member = new Member("1","member1","1234");
        sessionManager.createSession(member, response);
        //setCookie 응답 나갔다고 가정

        //요청에 응답 쿠키가 저장되었는지?
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(response.getCookies());

        //세션을 조회
        Object result = sessionManager.getSession(request);

        //메모리에 있으니까 똑같다.
        Assertions.assertThat(result).isEqualTo(member);

        Assertions.assertThat(result instanceof Member).isTrue();
        Member savedMember = (Member)result;

        Assertions.assertThat(savedMember).isEqualTo(member);

        //세션 만료
        sessionManager.expire(request);

        //다시 세션을 조회
        Object expired = sessionManager.getSession(request);
        Assertions.assertThat(expired).isNull();

    }

}