package demo.news.endpoints;

import lombok.NonNull;
import lombok.Value;

@Value
public class News {
	@NonNull
	private String id;

	@NonNull
	private String title;
	
	@NonNull
	private String content;
}
