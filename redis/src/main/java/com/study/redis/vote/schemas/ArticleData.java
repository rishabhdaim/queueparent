package com.study.redis.vote.schemas;

import lombok.NonNull;
import lombok.Value;

@Value
public class ArticleData {
    @NonNull private final String title;
    @NonNull private final String link;
}
