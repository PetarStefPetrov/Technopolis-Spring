package technopolisspring.technopolis.service;

import org.springframework.stereotype.Component;
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
    private static final String PHONE_REGEX ="^((\\+)|(00)|(\\*)|())[0-9]{3,14}((\\#)|())$";
    private static final String NAME_REGEX = "^[a-zA-Z._-]{3,}$";
    private static final Integer ADDRESS_LENGTH = 6;
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
        msg = checkConfirmPassword(user.getPassword(),user.getConfirmPassword());
        if( msg != null){
            return msg;
        }
        msg = checkPhone(user.getPhone());
        if( msg != null){
            return msg;
        }
        msg = checkAddress(user.getAddress());
        if( msg != null){
            return msg;
        }
        return null;
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
        if( msg != null){
            return msg;
        }
        return null;

    }

    private String checkAddress(String address) {
        if(address.length() < ADDRESS_LENGTH){
            return "Invalid address must be" + ADDRESS_LENGTH + "symbols";
        }
        return null;
    }

    private String checkPhone(String phone) {
        if(!phone.matches(PHONE_REGEX)){
            return "Invalid phone number";
        }
        return null;
    }

    private String checkConfirmPassword(String password,String confirmPassword) {
        if(!password.equals(confirmPassword)){
            return "Password don't match";
        }
        return null;
    }

    private String checkPassword(String password) {
        if(!password.matches(PASSWORD_REGEX)){
            return "Password must have one upper and lower letter , and one number and no spaces,must be 8 symbol";
        }
        return null;
    }

    private String checkLastName(String lastName) {
        if(!lastName.matches(NAME_REGEX)){
            return "Invalid last name";
        }
        return null;
    }

    private String checkFirstName(String firstName) {
        if(!firstName.matches(NAME_REGEX)){
            return "Invalid first name";
        }
        return null;
    }

    private String checkEmail(String email){
        if(!email.matches(EMAIL_REGEX)){
            return "Invalid email";
        }
        return null;
    }
}
