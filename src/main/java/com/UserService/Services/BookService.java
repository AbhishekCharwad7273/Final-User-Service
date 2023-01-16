package com.UserService.Services;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.UserService.Entity.Book;
import com.UserService.Entity.User;
import com.UserService.Exception.UserException;
import com.UserService.Repository.UserRepo;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class BookService {
	private static final String BOOK_REPO_URL = "http://localhost:9090/api/v1/digitalbooks/";

	private final UserRepo userRepository;

	public static Book getBookDataForBookId(Long bookId) {
		String retrieveBookUrl = BOOK_REPO_URL + "books/" + bookId;
		ResponseEntity<Book> bookResponse = new RestTemplate().getForEntity(retrieveBookUrl, Book.class);
		return Objects.requireNonNull(bookResponse.getBody());
	}

	public List<Book> callBookServiceToGetAllBooks() {
		ResponseEntity<Book[]> allBooks = new RestTemplate().getForEntity(BOOK_REPO_URL + "books", Book[].class);
		return Arrays.stream(Objects.requireNonNull(allBooks.getBody())).collect(Collectors.toList());
	}

	public Long addBook(Long authorId, Book book) {
		User user = userRepository.findById(authorId).orElseThrow(() -> new UserException("AuthorID is invalid"));
		if (!user.isAuthorUser())
			throw new UserException("User is not an author");

		String addBookUrl = BOOK_REPO_URL + authorId + "/books";
		ResponseEntity<Long> addedBook = new RestTemplate().postForEntity(addBookUrl, book, Long.class);
		return addedBook.getBody();
	}

	public Long updateBook(Long authorId, Long bookId, Book book) {

		User user = userRepository.findById(authorId).orElseThrow(() -> new UserException("AuthorID is invalid"));
		if (!user.isAuthorUser())
			throw new UserException("User is not an author");
		searchBooksByAuthorId(authorId).stream().map(Book::getBookId).filter(bookId::equals).findFirst()
				.orElseThrow(() -> new UserException("Book Update Errors!!! Book Not Present In Table"));
		String updateBookUrl = BOOK_REPO_URL + authorId + "/books/" + bookId;
		ResponseEntity<Long> updatedBook = new RestTemplate().exchange(updateBookUrl, HttpMethod.PUT,
				new HttpEntity<>(book), Long.class);
		return updatedBook.getBody();
	}

	public List<Book> searchBooksByAuthorId(Long authorId) {
		User user = userRepository.findById(authorId).orElseThrow(() -> new UserException("Author Not Found"));
		if (!user.isAuthorUser())
			throw new UserException("Book Search Error");

		String targetUrl = BOOK_REPO_URL + authorId + "/books";
		ResponseEntity<Book[]> booksByAuthor = new RestTemplate().getForEntity(targetUrl, Book[].class);
		return List.of(Objects.requireNonNull(booksByAuthor.getBody()));
	}

	public List<Book> searchUsingQuery(String category, String title, String publisher) {
		String targetUrl = BOOK_REPO_URL
				+ "search?category={category}&title={title}&publisher={publisher}";
		HashMap<String, String> bookSearchRequestParameters = new HashMap<>();
		bookSearchRequestParameters.put("category", category);
		bookSearchRequestParameters.put("title", title);
//		bookSearchRequestParameters.put("price", price);
		bookSearchRequestParameters.put("publisher", publisher);
		ResponseEntity<Book[]> allBooksWithoutAuthorSearch = new RestTemplate().getForEntity(targetUrl, Book[].class,
				bookSearchRequestParameters);
//		List<Book> booksOfSameAuthor = userRepository.findByNameContainsIgnoreCaseAllIgnoreCase(author).stream()
//				.filter(User::isAuthorUser).map(User::getId).map(this::searchBooksByAuthorId)
//				.flatMap(Collection::stream).collect(Collectors.toList());
		return Arrays.stream(allBooksWithoutAuthorSearch.getBody())
				.distinct().collect(Collectors.toList());
	}

	public Long toggleBookBlock(Long authorId, Long bookId, boolean block) {

		User user = userRepository.findById(authorId).orElseThrow(() -> new UserException("Block Error!"));
		if (!user.isAuthorUser())
			throw new UserException("Block Error: User is not Author");

		searchBooksByAuthorId(authorId).stream().map(Book::getBookId).filter(bookId::equals).findFirst()
				.orElseThrow(() -> new UserException("Block Update Error Author trying To Block Other Author Book"));

		String targetUrl = BOOK_REPO_URL + authorId + "/books/" + bookId + "?block={block}";
		HashMap<String, Boolean> blockRequestParameter = new HashMap<>();
		blockRequestParameter.put("block", block);
		ResponseEntity<Long> toggleBookBlockResponse = new RestTemplate().getForEntity(targetUrl, Long.class,
				blockRequestParameter);
		return toggleBookBlockResponse.getBody();
	}
}
