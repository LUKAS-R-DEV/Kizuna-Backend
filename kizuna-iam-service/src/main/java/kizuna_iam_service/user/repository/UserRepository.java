package kizuna_iam_service.user.repository;

import kizuna_iam_service.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
