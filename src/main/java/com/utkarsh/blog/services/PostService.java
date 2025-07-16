package com.utkarsh.blog.services;

import com.utkarsh.blog.models.Post;
import com.utkarsh.blog.models.Tag;
import com.utkarsh.blog.models.User;
import com.utkarsh.blog.repositories.PostRepository;
import com.utkarsh.blog.repositories.TagRepository;
import com.utkarsh.blog.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository, UserRepository userRepository){
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    public Page<Post> getPosts(int page,int size, String sortField, String sortDirection,
                               List<Integer> tagIds, List<String> selectedAuthors){
        Sort sort;
        if(sortDirection.equals("desc")){
            sort = Sort.by(sortField).descending();
        }else{
            sort = Sort.by(sortField).ascending();
        }
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<Post> postPage;

        if((tagIds == null || tagIds.isEmpty()) && (selectedAuthors == null || selectedAuthors.isEmpty())){
            postPage = postRepository.findAll(pageable);
        } else if (selectedAuthors==null || selectedAuthors.isEmpty()){
            postPage = postRepository.findByFilteredTags(tagIds,pageable);
        } else if (tagIds==null || tagIds.isEmpty()){
            postPage = postRepository.findByFilteredAuthor(selectedAuthors,pageable);
        } else {
            postPage = postRepository.findByFilteredAuthorAndTags(tagIds,selectedAuthors,pageable);
        }
       return postPage;
    }

    public void addPost(Post post , List<Integer> tagIds, Principal principal) {
        String userEmail = principal.getName();
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if(userOptional.isPresent()){
            post.setAuthor(userOptional.get());
        }
        post.setPublishedAt(new Date());
        post.setPublished(true);
        post.setCreatedAt(new Date());
        if(tagIds!=null){
            List<Tag> tags = tagRepository.findAllById(tagIds);
            post.setTags(tags);
        }
        postRepository.save(post);
    }

    public void deletePost(Integer postId) {
        postRepository.deleteById(postId);
    }

    public Post getPostById(Integer postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if(postOptional.isPresent()) {
            return postOptional.get();
        } else {
            return null;   //TODO : Throw new post now found exception
        }
    }

    public void updatePost(Post post, List<Integer> selectedTagIds) {
        Post oldPost = postRepository.findById(post.getId())
                    .orElseThrow(() -> new RuntimeException("Comment not found"));
        post.setPublishedAt(oldPost.getPublishedAt());
        post.setCreatedAt(oldPost.getCreatedAt());
        post.setUpdatedAt(new Date());
        if(oldPost.getComments() != null) {
            post.setComments(oldPost.getComments());
        }

        List<Tag> tags = new ArrayList<>();
        for(int tagId : selectedTagIds){
            Optional<Tag> tagOptional = tagRepository.findById(tagId);
            if(tagOptional.isPresent()){
                Tag tag = tagOptional.get();
                if(tag.getPosts()==null){
                    tag.setPosts(new ArrayList<>());
                }
                tag.getPosts().add(post);
                tags.add(tag);
            }
        }
        post.setTags(tags);
        postRepository.save(post);
    }

    public Page<Post> getPostBySearch(String keyword,int page, int size,
                                      List<Integer> selectedTagIds, List<String> selectedAuthors){
        Sort sort = Sort.by("publishedAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        if((selectedTagIds == null || selectedTagIds.isEmpty()) &&
                (selectedAuthors == null || selectedAuthors.isEmpty())){
            return postRepository.findPostBySearch(keyword,pageable);
        } else if(selectedTagIds == null || selectedTagIds.isEmpty()){
            return postRepository.findPostBySearchAndSelectedAuthors(keyword, selectedAuthors, pageable);
        } else if(selectedAuthors == null || selectedAuthors.isEmpty()){
            return postRepository.findPostBySearchAndSelectedTagIds(keyword, selectedTagIds, pageable);
        } else {
            return postRepository.findPostBySearchAndAuthorAndTag(keyword, selectedAuthors,
                                          selectedTagIds, pageable);
        }
    }

    public void addPostWithAuthor(Post post, List<Integer> selectedTagIds, String selectedAuthor) {
        Optional<User> authorOptional = userRepository.findByName(selectedAuthor);

        if(authorOptional.isPresent()){
            post.setAuthor(authorOptional.get());
        }
        post.setPublishedAt(new Date());
        post.setPublished(true);
        post.setCreatedAt(new Date());
         if(selectedTagIds !=null){
             List<Tag> tags = tagRepository.findAllById(selectedTagIds);
             post.setTags(tags);
         }
        postRepository.save(post);
    }
}
