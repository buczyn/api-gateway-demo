package demo.ratelimitgateway.gateway.news;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@RestController
public class NewsController {

    @Autowired
    private ArticlesService articlesService;

    private WebClient newsClient;

    public NewsController(WebClient.Builder webClientBuilder) {
        this.newsClient = webClientBuilder.baseUrl("http://news-service").build();
    }

    @GetMapping("/news-aggregator")
    public Flux<News> newsAggregator() {
        return newsClient.get()
                // retrieve news IDs from news-service
                .uri("/news/v2")
                .retrieve()
                .bodyToMono(List.class)
                // retrieve articles based on IDs
                .flatMapMany(articlesService::getArticles)
                // make JSON mapping
                .map(News::from);
    }

}

@Service
class ArticlesService {
    private final WebClient articlesClient;

    public ArticlesService(WebClient.Builder webClientBuilder) {
        this.articlesClient = webClientBuilder.baseUrl("http://articles-service").build();
    }

    Flux<Article> getArticles(Collection<String> ids) {
        return Flux.merge(ids.stream().map(this::getArticle).toArray(Mono[]::new));
    }

    private Mono<Article> getArticle(String id) {
        return articlesClient.get()
                .uri("/articles/{id}", id)
                .retrieve()
                .bodyToMono(Article.class);
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Article {
    private String id;
    private String title;
    private String text;
}

@Value
class News {
    private String id;
    private String title;
    private String content;

    static News from(Article article) {
        return new News(article.getId(), article.getTitle(), article.getText());
    }

    static News from(Object article) {
        if (article instanceof Article) {
            return from((Article) article);
        }
        return null;
    }
}