package com.example.blogMs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blogMs.entities.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
