package com.study.redis.etailor.schema;

import lombok.NonNull;
import lombok.Value;
import lombok.With;
import lombok.extern.flogger.Flogger;

@Flogger
@Value(staticConstructor = "of")
public class Token {

    @NonNull @With private final String name;

}
