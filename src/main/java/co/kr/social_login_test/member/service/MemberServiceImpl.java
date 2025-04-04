package co.kr.social_login_test.member.service;

import co.kr.social_login_test.member.model.Member;
import co.kr.social_login_test.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
  private final MemberRepository repository;
  @Override
  public Member findMember(String username) {
    return repository.findByUsername(username).orElse(null);
  }

  @Override
  public void save(Member member) {
    repository.save(member);
  }
}
