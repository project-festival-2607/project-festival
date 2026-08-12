package com.example.chook.member.repository;

import com.example.chook.member.entity.SuspendedReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuspendedReasonRepository extends JpaRepository<SuspendedReason, Long> {

}
