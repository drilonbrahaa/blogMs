package com.example.blogMs.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.blogMs.entities.Post;
import com.example.blogMs.entities.User;
import com.example.blogMs.repositories.PostRepository;
import com.example.blogMs.repositories.UserRepository;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    PostRepository postRepository;
    
    @Autowired
    UserRepository userRepository;
    
    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
    
    @PostMapping
    public Post createPost(@RequestBody Post post, @RequestParam String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent()) {
            post.setAuthor(user.get());
            return postRepository.save(post);
        }
        throw new RuntimeException("User not found");
    }
    
    @PutMapping("/{id}")
    public Post updatePost(@PathVariable Long id, @RequestBody Post updatedPost) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
        post.setContent(updatedPost.getContent());
        return postRepository.save(post);
    }
    
    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable Long id) {
        postRepository.deleteById(id);
        return "Post deleted successfully";
    }
    
    @GetMapping("/search")
    public List<Post> searchPosts(@RequestParam String keyword) {
        return postRepository.findByTitleContaining(keyword);
    }
}
