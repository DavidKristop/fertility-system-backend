package com.group3.backend.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.group3.backend.model.Blog;

public interface BlogRepository extends JpaRepository<Blog, UUID> {
    Page<Blog> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
