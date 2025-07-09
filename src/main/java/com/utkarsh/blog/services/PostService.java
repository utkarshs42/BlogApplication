package com.utkarsh.blog.services;

import com.utkarsh.blog.models.Post;
import com.utkarsh.blog.models.Tag;
import com.utkarsh.blog.repositories.PostRepository;
import com.utkarsh.blog.repositories.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private PostRepository postRepository;
    private TagRepository tagRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository){
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    public Page<Post> getPosts(int page,int size, String sortField, String sortDir,
                               List<Integer> tagIds){
        Sort sort;
        if(sortDir.equals("desc")){
            sort = Sort.by(sortField).descending();
        }else{
            sort = Sort.by(sortField).ascending();
        }
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<Post> postPage;
        if(tagIds==null||tagIds.isEmpty()){
            postPage = postRepository.findAll(pageable);
        }else{
            postPage = postRepository.findByFilteredTags(tagIds,pageable);
        }

       return postPage;
    }

    public void addPost(Post post , List<Integer> tagIds) {
        post.setPublishedAt(new Date());
        post.setPublished(true);
        post.setCreatedAt(new Date());

        List<Tag> tags = new ArrayList<>();
        for(int tagId : tagIds){
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

    public void deletePost(Integer id) {
        postRepository.deleteById(id);
    }

    public Post getPostById(Integer id) {
        Optional<Post> postOptional = postRepository.findById(id);
        if(postOptional.isPresent()){
            return postOptional.get();
        }else{
            return null;   //TODO : Throw new post now found exception
        }
    }

    public void updatePost(Post post, List<Integer> selectedTagIds) {
        Post oldPost = postRepository.findById(post.getId())
                    .orElseThrow(() -> new RuntimeException("Comment not found"));
        post.setPublishedAt(oldPost.getPublishedAt());
        post.setCreatedAt(oldPost.getCreatedAt());
        post.setUpdatedAt(new Date());
        if(oldPost.getComments() != null){
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

    public Page<Post> getPostBySearch(String keyword,int page, int size){
        Sort sort = Sort.by("publishedAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Post> postPage = postRepository.findPostBySearch(keyword,pageable);
        return postPage;
    }
}
