package com.study.redis.vote.controller;

import com.study.redis.vote.VoteUtils;
import com.study.redis.vote.schemas.Article;
import com.study.redis.vote.schemas.ArticleDao;
import com.study.redis.vote.schemas.OrderBy;
import com.study.redis.vote.schemas.User;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import redis.clients.jedis.Jedis;

import javax.validation.Valid;
import java.util.List;

@RestController
@Flogger
@RequestMapping(path = "/articles")
public class ArticleController {

    @Autowired
    private Jedis jedis;

    @PostMapping()
    public ResponseEntity<Long> createArticle(@RequestBody @Valid Article article) {
        log.atInfo().log("Creating new Article witj title %s", article.getTitle());
        var articleId = VoteUtils.postArticle(jedis, User.of(article.getUserId()), article);
        var location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/articles/{id}").buildAndExpand(articleId).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ArticleDao getArticle(@PathVariable("id") long articleId) {
        log.atInfo().log("Getting article with id %s", articleId);
        return VoteUtils.getArticle(jedis, articleId);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<ArticleDao> getArticles(@RequestParam(name = "page", defaultValue = "1") int page,
                                        @RequestParam(name = "orderBy", defaultValue = "TIME") OrderBy orderBy) {
        log.atInfo().log("Getting articles from page %s with order %s", page, orderBy);
        return VoteUtils.getArticles(jedis, page, orderBy);
    }

    @DeleteMapping(path = "/{userId}/{id}")
    public ResponseEntity<Boolean> deleteArticle(@PathVariable("userId") Long userId, @PathVariable("id") Long articleId) {
        log.atInfo().log("Deleting Article %s for User %s", articleId, userId);
        var response = VoteUtils.deleteArticle(jedis, User.of(userId), articleId);
        return ResponseEntity.ok(response);
    }
}
