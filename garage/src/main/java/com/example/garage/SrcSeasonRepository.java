package com.example.garage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface SrcSeasonRepository extends JpaRepository<SrcSeason, Long> {
    Optional<SrcSeason> findByUserIdAndFinishedFalse(Long userId);
    java.util.List<SrcSeason> findByUserIdOrderByIdDesc(Long userId);
}
