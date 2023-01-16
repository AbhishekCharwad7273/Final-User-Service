package com.UserService.Entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {

	
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long bookId;
	    private String title;
	    private String category;
	    private Double price;
	    private Long authorId;
	    private String publisher;
	    private LocalDate publishedDate;
	    private String content;
	    private Boolean blocked;

}
