package jpa.jpa.repository;

import jpa.jpa.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 추가적인 쿼리 메서드를 작성할 수 있습니다.
    User findByUsername(String username);
}
