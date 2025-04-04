package co.kr.social_login_test.member.service;

import co.kr.social_login_test.member.model.Member;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {
  Member findMember(String username);

  void save(Member member);
}
