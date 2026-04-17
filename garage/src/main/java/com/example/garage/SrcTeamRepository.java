package com.example.garage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface SrcTeamRepository extends JpaRepository<SrcTeam, Long> {
    List<SrcTeam> findByReputationRequiredLessThanEqualOrderByRankAsc(int rep);
}
