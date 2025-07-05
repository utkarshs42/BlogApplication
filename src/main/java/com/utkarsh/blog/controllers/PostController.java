package com.utkarsh.blog.controllers;

import com.utkarsh.blog.models.Post;
import com.utkarsh.blog.models.Tag;
import com.utkarsh.blog.services.PostService;
import com.utkarsh.blog.services.TagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {
    private PostService postService;
    private TagService tagService;

    public PostController(PostService postService, TagService tagService){
        this.postService = postService;
        this.tagService = tagService;
    }

    @GetMapping
    public String getPosts(Model model){
        List<Post> posts = postService.getPosts();
        model.addAttribute("posts",posts);
        return "homePage";
    }

    @GetMapping("/new-post")
    public String newPost(Model model){
        Post post = new Post();
        model.addAttribute("post", post);
        List<Tag> tagList = tagService.getTags();
        model.addAttribute("tagList",tagList);
        return "new-post";
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute("post") Post post,
                          @RequestParam("selectedTagIds") List<Integer> selectedTagIds){
        postService.addPost(post, selectedTagIds);
        return "redirect:/posts";
    }

    @PostMapping("/deletePost")
    public String deletePost(@RequestParam("id") Integer id){
        postService.deletePost(id);
        return "redirect:/posts";
    }

}
