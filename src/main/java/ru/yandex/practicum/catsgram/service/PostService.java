package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

@Service
public class PostService {

    private final UserService userService;
    private final Map<Long, Post> posts = new HashMap<>();

    public PostService(UserService userService) {
        this.userService = userService;
    }

    public Collection<Post> findAll(String sort, String size, String from) {
        List<Post> postForFind = posts.values().stream().toList().subList(parseInt(from) - 1, posts.size());
        if (sort.equals("asc")) {
            return postForFind.stream().sorted((post1, post2) -> {
                if (post1.equals(post2)) {
                    return 0;
                } else if (post1.getPostDate() == null) {
                    return 1;
                } else if (post2.getPostDate() == null) {
                    return -1;
                } else if (post1.getPostDate().getEpochSecond() - post2.getPostDate().getEpochSecond() > 0) {
                    return -1;
                }
                return 1;
            }).limit((parseInt(size))).collect(Collectors.toSet());
        }

        if (sort.equals("desc")) {
            return postForFind.stream().sorted((post1, post2) -> {
                if (post1.equals(post2)) {
                    return 0;
                } else if (post1.getPostDate() == null) {
                    return 1;
                } else if (post2.getPostDate() == null) {
                    return -1;
                } else if (post1.getPostDate().getEpochSecond() - post2.getPostDate().getEpochSecond() > 0) {
                    return 1;
                }
                return -1;
            }).limit((parseInt(size))).collect(Collectors.toSet());
        }

        return null;

    }

    public Post create(Post post) {
        User postAuthor = userService.findUserById(post.getAuthorId());
        if (postAuthor == null) {
            throw new ConditionsNotMetException(String.format(
            "Пользователь %s не найден",
            post.getAuthorId()));
        }
        // проверяем выполнение необходимых условий
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
        // формируем дополнительные данные
        post.setId(getNextId());
        post.setPostDate(Instant.now());
        // сохраняем новую публикацию в памяти приложения
        posts.put(post.getId(), post);

        return post;
    }

    public Post update(Post newPost) {
        // проверяем необходимые условия
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            // если публикация найдена и все условия соблюдены, обновляем её содержимое
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    public Post findPostById(Long id) {
        return posts.get(id);

    }

    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}