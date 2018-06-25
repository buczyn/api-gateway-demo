package demo.news.endpoints;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NewsController {

	private ArticlesClient articlesClient;
	private int defaultSize;

	@Autowired
	public NewsController(ArticlesClient articlesClient, @Value("${news.top.defaultSize}") int defaultSize) {
		this.articlesClient = articlesClient;
		this.defaultSize = defaultSize;
	}

	@RequestMapping(path = "/news", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public NewsList getTopNews(@RequestParam(name = "size", required = false) Integer size) {
		return generateTopNews(size == null ? defaultSize : size);

	}

	@RequestMapping(path = "/news/v2", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getTopNewsV2(@RequestParam(name = "size", required = false) Integer size) {
		return generateTopNewsIds(size == null ? defaultSize : size);
	}

	private List<String> generateTopNewsIds(int size) {
		Random random = new Random();
		return IntStream.range(0, size)
				.map(i -> random.nextInt(100))
				.mapToObj(Integer::toString)
				.collect(Collectors.toList());
	}

	private NewsList generateTopNews(int size) {
		List<News> newsList = new ArrayList<>();
		for (String id: generateTopNewsIds(size)) {
			Article article = articlesClient.getArticle(id);
			newsList.add(new News(article.getId(), article.getTitle(), article.getText()));
		}
		return new NewsList(newsList);
	}

}
