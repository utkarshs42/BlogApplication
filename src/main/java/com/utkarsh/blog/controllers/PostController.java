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
    private final PostService postService;
    private final TagService tagService;

    public PostController(PostService postService, TagService tagService){
        this.postService = postService;
        this.tagService = tagService;
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
        List<String> authors = postService.getAuthors();

        model.addAttribute("posts",postPage.getContent());
        model.addAttribute("keyword",keyword);

        model.addAttribute("tags",tags);
        model.addAttribute("authors",authors);
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
        Post post = new Post();
        model.addAttribute("post", post);
        List<Tag> tagList = tagService.getTags();
        model.addAttribute("tagList",tagList);
        return "posts/new";
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute("post") Post post,
                          @RequestParam("selectedTagIds") List<Integer> selectedTagIds){
        postService.addPost(post, selectedTagIds);
        return "redirect:/posts";
    }

    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable Integer postId){
        postService.deletePost(postId);
        return "redirect:/posts";
    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable Integer id, Model model){
        Post post = postService.getPostById(id);
        model.addAttribute("post",post);
        return "posts/view";
    }

    @GetMapping("/edit/{id}")
    public String getEditPostPage(@PathVariable Integer id, Model model){
        Post post = postService.getPostById(id);
        List<Tag> tagList = tagService.getTags();
        model.addAttribute("tagList",tagList);
        model.addAttribute("post",post);
        return "posts/edit";
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
