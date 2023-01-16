package com.UserService.Services;



import lombok.RequiredArgsConstructor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.UserService.Entity.Book;
import com.UserService.Entity.BookSubscription;
import com.UserService.Entity.Subscription;
import com.UserService.Entity.User;
import com.UserService.Exception.UserException;
import com.UserService.Repository.SubscribeRepo;
import com.UserService.Repository.UserRepo;

import javax.transaction.Transactional;

import static com.UserService.Services.BookService.getBookDataForBookId;

import java.time.LocalDate;
import java.util.List;

import java.util.stream.Collectors;



@Service
@Transactional
@RequiredArgsConstructor
public class SubscriptionService {
    @Autowired
    private final UserRepo userRepository;

    @Autowired
    private final SubscribeRepo subscriberRepository;

    public Long subscribeToBook(Long userId, Long bookId) {
        User user = checkIfUserAndBookAreValid(userId, bookId);
        user.getSubscriptions().stream()
                .map(Subscription::getBookId)
                .filter(bookId::equals)
                .findFirst()
                .ifPresent(x -> {
                    throw new UserException("Subscribe Error");
                });
        Subscription newSubscription = subscriberRepository.save(Subscription.builder().bookId(bookId).subscriptionDate(LocalDate.now()).build());
        user.getSubscriptions().add(newSubscription);
        return (long) user.getSubscriptions().size();
    }

    public Long unSubscribeToBook(Long userId, Long bookId) {
        User user = checkIfUserAndBookAreValid(userId, bookId);
        user.getSubscriptions().stream()
                .filter(subscription -> bookId.equals(subscription.getBookId()))
                .findFirst()
                .ifPresentOrElse(o -> {
                    LocalDate subscriptionDatePlus24 = o.getSubscriptionDate().plusDays(1);//+24hrs
                    if (!LocalDate.now().isAfter(subscriptionDatePlus24)) {
                        user.getSubscriptions().remove(o);
                        subscriberRepository.delete(o);
                    }else throw new UserException("Subscription Error");
                }, () -> {
                    throw new UserException("Subscription Error");
                });
        return (long) user.getSubscriptions().size();
    }

    public List<BookSubscription> retrieveSubscribedBooksForUser(Long userId) {
        User user = checkIfUserIsValid(userId);
        return user.getSubscriptions().stream()
                .map(subscription -> {
                    Long subscriptionId = subscription.getBookId();
                    Book book = getBookDataForBookId(subscriptionId);
                    return new BookSubscription(book, subscription);
                })
                .collect(Collectors.toList());
    }

    private User checkIfUserAndBookAreValid(Long userId, Long bookId) {
        getBookDataForBookId(bookId);
        return checkIfUserIsValid(userId);
    }

    private User checkIfUserIsValid(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new UserException("Subscriber Error"));
    }
}
