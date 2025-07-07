package com.utkarsh.blog.controllers;

import com.utkarsh.blog.models.Post;
import com.utkarsh.blog.models.Tag;
import com.utkarsh.blog.services.PostService;
import com.utkarsh.blog.services.TagService;
import org.springframework.data.domain.Page;
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
    public String getPosts(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(defaultValue = "publishedAt") String sortField,
                           @RequestParam(defaultValue = "desc") String sortDir,
                           @RequestParam(value = "tagIds", required = false) List<Integer> tagIds,
                           Model model){

        Page<Post> postPage = postService.getPosts(page,size,sortField,sortDir,tagIds);
        List<Tag> tags = tagService.getTags();

        model.addAttribute("posts",postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());

        model.addAttribute("tags",tags);
        model.addAttribute("selectedTagIds", tagIds);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

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

    @GetMapping("/{id}")
    public String getPost(@PathVariable Integer id, Model model){
        Post post = postService.getPostById(id);
        model.addAttribute("post",post);
        return "view-post";
    }

    @GetMapping("/edit/{id}")
    public String editPost(@PathVariable Integer id,Model model){
        Post post = postService.getPostById(id);
        List<Tag> tagList = tagService.getTags();
        model.addAttribute("tagList",tagList);
        model.addAttribute("post",post);
        return "edit-post";
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute Post post,
                             @RequestParam("selectedTagIds") List<Integer> selectedTagIds){
        postService.updatePost(post, selectedTagIds);
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/search")
    public String getPostBySearch(@RequestParam("keyword") String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "9") int size,
                                  Model model){
        Page<Post> postPage = postService.getPostBySearch(keyword, page, size);

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage",page);
        model.addAttribute("totalPages",postPage.getTotalPages());
        model.addAttribute("keyword",keyword);

        return "view-searched-posts";
    }
}
