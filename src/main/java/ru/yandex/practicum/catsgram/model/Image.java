package ru.yandex.practicum.catsgram.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class Image {
    @EqualsAndHashCode.Include
    private Long id;
    private long postId;
    private String originalFileName;
    private String filePath;
}
