package com.group3.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group3.backend.model.Blog;

@Repository
public interface BlogRepository extends JpaRepository<Blog, UUID> {
    // Bạn có thể thêm các custom query nếu cần, ví dụ:
    // List<Blog> findByAuthor_Id(UUID authorId);
}
