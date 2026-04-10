package com.LeetForce.Repository;

import com.LeetForce.Entity.SyncLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncLogsRepository extends JpaRepository<SyncLogs, Long> {
}

