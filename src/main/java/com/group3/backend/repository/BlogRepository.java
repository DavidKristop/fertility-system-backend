package com.group3.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group3.backend.model.Blog;

public interface BlogRepository extends JpaRepository<Blog, Long> {
}
