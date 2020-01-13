package com.study.redis.vote.controller;

import com.study.redis.vote.VoteUtils;
import com.study.redis.vote.schemas.User;
import com.study.redis.vote.schemas.Vote;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

@RestController
@RequestMapping(path = "/vote")
@Flogger
public class VotingController {

    @Autowired
    private Jedis jedis;

    @PostMapping(path = "/up/{userId}/{id}")
    public ResponseEntity<Boolean> upVoteArticle(@PathVariable Long userId, @PathVariable Long id) {
        var response = VoteUtils.voteArticle(jedis, User.of(userId), id, Vote.UP_VOTE);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/down/{userId}/{id}")
    public ResponseEntity<Boolean> downVoteArticle(@PathVariable Long userId, @PathVariable Long id) {
        var response = VoteUtils.voteArticle(jedis, User.of(userId), id, Vote.DOWN_VOTE);
        return ResponseEntity.ok(response);
    }
}
