package demo.news.endpoints;

import java.util.List;

import lombok.Data;
import lombok.NonNull;

@Data
public class NewsList {

	@NonNull
	private List<News> newsList;
}
