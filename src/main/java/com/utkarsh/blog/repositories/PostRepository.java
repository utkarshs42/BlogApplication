package com.utkarsh.blog.repositories;

import com.utkarsh.blog.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN p.tags t WHERE " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "+
            "LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "+
            "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Post> findPostBySearch(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN p.tags t WHERE " +
             "t.id IN :tagIds")
    Page<Post> findByFilteredTags(@Param("tagIds") List<Integer> tagIds, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p WHERE p.author.name IN :selectedAuthors")
    Page<Post> findByFilteredAuthor(@Param("selectedAuthors")List<String> selectedAuthors, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN p.tags t WHERE " +
            "t.id IN :tagIds AND p.author.name IN :selectedAuthors")
    Page<Post> findByFilteredAuthorAndTags(@Param("tagIds")List<Integer> tagIds,
                                           @Param("selectedAuthors")List<String> selectedAuthors,
                                           Pageable pageable);


    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN p.tags t WHERE " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "+
            "LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "+
            "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "p.author.name IN :selectedAuthors")
    Page<Post> findPostBySearchAndSelectedAuthors(@Param("keyword") String keyword,
                                                  @Param("selectedAuthors") List<String> selectedAuthors,
                                                  Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags t WHERE " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "+
            "LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "+
            "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "t.id IN :selectedTagIds")
    Page<Post> findPostBySearchAndSelectedTagIds(@Param("keyword") String keyword,
                                                 @Param("selectedTagIds") List<Integer> selectedTagIds,
                                                 Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags t WHERE " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "+
            "LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "+
            "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ) AND " +
            "t.id IN :selectedTagIds AND " +
            "p.author.name IN :selectedAuthors")
    Page<Post> findPostBySearchAndAuthorAndTag(@Param("keyword") String keyword,
                                               @Param("selectedAuthors") List<String> selectedAuthors,
                                               @Param("selectedTagIds") List<Integer> selectedTagIds,
                                               Pageable pageable);
}
