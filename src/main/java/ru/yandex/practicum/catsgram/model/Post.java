package ru.yandex.practicum.catsgram.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
public class Post {
    @EqualsAndHashCode.Include
    private  Long id;
    private long authorId;
    private String description;
    private Instant postDate;
}
