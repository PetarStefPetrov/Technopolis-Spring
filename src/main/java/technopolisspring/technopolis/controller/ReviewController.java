package technopolisspring.technopolis.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.exception.BadRequestException;
import technopolisspring.technopolis.exception.InvalidArgumentsException;
import technopolisspring.technopolis.exception.NotFoundException;
import technopolisspring.technopolis.model.daos.ReviewDao;
import technopolisspring.technopolis.model.dto.EditReviewDto;
import technopolisspring.technopolis.model.dto.ReviewOfUserDto;
import technopolisspring.technopolis.model.dto.UserWithoutPasswordDto;
import technopolisspring.technopolis.model.pojos.Review;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@RestController
public class ReviewController extends AbstractController {

    private static final String REVIEW_NOT_FOUND = "Review not found";
    private static final String DELETE_YOUR_OWN_REVIEWS = "You can only delete your own reviews!";
    private static final String INVALID_REVIEW = "Invalid review";
    @Autowired
    private ReviewDao reviewDao;

    @SneakyThrows
    @PostMapping("users/reviews/{productId}")
    public Review addReview(@RequestBody Review review, HttpSession session, @PathVariable long productId) {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session);
        if(!reviewDao.addReview(review, productId, user)){
            throw new InvalidArgumentsException(ProductController.INVALID_PRODUCT);
        }
        return review;
    }

    @SneakyThrows
    @GetMapping("users/reviews/page")
    public List<ReviewOfUserDto> getReviewsOfUser(HttpSession session,
                                                  @RequestParam(defaultValue = DEFAULT_PAGE) int pageNumber) {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session);
        return reviewDao.getReviewsOfUser(user.getId(), pageNumber);
    }

    @PutMapping("users/reviews")
    public EditReviewDto editReview(@RequestBody EditReviewDto review, HttpSession session) throws SQLException {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session);
        review.setUserId(user.getId());
        if (!reviewDao.editReview(review)){
            throw new InvalidArgumentsException(INVALID_REVIEW);
        }
        return review;
    }

    @DeleteMapping("users/reviews/{reviewId}")
    public Review deleteReview(HttpSession session, @PathVariable long reviewId) throws SQLException {
        UserWithoutPasswordDto user = checkIfUserIsLogged(session);
        Review review = reviewDao.getReviewById(reviewId);
        if (review == null){
            throw new NotFoundException(REVIEW_NOT_FOUND);
        }
        if (review.getUserId() != user.getId()){
            throw new BadRequestException(DELETE_YOUR_OWN_REVIEWS);
        }
        reviewDao.deleteReview(reviewId);
        return review;
    }

    @GetMapping("products/{productId}/reviews/page")
    public List<Review> getReviewsOfProduct(@PathVariable long productId,
                                            @RequestParam(defaultValue = DEFAULT_PAGE) int pageNumber){
        return reviewDao.getReviewsOfProduct(productId, pageNumber);
    }

}
