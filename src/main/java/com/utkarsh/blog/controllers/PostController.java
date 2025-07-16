package com.utkarsh.blog.controllers;

import com.utkarsh.blog.models.Post;
import com.utkarsh.blog.models.Tag;
import com.utkarsh.blog.models.User;
import com.utkarsh.blog.services.PostService;
import com.utkarsh.blog.services.TagService;
import com.utkarsh.blog.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final TagService tagService;
    private final UserService userService;

    public PostController(PostService postService, TagService tagService, UserService userService){
        this.postService = postService;
        this.tagService = tagService;
        this.userService = userService;
    }

    @GetMapping
    public String getPosts(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(defaultValue = "publishedAt") String sortField,
                           @RequestParam(defaultValue = "desc") String sortDirection,
                           @RequestParam(value="keyword",required = false) String keyword,
                           @RequestParam(value = "tagIds", required = false) List<Integer> selectedTagIds,
                           @RequestParam(value = "selectedAuthors",required = false) List<String> selectedAuthors,
                           Model model){
        Page<Post> postPage;
        if (keyword != null && !keyword.isBlank()) {
            postPage = postService.getPostBySearch(keyword, page, size, selectedTagIds, selectedAuthors);
        } else {
            postPage = postService.getPosts(page, size, sortField, sortDirection, selectedTagIds, selectedAuthors);
        }
        List<Tag> tags = tagService.getTags();
        List<String> authors = userService.getAllAuthorsName();

        model.addAttribute("posts",postPage.getContent());
        model.addAttribute("keyword",keyword);

        model.addAttribute("tags",tags);
        model.addAttribute("authors", authors);
        model.addAttribute("selectedTagIds", selectedTagIds);
        model.addAttribute("selectedAuthors",selectedAuthors);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());

        return "posts/home";
    }

    @GetMapping("/new-post")
    public String getNewPostForm(Model model){
        List<Tag> tagList = tagService.getTags();
        List<String> authorNames = userService.getAllAuthorsName();

        model.addAttribute("authorNames",authorNames);
        model.addAttribute("tagList",tagList);
        model.addAttribute("post", new Post());
        return "posts/new";
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute("post") Post post,
                          @RequestParam("selectedTagIds") List<Integer> selectedTagIds,
                          @RequestParam(value = "selectedAuthor", required = false) String selectedAuthor,
                          Principal principal){

        if(selectedAuthor == null || selectedAuthor.isEmpty()){
            postService.addPost(post, selectedTagIds, principal);
        }else{
            postService.addPostWithAuthor(post, selectedTagIds, selectedAuthor);
        }
        return "redirect:/posts";
    }

    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable Integer postId, Principal principal){
        Post post = postService.getPostById(postId);
        User currentUser = userService.findByEmail(principal.getName());
        if (!post.getAuthor().getEmail().equals(currentUser.getEmail())
                && !currentUser.getRole().equals("ROLE_ADMIN")) {
            return "error/403";
        }else{
            postService.deletePost(postId);
            return "redirect:/posts";
        }
    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable Integer id, Model model){
        Post post = postService.getPostById(id);
        model.addAttribute("post",post);
        return "posts/view";
    }

    @GetMapping("/edit/{id}")
    public String getEditPostPage(@PathVariable Integer id, Principal principal ,Model model){
        Post post = postService.getPostById(id);
        User currentUser = userService.findByEmail(principal.getName());
        if (!post.getAuthor().getEmail().equals(currentUser.getEmail())
                && !currentUser.getRole().equals("ROLE_ADMIN")) {
            return "error/403";
        }else{
            List<Tag> tagList = tagService.getTags();
            model.addAttribute("tagList",tagList);
            model.addAttribute("post",post);
            return "posts/edit";
        }
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute Post post,
                             @RequestParam("selectedTagIds") List<Integer> selectedTagIds){
        postService.updatePost(post, selectedTagIds);
        return "redirect:/posts/" + post.getId();
    }

   /* @GetMapping("/search")
    public String getPostBySearch(@RequestParam("keyword") String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "9") int size,
                                  Model model){
        Page<Post> postPage = postService.getPostBySearch(keyword, page, size);
        List<Tag> tags = tagService.getTags();

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage",page);
        model.addAttribute("totalPages",postPage.getTotalPages());
        model.addAttribute("keyword",keyword);

        model.addAttribute("tags",tags);
        model.addAttribute("selectedTagIds", null);

        model.addAttribute("sortField", "publishedAt");
        model.addAttribute("sortDir", "desc");

        return "posts/home";
    }*/
}
