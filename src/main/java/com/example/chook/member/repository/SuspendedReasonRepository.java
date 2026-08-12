package com.example.chook.member.repository;

import com.example.chook.member.entity.MemberSuspension;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuspendedReasonRepository extends JpaRepository<MemberSuspension, Long> {

}
