package com.study.redis.vote.controller;

import com.study.redis.constants.RedisConstants;
import com.study.redis.vote.VoteUtils;
import com.study.redis.vote.schemas.Article;
import com.study.redis.vote.schemas.ArticleData;
import com.study.redis.vote.schemas.OrderBy;
import com.study.redis.vote.schemas.User;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Flogger
@RequestMapping(path = "/article")
public class ArticleController {

    @Autowired
    private Jedis jedis;

    @PostMapping(path = "/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public long createArticle(@PathVariable Long userId, @RequestBody ArticleData articleData) {
        log.atInfo().log("Creating new Article");
        return VoteUtils.postArticle(jedis, User.of(userId), articleData);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Article getArticle(@PathVariable("id") long articleId) {
        log.atInfo().log("Getting article with id %s", articleId);
        return VoteUtils.getArticle(jedis, articleId);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<Article> getArticles(@RequestParam(name = "page", defaultValue = "1") int page,
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
