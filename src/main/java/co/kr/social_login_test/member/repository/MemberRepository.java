package co.kr.social_login_test.member.repository;

import co.kr.social_login_test.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
  Optional<Member> findByUsername(String username);
}
