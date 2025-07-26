package com.group3.backend.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.group3.backend.dto.request.BlogRequest;
import com.group3.backend.exception.ResourceNotFoundException;
import com.group3.backend.model.Blog;
import com.group3.backend.model.User;
import com.group3.backend.repository.BlogRepository;
import com.group3.backend.repository.UserRepository;

@Service
public class BlogService {
    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserRepository userRepository;

    public Blog saveBlog(BlogRequest blogRequest){
        Optional<User> authorOptional = userRepository.findById(blogRequest.getAuthorId());
        if (authorOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        Blog blog;
        if(blogRequest.getId().isEmpty()){
            blog = new Blog();
            blog.setAuthor(authorOptional.get());
        }else{
            blog = blogRepository.findById(blogRequest.getId().get())
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found"));
        }
        blog.setTitle(blogRequest.getTitle());
        blog.setContent(blogRequest.getContent());
        blog.setThumbnailUrl(blogRequest.getThumbnailUrl());

        return blogRepository.save(blog);
    }

    public Page<Blog> getBlogsByTitle(String title, Pageable pageable){
        return blogRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    public Blog getBlogById(UUID id){
        return blogRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Blog not found"));
    }
}
