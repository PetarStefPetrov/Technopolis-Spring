package technopolisspring.technopolis.utils;

import org.springframework.stereotype.Component;
import technopolisspring.technopolis.controller.UserController;
import technopolisspring.technopolis.model.dto.ChangePasswordDto;
import technopolisspring.technopolis.model.dto.EditUserDto;
import technopolisspring.technopolis.model.dto.RegisterUserDto;

@Component
public class ValidationUtil {
    private static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+\\/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+\\/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b" +
            "\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9]" +
            "(?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)" +
            "\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f" +
            "\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
    private static final String PHONE_REGEX ="^((\\+)|(00)|(\\*)|())[0-9]{9,14}((\\#)|())$";
    private static final String NAME_REGEX = "^[a-zA-Z._-]{3,}$";
    private static final Integer ADDRESS_LENGTH = 6;
    private static final String INVALID_ADDRESS = "Address must be at least " + ADDRESS_LENGTH + " symbols";
    private static final String PASSWORD_REQUIREMENTS = "Password must have one upper and lower letter, " +
            "one number and no spaces and it must be at least 8 symbols";
    private static final String INVALID_LAST_NAME = "Invalid last name";
    private static final String INVALID_FIRST_NAME = "Invalid first name";
    private static final String INVALID_EMAIL = "Invalid email";
    private static final String INVALID_PHONE_NUMBER = "Invalid phone number";
    private static final int MIN_PAGE_NUMBER = 1;

    public String checkUser(RegisterUserDto user) {
        String msg = checkEmail(user.getEmail());
        if( msg != null){
            return msg;
        }
        msg = checkFirstName(user.getFirstName());
        if( msg != null){
            return msg;
        }
        msg = checkLastName(user.getLastName());
        if( msg != null){
            return msg;
        }
        msg = checkPassword(user.getPassword());
        if( msg != null){
            return msg;
        }
        msg = checkConfirmPassword(user.getPassword(), user.getConfirmPassword());
        if( msg != null){
            return msg;
        }
        msg = checkPhone(user.getPhone());
        if( msg != null){
            return msg;
        }
        msg = checkAddress(user.getAddress());
        return msg;
    }

    public String checkUser(EditUserDto user){
        String msg = checkEmail(user.getEmail());
        if( msg != null){
            return msg;
        }
        msg = checkFirstName(user.getFirstName());
        if( msg != null){
            return msg;
        }
        msg = checkLastName(user.getLastName());
        if( msg != null){
            return msg;
        }
        msg = checkPhone(user.getPhone());
        if( msg != null){
            return msg;
        }
        msg = checkAddress(user.getAddress());
        return msg;
    }

    public String checkUser(ChangePasswordDto user) {
        String msg = checkPassword(user.getNewPassword());
        if( msg != null){
            return msg;
        }
        return msg;
    }

    private String checkAddress(String address) {
        if(address.length() < ADDRESS_LENGTH){
            return INVALID_ADDRESS;
        }
        return null;
    }

    private String checkPhone(String phone) {
        if(!phone.matches(PHONE_REGEX)){
            return INVALID_PHONE_NUMBER;
        }
        return null;
    }

    private String checkConfirmPassword(String password, String confirmPassword) {
        if(!password.equals(confirmPassword)){
            return UserController.PASSWORDS_DONT_MATCH;
        }
        return null;
    }

    private String checkPassword(String password) {
        if(!password.matches(PASSWORD_REGEX)){
            return PASSWORD_REQUIREMENTS;
        }
        return null;
    }

    private String checkLastName(String lastName) {
        if(!lastName.matches(NAME_REGEX)){
            return INVALID_LAST_NAME;
        }
        return null;
    }

    private String checkFirstName(String firstName) {
        if(!firstName.matches(NAME_REGEX)){
            return INVALID_FIRST_NAME;
        }
        return null;
    }

    private String checkEmail(String email){
        if(!email.matches(EMAIL_REGEX)){
            return INVALID_EMAIL;
        }
        return null;
    }

    public boolean validId(long id){
        return id >= 1;
    }

    public int validatePageNumber(int pageNumber){
        if (pageNumber < MIN_PAGE_NUMBER){
            return MIN_PAGE_NUMBER;
        }
        return pageNumber;
    }

}
