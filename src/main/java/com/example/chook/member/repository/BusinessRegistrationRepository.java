package com.example.chook.member.repository;

import com.example.chook.member.entity.BusinessRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessRegistrationRepository extends JpaRepository<BusinessRegistration, Long> {
}
